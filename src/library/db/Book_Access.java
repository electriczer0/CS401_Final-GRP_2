package library.db;

import library.db.*;
import library.model.*;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.lang.reflect.Method;
import java.sql.Connection;

public class Book_Access extends Table_Access<Book> {
	protected final String table_name = "Book";
	protected final String primary_key = "BookID";
	protected Connection connection; 
	private final List<String> schema =
			Arrays.asList(
					primary_key + " INTEGER PRIMARY KEY AUTOINCREMENT",
					"Author TEXT NOT NULL",
					"ISBN TEXT CHECK(length(ISBN) = 10 OR length(ISBN) = 13) NOT NULL",
					"Title TEXT NOT NULL"
					);

	private final Map<String, Method> columnGetterMap = new HashMap<>();
    private final Map<String, Method> columnSetterMap = new HashMap<>();
	
   	
    //initialize column getter/setter maps so that generic methods from 
  	//abstract super class can be used. 
  	{
  		
          try {
              columnGetterMap.put(primary_key, Book.class.getMethod("getID"));
              columnGetterMap.put("Author", Book.class.getMethod("getAuthor"));
              columnGetterMap.put("ISBN", Book.class.getMethod("getISBN"));
              columnGetterMap.put("Title", Book.class.getMethod("getTitle"));

              columnSetterMap.put(primary_key, Book.class.getMethod("setID", int.class));
              columnSetterMap.put("Author", Book.class.getMethod("setAuthor", String.class));
              columnSetterMap.put("ISBN", Book.class.getMethod("setISBN", String.class));
              columnSetterMap.put("Title", Book.class.getMethod("setTitle", String.class));
          } catch (NoSuchMethodException e) {
              throw new RuntimeException("Failed to initialize column getter/setter maps", e);
          }
      
  	}
	
	
	protected Book_Access() {
		super(Book.class);
	}
	
	protected  List<String> getTableSchema(){
		return this.schema;
	}
    protected  Map<String, Method> getColumnGetterMap(){
    	return this.columnGetterMap; 
    }
    protected  Map<String, Method> getColumnSetterMap(){
    	return this.columnSetterMap;
    }
    protected Connection getConnection() {
    	return this.connection;
    }
    protected void setConnection(Connection connection) {
    	this.connection = connection;
    }
    protected String getTableName() {
    	return this.table_name;
    }
    protected String getPrimaryKey() {
    	return this.primary_key;
    }
}
