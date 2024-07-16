package lib.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.db.*;
import lib.model.*;

public abstract class Table_Access<T extends Has_ID> {

	private static final Map<Class<? extends Table_Access<?>>, Table_Access<?>> instances = new HashMap<>();
	private Class<T> type;
	
    protected abstract List<String> getTableSchema();
    protected abstract Map<String, Method> getColumnGetterMap();
    protected abstract Map<String, Method> getColumnSetterMap();
    protected abstract Connection getConnection();
    protected abstract void setConnection(Connection connection);
    protected abstract String getTableName();
    protected abstract String getPrimaryKey();

	
	
	@SuppressWarnings("unused")
	private Table_Access() {
	}
	
	protected Table_Access(Class<T> type) {
		this.type = type; 
	}
	
	

	//Generic method to get or create a singleton instance for a given class with the requisite connection variable
	//This method acts is a helper function which acts as the primary constructor for all subclasses. 
	public static <T extends Table_Access<?>> T getInstance( Connection connection, Class<T> callerClass) throws SQLException {
		//Class<T> callerClass = (Class<T>) getCallerClass();
		
		T instance = null; 
		synchronized (instances) {
			if (!instances.containsKey(callerClass)) {
				try {
					// Use reflection to create new instance of the class through the subclass
					instance = callerClass.getDeclaredConstructor().newInstance();
					instance.setConnection(connection); 
					instances.put(callerClass, instance);
					
				} catch (Exception e) {
					throw new RuntimeException("Failed to create singleton instance for: " + callerClass, e);
				} 
			} else {
				instance = callerClass.cast(instances.get(callerClass));
			}
		}
	
		//Check if table exists in the database. If not, create the table. 
		try {
			if(!instance.tableExists()) {
				instance.createTable();
			} 
		}catch (SQLException e) {
			throw new SQLException("Failed to check or create the table " + instance.getTableName() , e);
		}

		return instance;
	}
	
	
	//Generic method to get the existing singleton instance for a given class
	public static <T extends Table_Access<?>> T getInstance() {
		Class<T> callerClass = (Class<T>) getCallerClass();
		
		synchronized(instances) {
			if(!instances.containsKey(callerClass)) {
				throw new RuntimeException("Instance not created yet for: " + callerClass);
			}
			return callerClass.cast(instances.get(callerClass));
		}
	}
	
	//Determine the concrete class that called this method
	private static Class<?> getCallerClass() {
        try {
            String callerClassName = Thread.currentThread().getStackTrace()[3].getClassName();
            return Class.forName(callerClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to determine caller class", e);
        }
    }
	//Checks if this table already exists in the database
	protected boolean tableExists() throws SQLException{
		DatabaseMetaData dbMeta = getConnection().getMetaData();
		try (var rs = dbMeta.getTables(null,  null,  this.getTableName(), null)){
			return rs.next();
		}
	}
	
	//creates the table within the database if it does not already exist. 
	protected void createTable() throws SQLException {
		List<String> schema = getTableSchema();
		StringBuilder createTableSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS " + getTableName() + " (");
        for (int i = 0; i < schema.size(); i++) {
            createTableSQL.append(schema.get(i));
            if (i < schema.size() - 1) {
                createTableSQL.append(", ");
            }
        }
        createTableSQL.append(")");

        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(createTableSQL.toString());
        }catch (SQLException e) {
            throw new SQLException("Failed to create table with statement: " + createTableSQL.toString(), e);
        }
    }
	
	// Helper method to create an instance of the generic type T
    protected T createEntityInstance() throws ReflectiveOperationException{
    	return type.getDeclaredConstructor().newInstance();
    	
    }
	
    
	
	
	
	
	public void delete(int Record_Id) throws SQLException{
		//delete record where the column this.getPrimaryKey() matches Record_Id
		String sql = String.format("DELETE FROM %s WHERE %s = ?", this.getTableName(), this.getPrimaryKey()); 
		try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
			stmt.setInt(1,  Record_Id);
			stmt.executeUpdate();
		}catch (SQLException e) {
            throw new SQLException("Failed to delete record with id: " + Record_Id, e);
        }
	}
	
	public void delete(T record) throws SQLException{
		int Record_Id = record.getID();
		try {
			this.delete(Record_Id);
		}catch (SQLException e) {
            throw new SQLException("Failed to delete record with id: " + Record_Id, e);
        }
		
	}
	
	
	
