package library.db;

import library.db.*;
import library.model.*;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Table_Access<T extends Has_ID> {
	protected String table_name; 
	protected Connection connection; 
	protected String primary_key; 
	private static final Map<Class<? extends Table_Access<?>>, Table_Access<?>> instances = new HashMap<>();
	private Map<String, Method> columnGetterMap;
    private Map<String, Method> columnSetterMap;
    private List<String> schema; 
    private Class<T> type;
	
	
	
	@SuppressWarnings("unused")
	private Table_Access() {
	}
	
	protected Table_Access(Class<T> type) {
		this.type = type;
	}
	
	

	//Generic method to get or create a singleton instance for a given class with the requisite connection variable
	//This method acts is a helper function which acts as the primary constructor for all subclasses. 
	public static <T extends Table_Access<?>> T getInstance( Connection connection) throws SQLException {
		Class<T> callerClass = (Class<T>) getCallerClass();
		
		T instance = null; 
		synchronized (instances) {
			if (!instances.containsKey(callerClass)) {
				try {
					// Use reflection to create new instance of the class through the subclass
					instance = callerClass.getDeclaredConstructor().newInstance();
					instance.connection = connection; 
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
		DatabaseMetaData dbMeta = connection.getMetaData();
		try (var rs = dbMeta.getTables(null,  null,  this.table_name, null)){
			return rs.next();
		}
	}
	
	//creates the table within the database if it does not already exist. 
	protected void createTable() throws SQLException {
		List<String> schema = getTableSchema();
		StringBuilder createTableSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table_name + " (");
        for (int i = 0; i < schema.size(); i++) {
            createTableSQL.append(schema.get(i));
            if (i < schema.size() - 1) {
                createTableSQL.append(", ");
            }
        }
        createTableSQL.append(")");

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL.toString());
        }
    }
	
	// Helper method to create an instance of the generic type T
    protected T createEntityInstance() throws ReflectiveOperationException{
    	return type.getDeclaredConstructor().newInstance();
    	
    }
	
	protected Map<String, Method> getColumnGetterMap(){
		return columnGetterMap;
	}
	
    protected Map<String, Method> getColumnSetterMap(){
    	return columnSetterMap;
    }
    
    protected List<String> getTableSchema(){
    	return schema;
    }
    
	public String getTableName() {
		return this.table_name;
	}
	
	public String getPrimaryKey() {
		return this.primary_key;
	}
	
	public void delete(int Record_Id) throws SQLException{
		//delete record where the column this.primary_key matches Record_Id
		String sql = String.format("DELETE FROM %s WHERE %s = ?", this.getTableName(), this.getPrimaryKey()); 
		try(PreparedStatement stmt = connection.prepareStatement(sql)){
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
        String columns = String.join(", ", getters.keySet());
        String values = String.join(", ", getters.keySet().stream().map(col -> "?").toArray(String[]::new));

        String sql = "INSERT INTO " + table_name + " (" + columns + ") VALUES (" + values + ")";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int index = 1;
            for (String column : getters.keySet()) {
                Method getter = getters.get(column);
                try {
                    Object value = getter.invoke(entity);
                    stmt.setObject(index++, value);
                } catch (Exception e) {
                    throw new SQLException("Failed to invoke getter method for column: " + column, e);
                }
            }
            stmt.executeUpdate();
            
            Map<String, Method> setters = getColumnSetterMap();

            if (primary_key != null && setters.containsKey(primary_key)) { //if the primary key field is known, and available in the getters map
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {  //get the results from the insert operation
                    if (generatedKeys.next()) { 
                        Method setter = setters.get(primary_key); 
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
        String sql = "SELECT * FROM " + table_name + " WHERE " + primary_key + " = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    T entity = createEntityInstance();
                    Map<String, Method> setters = getColumnSetterMap();
                    for (String column : setters.keySet()) {
                        Method setter = setters.get(column);
                        try {
                            setter.invoke(entity, rs.getObject(column));
                        } catch (Exception e) {
                            throw new SQLException("Failed to invoke setter method for column: " + column, e);
                        }
                    }
                    return entity;
                }
            } catch (Exception e) {
                throw new SQLException("Failed to set fields via reflection", e);
            }
        }
        return null;
    }
	
	public List<T> readAll() throws SQLException {
        String sql = "SELECT * FROM " + table_name;
        Map<String, Method> setters = getColumnSetterMap();
        List<T> resultList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
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
        String sql = "UPDATE " + table_name + " SET ";

        Map<String, Method> getters = getColumnGetterMap();
        StringBuilder setClause = new StringBuilder();
        for (String column : getters.keySet()) {
            if (!column.equals(primary_key)) {
                if (setClause.length() > 0) {
                    setClause.append(", ");
                }
                setClause.append(column).append(" = ?");
            }
        }
        sql += setClause.toString() + " WHERE " + primary_key + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int index = 1;
            for (String column : getters.keySet()) {
                if (!column.equals(primary_key)) {
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

        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(table_name).append(" WHERE ");
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            sql.append(entry.getKey()).append(" = ? AND ");
            values.add(entry.getValue());
        }

        // Remove the trailing "AND "
        sql.setLength(sql.length() - 4);

        List<T> resultList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
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
	
	
	
	
		
	

}
