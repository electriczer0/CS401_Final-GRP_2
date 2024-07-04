package library.db;

import library.db.*;
import library.model.*;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

public class Book_Access extends Table_Access<Book> {
	protected static final String table_name = "Book";
	protected static final String primary_key = "BookID";

	private Book_Access(Connection connection) {
		super();
		this.connection = connection;
	}
	
	//Get or create the instance with connection variable
	public static Book_Access getInstance(Connection connection) {
		return getInstance(Book_Access.class, connection);
	}
	
	//Get the existing instance; assuming it has been created
	public static Book_Access getInstance() {
		return getInstance(Book_Access.class);
	}
	

	@Override
	public void add(Book record) throws SQLException {
		// TODO Auto-generated method stub
		//Add Book record to DB. If book_Id is null, then update the Book 
		//record with the ID set by the DB
		
	}

	@Override
	public Book get(int Record_Id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<Book> get() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<Book> get(HashMap<String, String> args) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void del(Book record) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
