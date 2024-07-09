package library.db;

import library.db.*;
import library.model.*;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.lang.reflect.Method;

public class Book_Access extends Table_Access<Book> {
	protected final String table_name = "Book";
	protected final String primary_key = "BookID";
	private final List<String> schema =
			Arrays.asList(
					primary_key + " INTEGER PRIMARY KEY AUTOINCREMENT",
					"Author TEXT NOT NULL",
					"ISBN TEXT CHECK(length(ISBN) = 10 OR length(ISBN) = 13) NOT NULL",
					"Title TEXT NOT NULL"
					);

	private final Map<String, Method> columnGetterMap = new HashMap<>();
    private final Map<String, Method> columnSetterMap = new HashMap<>();
	
   	
	
	
	
	@SuppressWarnings("unused")
	private Book_Access() {
		super();
	}
	
	//initialize column getter/setter maps so that generic methods from 
	//abstract super class can be used. 
	{
		
        try {
            columnGetterMap.put(primary_key, Book.class.getMethod("getID"));
            columnGetterMap.put("Author", Book.class.getMethod("getAuthor"));
            columnGetterMap.put("ISBN", Book.class.getMethod("getISBN"));
            columnGetterMap.put("Title", Book.class.getMethod("getTitle"));

            columnSetterMap.put(primary_key, Book.class.getMethod("setID", Integer.class));
            columnSetterMap.put("Author", Book.class.getMethod("setAuthor", String.class));
            columnSetterMap.put("ISBN", Book.class.getMethod("setISBN", String.class));
            columnSetterMap.put("Title", Book.class.getMethod("setTitle", String.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize column getter/setter maps", e);
        }
    
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
	
	protected List<String> getTableSchema(){
		return this.schema;
	}
	

}
