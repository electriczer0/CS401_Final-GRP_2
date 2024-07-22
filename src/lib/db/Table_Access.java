package lib.db;

//TODO Make read() a call to find() 
//TODO reuse internal logic such as creating and setting entity fields from result set


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
/**
 * This abstract class defines a data base controller object
 * The basic CRUD functionality of a DB controller are defined in this class
 * While the table-specific configuration is defined in the sub-class
 * The sub-classes can also override common CRUD operations to define table-specific behavior
 * Or add new methods for different DB operations such as join table reading and writing
 * Each controller object should relate to one java class equivalent to the records represented 
 * in the table. 
 * Each controller object should be instantiated as a singleton. Table_Access
 * provides methods for establishing the instances of subclasses, and providing them 
 * through the getInstance() methods.  
 * @param <T>
 */
public abstract class Table_Access<T extends Has_ID> {

	/**
	 * The Table_Access super class enforces a singleton pattern by maintaining this
	 * Map between Class and Instance, wherein the class is a subclass of Table_Access
	 */
	private static final Map<Class<? extends Table_Access<?>>, Table_Access<?>> instances = new HashMap<>();
	/**
	 * a class representing the type of object the table instance accesses
	 */
	private Class<T> type;
	
    protected abstract List<String> getTableSchema();
    protected abstract Map<String, Method> getColumnGetterMap();
    protected abstract Map<String, Method> getColumnSetterMap();
    protected abstract Connection getConnection();
    protected abstract void setConnection(Connection connection);
    protected abstract String getTableName();
    protected abstract String getPrimaryKey();
	
	@SuppressWarnings("unused")
	private Table_Access() {	}
	
	/**
	 * Instantiates Table_Access controller
	 * @param type the class of the object being accessed. must extend Has_ID
	 */
	protected Table_Access(Class<T> type) {
		this.type = type;
		ensureFactoryExists();
	}
	
