package library.db;

import library.db.*;
import library.model.*;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

public class Book_Access extends Table_Access<Book> {
	protected static String table_name = "Book";
	protected static String primary_key = "BookID";

	public Book_Access(Connection connection) {
		super(connection);
	}

	@Override
	public void add(Book record) throws SQLException {
		// TODO Auto-generated method stub
		
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
