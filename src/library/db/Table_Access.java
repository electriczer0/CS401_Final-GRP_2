package library.db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;
import java.util.HashMap;

public abstract class Table_Access<T> {
	protected static String table_name; 
	protected Connection connection; 
	protected static String primary_key; 
	
	public abstract void add(T record) throws SQLException; //Adds record to database table
	public abstract T get(int Record_Id) throws SQLException; //returns the record in table specified by Record_Id
	public abstract Vector<T> get() throws SQLException; //returns all records in table
	public abstract Vector<T> get(HashMap<String, String> args) throws SQLException; //Query database based on args which is specified as <column_name, query_string>
	public abstract void del(T record) throws SQLException; //delete the specified record from the database
	public abstract void initialize() throws SQLException; 
	
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
	
	public Table_Access(Connection connection) {
		this.connection = connection;
		
	}
	
	
	@SuppressWarnings("unused")
	private Table_Access() {
		
	}

}