	//Uses getter/setter maps from the subclass to add a record to the DB
	public void insert(T entity) throws SQLException {
		
	
		Map<String, Method> getters = getColumnGetterMap();
	    Map<String, Method> setters = getColumnSetterMap();
	    
		 // Prepare the lists for columns and values, excluding the primary key if not set
	    StringBuilder columnsBuilder = new StringBuilder();
	    StringBuilder valuesBuilder = new StringBuilder();
	    
	    boolean first = true;

	    for (String column : getters.keySet()) {
	        Method getter = getters.get(column);
	        try {
	            Object value = getter.invoke(entity);
	            if (column.equals(getPrimaryKey()) && (value == null || ((int) value == -1))) {
	                continue; // Skip the primary key if it is not set
	            }
	            if (!first) {
	                columnsBuilder.append(", ");
	                valuesBuilder.append(", ");
	            }
	            columnsBuilder.append(column);
	            valuesBuilder.append("?");
	            first = false;
	        } catch (Exception e) {
	            throw new SQLException("Failed to invoke getter method for column: " + column, e);
	        }
	    }

	    String columns = columnsBuilder.toString();
	    String values = valuesBuilder.toString();
	    
	    String sql = "INSERT INTO " + getTableName() + " (" + columns + ") VALUES (" + values + ")";
	    try (PreparedStatement stmt = getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
	        int index = 1;
	        for (String column : getters.keySet()) {
	            Method getter = getters.get(column);
	            try {
	                Object value = getter.invoke(entity);
	                if (column.equals(getPrimaryKey()) && (value == null || (value instanceof Integer && (int) value == -1))) {
	                    continue; // Skip the primary key if it is not set
	                }
	                stmt.setObject(index++, value);
	            } catch (Exception e) {
	            	
	            	throw new SQLException("Failed to invoke getter method for column: " + column, e);
	                
	            }
	        }
	        stmt.executeUpdate();

	        // Update the primary key in the entity if it was generated by the database
	        if (getPrimaryKey() != null && setters.containsKey(getPrimaryKey())) {
	            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    Method setter = setters.get(getPrimaryKey());
	                    if (setter != null) {
	                        setter.invoke(entity, generatedKeys.getInt(1));
	                    }
	                }
	            } catch (Exception e) {
	                throw new SQLException("Failed to set generated primary key", e);
	            }
	        }
	    } 
}
	
	public T read(int recordId) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getPrimaryKey() + " = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    T entity = createEntityInstance();
                    Map<String, Method> setters = getColumnSetterMap();
                    
                    
                    for (Map.Entry<String, Method> entry : setters.entrySet()) {
                        String columnName = entry.getKey();
                        Method setter = entry.getValue();
                        Class<?> paramType = setter.getParameterTypes()[0];
                        
                        Object value = rs.getObject(columnName);
                        
                        //convert Boolean from DB to java
                        if (paramType == Boolean.class) {
                        	value = rs.getInt(columnName) != 0;
                        }
                        
                        //convert Date formats from DB to java
                        if (paramType == Date.class) { 
                        		value = parseDate(rs.getString(columnName));
                        }
                        
                        
                        try {
                        	setter.invoke(entity, value);
                        	
                        } catch (Exception e) {
                            throw new SQLException("Failed to invoke setter method for column: " + columnName, e);
                        }
                        
                    }
                    /*for (String column : setters.keySet()) {
                    	
                        Method setter = setters.get(column);
                        try {
                            setter.invoke(entity, rs.getObject(column));
                        } catch (Exception e) {
                            throw new SQLException("Failed to invoke setter method for column: " + column, e);
                        }
                    }*/
                    
                    return entity;
                }
            } catch (Exception e) {
                throw new SQLException("Failed to set fields via reflection", e);
            }
        }
        return null;
    }
	
	public List<T> readAll() throws SQLException {
        String sql = "SELECT * FROM " + getTableName();
        Map<String, Method> setters = getColumnSetterMap();
        List<T> resultList = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                T entity = createEntityInstance();
                for (String column : setters.keySet()) {
                    Method setter = setters.get(column);
                    try {
                        setter.invoke(entity, rs.getObject(column));
                    } catch (Exception e) {
                        throw new SQLException("Failed to invoke setter method for column: " + column, e);
                    }
                }
                resultList.add(entity);
            }
        } catch (Exception e) {
            throw new SQLException("Failed to retrieve all records", e);
        }
        return resultList;
    }
	
	
	public void update(T record) throws SQLException {
        int recordId = record.getID();
        String sql = "UPDATE " + getTableName() + " SET ";

        Map<String, Method> getters = getColumnGetterMap();
        StringBuilder setClause = new StringBuilder();
        for (String column : getters.keySet()) {
            if (!column.equals(getPrimaryKey())) {
                if (setClause.length() > 0) {
                    setClause.append(", ");
                }
                setClause.append(column).append(" = ?");
            }
        }
        sql += setClause.toString() + " WHERE " + getPrimaryKey() + " = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            int index = 1;
            for (String column : getters.keySet()) {
                if (!column.equals(getPrimaryKey())) {
                    Method getter = getters.get(column);
                    try {
                        Object value = getter.invoke(record);
                        stmt.setObject(index++, value);
                    } catch (Exception e) {
                        throw new SQLException("Failed to invoke getter method for column: " + column, e);
                    }
                }
            }
            stmt.setInt(index, recordId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Record not found for update: " + recordId);
            }
        }
    }
	
	public List<T> find(HashMap<String, String> parameters) throws SQLException {
        if (parameters == null || parameters.isEmpty()) {
            throw new IllegalArgumentException("Parameters cannot be null or empty");
        }

        // Validate that each key in the parameters map matches a column in the table
        Map<String, Method> setters = getColumnSetterMap();
        for (String key : parameters.keySet()) {
            if (!setters.containsKey(key)) {
                throw new IllegalArgumentException("Invalid column name: " + key);
            }
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(getTableName()).append(" WHERE ");
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            sql.append(entry.getKey()).append(" = ? AND ");
            values.add(entry.getValue());
        }

        // Remove the trailing "AND "
        sql.setLength(sql.length() - 4);

        List<T> resultList = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setString(i + 1, values.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    T entity = createEntityInstance();
                    for (String column : setters.keySet()) {
                        Method setter = setters.get(column);
                        try {
                            setter.invoke(entity, rs.getObject(column));
                        } catch (Exception e) {
                            throw new SQLException("Failed to invoke setter method for column: " + column, e);
                        }
                    }
                    resultList.add(entity);
                }
            } catch (Exception e) {
                throw new SQLException("Failed to retrieve records via reflection", e);
            }
        }
        return resultList;
    }
	
	private Date parseDate(String dateString) throws NumberFormatException {
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//return dateFormat.parse(dateString);
		Date date = null;
		try { 
			long timeMillis = Long.parseLong((String) dateString);
			date = new Date(timeMillis);
			
		} catch (NumberFormatException e) {
            throw new NumberFormatException("Caught Exception parsing: " + dateString);
        }
		return date; 
	}
	
	
	
		
	

}