	/**
	 * This method is called at instantiation of Table_Access objects
	 * We are establishing object factories within the data model classes
	 * Because java has no interface model to enforce the presence of these static methods
	 * we instead use reflection to confirm their existence. 
	 * Our CRUD operations depend on these factories for object instantiation so they must exist 
	 */
	private void ensureFactoryExists() {
		
        try {
            Method method = type.getMethod("create");
            if (!Modifier.isStatic(method.getModifiers())) {
                throw new RuntimeException("The create method must be static in " + type.getName());
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("The create method must be present in " + type.getName(), e);
        }
    }

	/**
	 * Generic method to get or create a singleton instance for a given class with the requisite connection variable
	 * This method acts is a helper function which acts as the primary constructor for all subclasses. 
	 */
	public static <T extends Table_Access<?>> T getInstance( Connection connection, Class<T> clazz) throws SQLException {
		
		
		T instance = null; 
		synchronized (instances) {
			if (!instances.containsKey(clazz)) {
				try {
					// Use reflection to create new instance of the class through the subclass
					instance = clazz.getDeclaredConstructor().newInstance();
					instance.setConnection(connection); 
					instances.put(clazz, instance);
					
				} catch (Exception e) {
					throw new RuntimeException("Failed to create singleton instance for: " + clazz, e);
				} 
			} else {
				instance = clazz.cast(instances.get(clazz));
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
		
		assert instance != null: "Caught returning null instance of " + clazz; 
		return instance;
	}
	
	/**
	 * Public class to remove instances of Table_Access objects. 
	 * Needed, for example in testing when the database must be reinitialized
	 * Example usage Table_Access.removeInstance(Loan_Access.class)
	 * @param clazz a class of type <T extends Table_Access<?>> representing the concrete class to be removed
	 */
	public static void removeInstance(Class<? extends Table_Access<?>> clazz) {
		
		synchronized (instances){
			instances.remove(clazz);
		}
	}
	
	/**
	 * Used when the table instance is known to exist, returns a singleton of the Table_Access concrete class specified
	 * @param <T> a subclass of Table_Access
	 * @param callerClass the class whose instance should be returned
	 * @return a singleton instance of the class in question
	 */
	//Generic method to get the existing singleton instance for a given class
	public static <T extends Table_Access<?>> T getInstance(Class<T> callerClass) {
		
		synchronized(instances) {
			if(!instances.containsKey(callerClass)) {
				throw new RuntimeException("Instance not created yet for: " + callerClass);
			}
			return callerClass.cast(instances.get(callerClass));
		}
	}
	
/**
 * Deprecated
 * @return
 */
	private static Class<?> getCallerClass() {
        try {
            String callerClassName = Thread.currentThread().getStackTrace()[3].getClassName();
            return Class.forName(callerClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to determine caller class", e);
        }
    }
	
	/**
	 * helper function determines if the table already exists within the DB
	 * @return
	 * @throws SQLException
	 */
	protected boolean tableExists() throws SQLException{
		DatabaseMetaData dbMeta = getConnection().getMetaData();
		try (var rs = dbMeta.getTables(null,  null,  this.getTableName(), null)){
			return rs.next();
		}
	}
	
/**
 * Creates the table in the DB if it does not exist using the specified schema
 * @throws SQLException
 */
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
	
	/**
	 * Helper method creates blank instances of the class defined by type 
	 * used to load return data from DB 
	 * @return
	 * @throws ReflectiveOperationException
	 */
	protected T createEntityInstance(){
		try {
			Method method = type.getMethod("create");
			if (!Modifier.isStatic(method.getModifiers())) {
				throw new IllegalArgumentException("The create method in " + type.getName() + " is not static.");
			}
		
			return type.cast(method.invoke(null));
		} catch (Exception e) {
            throw new RuntimeException("Failed to create entity instance", e);
        }
    }
	
	/**
	 * Delete record where the column this.getPrimaryKey() matches Record_Id
	 * @param Record_Id the record primary key to delete
	 * @throws SQLException
	 */
	public void delete(int Record_Id) throws SQLException{
		String sql = String.format("DELETE FROM %s WHERE %s = ?", this.getTableName(), this.getPrimaryKey()); 
		try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
			stmt.setInt(1,  Record_Id);
			stmt.executeUpdate();
		}catch (SQLException e) {
            throw new SQLException("Failed to delete record with id: " + Record_Id, e);
        }
	}

	/**
	 * Convenience function to delete record. calls delete(int Record_Id)
	 * @param record the record to delete
	 * @throws SQLException
	 */
	public void delete(T record) throws SQLException{
		int Record_Id = record.getID();
		try {
			this.delete(Record_Id);
		}catch (SQLException e) {
            throw new SQLException("Failed to delete record with id: " + Record_Id, e);
        }
		
	}
	
	/**
	 * Inserts specified record into DB
	 * @param entity
	 * @throws SQLException
	 */
	public void insert(T entity) throws SQLException {
			
		Map<String, Method> getters = getColumnGetterMap();
	    Map<String, Method> setters = getColumnSetterMap();
	    
		 // Prepare the lists for columns and values, excluding the primary key if not set
	    StringBuilder columnsBuilder = new StringBuilder();
	    StringBuilder valuesBuilder = new StringBuilder();
	    
	    boolean first = true;

	    //Builds the SQL query statement. We need the column getters so that we can identify and extract
	    //the primary key value. If it is "null" then we do not include it in the insert statement. 
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
	        } catch (IllegalArgumentException e) {
	        	System.err.println("Caught IllegalArgumentException");
	        	System.err.println("Entity class: " + entity.getClass().getName());
                System.err.println("Getter declaring class: " + getter.getDeclaringClass().getName());
                throw new SQLException("Failed to invoke getter method for column: " + column, e);
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
	                
	                //TODO fix this 
	                //This is a hack to overwrite foreign keys which are set to -1 to "null" 
	                //So that we can enforce foreign key relationships when the fk is not set
	                //really we should set these foreign keys up as Integer values so that we 
	                //use null values in the object itself, but this would be a significant refactor. 
	                //this code could cause problems in int fields where -1 is a valid entry.
	                //currently the system does not have any such fields. 
	                if (value instanceof Integer && ((Integer) value).intValue() == -1) { 
                        // Replace -1 with NULL for integer values
                        stmt.setNull(index++, java.sql.Types.INTEGER);
                    } else {
                    	stmt.setObject(index++, value);
                    }
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
	
	/**
	 * Reads a single record from the table specified by its primary key
	 * @param recordId the int representing the primary key
	 * @return Object of type <T> found by primary key or NULL if not found
	 */
	public T read(int recordId) throws SQLException {
		
       HashMap<String,String> recordQuery = new HashMap<String, String>();
       recordQuery.put(getPrimaryKey(), String.valueOf(recordId));
       
       Map<Integer, T> recordsFound = find(recordQuery);
       
       return recordsFound.values().stream().findFirst().orElse(null);
    }
	
	/**
	 * Parses objects returned from a sql query translating them into 
	 * the desired java datatypes. Will process a variety of objects. 
	 * Logic exists for Boolean data types and Date objects
	 * Otherwise the method returns a typecast copy of the original Object
	 * @param paramType the java type which is expected
	 * @param value the Object which was returned by the db query 
	 * @return an object of type paramType representing the value parameter
	 */
	protected Object parseValue(Class<?> paramType, Object value) throws SQLException {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //Format of date string from SQLITE
		
		try {
            if (paramType == Boolean.class) {
                return value != null && ((Number) value).intValue() != 0;
            }
            if (paramType == Date.class) {
            	if (value instanceof String) {
	               	try {              	
	               		return dateFormat.parse((String) value);
	               	} catch(ParseException e) {
	               		e.printStackTrace();
	               	}
            	} else if (value instanceof Number) {
                    return new Date(((Number) value).longValue());
                }
            }
            if(paramType == int.class && value == null) { return (int) -1; } 
            if (paramType == int.class && value instanceof Integer) { return (int) value; }
            if (paramType == long.class && value instanceof Long) { return (long) value; }
            if (paramType == double.class && value instanceof Double) {return (double) value;}
            if (paramType == float.class && value instanceof Float) { return (float) value;}
            if (paramType.isEnum()) {
            	return Enum.valueOf((Class<Enum>) paramType, value.toString());
            }
            if (paramType == int.class && value instanceof String) {
            	if(((String) value).isEmpty()) return -1;
            	return (int) Integer.parseInt((String) value);
            };
            return paramType.cast(value);
        } catch (NumberFormatException e) {
            throw new SQLException("Failed to parse value: " + value, e);
        }
		
	}
	
	public Map<Integer, T> readAll(int offset, int limit) throws SQLException {
		/**
		 * Reads all records from the table limiting return to limit number and starting at offset'th record
		 * @param limit the maximum number of records to return
		 * @param offset the first record to return 
		 * @return Map<Integer, T> of records where key is the primarykey and value is the record 
		 */
		
        HashMap<String, String> blankQuery = new HashMap<>();
        return find(blankQuery, offset, limit);
	}
	
	/**
	 * Convenience method. Returns the first 100 records from table
	 */
	public Map<Integer, T> readAll() throws SQLException { return readAll(0,100); }
	
	/**
	 * Updates the record specified by record.getId() using the attributes of record
	 * @param record
	 * @throws SQLException
	 */
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
        
        Map<String, Object> debugValues = new HashMap<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            int index = 1;
            for (String column : getters.keySet()) {
                if (!column.equals(getPrimaryKey())) {
                    Method getter = getters.get(column);
                    try {
                        Object value = getter.invoke(record);
                        debugValues.put(column, value);
                        //TODO fix this 
    	                //This is a hack to overwrite foreign keys which are set to -1 to "null" 
    	                //So that we can enforce foreign key relationships when the fk is not set
    	                //really we should set these foreign keys up as Integer values so that we 
    	                //use null values in the object itself, but this would be a significant refactor. 
    	                //this code could cause problems in int fields where -1 is a valid entry.
    	                //currently the system does not have any such fields. 
    	                if (value instanceof Integer && ((Integer) value).intValue() == -1) { 
                            // Replace -1 with NULL for integer values
                            stmt.setNull(index++, java.sql.Types.INTEGER);
                        } else {
                        	stmt.setObject(index++, value);
                        }
                    } catch (Exception e) {
                        throw new SQLException("Failed to invoke getter method for column: " + column, e);
                    }
                }
            }
            stmt.setInt(index, recordId);
            int affectedRows = 0;
            try {
            	affectedRows = stmt.executeUpdate();
            } catch (SQLException e) {
            	throw new SQLException("Update Action failed for " + getTableName() +"\nWith Query: \n" + debugValues, e);
            }
            if (affectedRows == 0) {
                throw new SQLException("Record not found for update: " + recordId);
            }
        }
    }
	
	/**
	 * Find operation. Queries table and returns records found. Null parameters
	 * will return all records. 
	 * @param paramaters a HashMap<String, String> of key, value pairs representing
	 * the query column and search value
	 * @param limit the maximum records to return
	 * @param offset the first record to return 
	 * @return a Map<Integer, T> where key is the record ID, and value is the record 
	 */
	public Map<Integer, T> find(HashMap<String, String> parameters, int offset, int limit) throws SQLException {
        // Validate that each key in the parameters map matches a column in the table
        Map<String, Method> setters = getColumnSetterMap();
        for (String key : parameters.keySet()) {
            if (!setters.containsKey(key)) {
                throw new IllegalArgumentException("Invalid column name: " + key);
            }
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(getTableName());
        List<String> values = new ArrayList<>();
        
        if (parameters != null && !parameters.isEmpty()) {
        	sql.append(" WHERE ");
        }

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            sql.append(entry.getKey()).append(" = ? AND ");
            values.add(entry.getValue());
        }

        // Remove the trailing "AND "
        if (parameters != null && !parameters.isEmpty()) {
        	sql.setLength(sql.length() - 4);
        }
        
        sql.append(String.format(" LIMIT %d OFFSET %d", limit, offset));

        Map<Integer, T> resultMap = new HashMap<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setString(i + 1, values.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    T entity = createEntityInstance();
                    for (String column : setters.keySet()) {
                        Method setter = setters.get(column);
                        
                        Class<?> paramType = setter.getParameterTypes()[0];
                        Object value = parseValue(paramType, rs.getObject(column));
                        
                        try {
                            setter.invoke(entity, value);
                        } catch (Exception e) {
                            throw new SQLException("Failed to invoke setter method for column: " + column, e);
                        }
                    }
                    resultMap.put(entity.getID(), entity);
                }
            } catch (Exception e) {
                throw new SQLException("Failed to retrieve records via reflection", e);
            }
        }
        return resultMap;
    }
	
	/**
	 * Convenience function returns up to the first 100 records of the specified query 
	 * @param paramaters a HashMap<String, String> of key, value pairs representing
	 * the query column and search value
	 * @return a Map<Integer, T> where key is the record ID, and value is the record 
	 */
	public Map<Integer, T> find(HashMap<String, String> parameters) throws SQLException {return find(parameters, 0, 100);}
	
	/**
	 * Deprecated replaced by praseValue
	 * @param dateString
	 * @return
	 * @throws NumberFormatException
	 */
	private Date parseDate(String dateString) throws NumberFormatException {
		Date date = null;
		try { 
			long timeMillis = Long.parseLong((String) dateString);
			date = new Date(timeMillis);
			
		} catch (NumberFormatException e) {
            throw new NumberFormatException("Caught Exception parsing: " + dateString);
        }
		return date; 
	}
	
	@Override
	public String toString() {
		return this.getClass().toString();
	}

}
