package library.db;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Table_Access<T> {
	protected String table_name; 
	protected Connection connection; 
	protected String primary_key; 
	private static final Map<Class<? extends Table_Access<?>>, Table_Access<?>> instances = new HashMap<>();
	private final Map<String, Method> columnGetterMap;
    private final Map<String, Method> columnSetterMap;
    private final List<String> schema; 
	
	public abstract T get(int Record_Id) throws SQLException; //returns the record in table specified by Record_Id
	public abstract Vector<T> get() throws SQLException; //returns all records in table
	public abstract Vector<T> get(HashMap<String, String> args) throws SQLException; //Query database based on args which is specified as <column_name, query_string>
	public abstract void del(T record) throws SQLException; //delete the specified record from the database
	public abstract void initialize() throws SQLException;
	
	
	
	@SuppressWarnings("unused")
	protected Table_Access() {
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
		}
	}
	
	protected boolean tableExists() throws SQLException{
		DatabaseMetaData dbMeta = connection.getMetaData();
		try (var rs = dbMeta.getTables(null,  null,  this.table_name, null)){
			return rs.next();
		}
	}
	
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
	
	protected Map<String, Method> getColumnGetterMap(){
		return columnGetterMap;
	}
    protected Map<String, Method> getColumnSetterMap(){
    	return columnSetterMap;
    }
    
    protected abstract List<String> getTableSchema(){
    	return schema;
    }
	
	
		
	

}
