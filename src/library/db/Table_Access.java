package library.db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;
import java.util.HashMap;
import java.util.Map;

public abstract class Table_Access<T> {
	protected String table_name; 
	protected Connection connection; 
	protected String primary_key; 
	private static final Map<Class<? extends Table_Access<?>>, Table_Access<?>> instances = new HashMap<>();
	
	public abstract void add(T record) throws SQLException; //Adds record to database table
	public abstract T get(int Record_Id) throws SQLException; //returns the record in table specified by Record_Id
	public abstract Vector<T> get() throws SQLException; //returns all records in table
	public abstract Vector<T> get(HashMap<String, String> args) throws SQLException; //Query database based on args which is specified as <column_name, query_string>
	public abstract void del(T record) throws SQLException; //delete the specified record from the database
	public abstract void initialize() throws SQLException; 
	
	protected Table_Access() {
		
	}
	
	//Generic method to get or create a singleton instance for a given class with the requisite connection variable
	protected static <T extends Table_Access<?>> T getInstance(Class<T> clazz, Connection connection) {
		synchronized (instances) {
			if (!instances.containsKey(clazz)) {
				try {
					// Use reflection to create new instance of the class through the subclass
					T instance = clazz.getDeclaredConstructor(connection.getClass()).newInstance(connection);
					instances.put(clazz, instance);
				} catch (Exception e) {
					throw new RuntimeException("Failed to create singleton instance for: " + clazz, e);
				}
			}
			return clazz.cast(instances.get(clazz));
		}
	}
	
	//Generic method to get the existing singleton instance for a given class
	protected static <T extends Table_Access<?>> T getInstance(Class<T> clazz) {
		synchronized(instances) {
			if(!instances.containsKey(clazz)) {
				throw new RuntimeException("Instance not created yet for: " + clazz);
			}
			return clazz.cast(instances.get(clazz));
		}
	}
	
	
	public String getTableName() {
		return this.table_name;
	}
	
	public String getPrimaryKey() {
		return this.primary_key;
	}
	
	public void del(int Record_Id) throws SQLException{
		//delete record where the column this.primary_key matches Record_Id
		String sql = String.format("DELETE FROM %s WHERE %s = ?", this.getTableName(), this.getPrimaryKey()); 
		try(PreparedStatement stmt = connection.prepareStatement(sql)){
			stmt.setInt(1,  Record_Id);
			stmt.executeUpdate();
		}
		
	}
	

	
	
	
		
	

}
