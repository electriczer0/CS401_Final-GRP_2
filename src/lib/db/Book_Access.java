package lib.db;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.db.*;
import lib.model.*;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public static Book_Access getInstance() {
    	return Table_Access.getInstance(Book_Access.class);
    }

    /**
     * Returns a user's favorite books by querying a join table 
     * @param userID The user in question
     * @return Map<Integer, Book> where Integer is the BookID. 
     * @throws SQLException
     */
    public Map<Integer, Book> getFavorites(int userID) throws SQLException{
    	String sql = "SELECT b.BookID, b.Title, b.Author "
                + "FROM Books b "
                + "INNER JOIN FavBooks fb ON b.BookID = fb.BookID "
                + "INNER JOIN Users u ON fb.UserID = u.UserID "
                + "WHERE u.UserID = ?";

     Map<Integer, Book> favoriteBooks = new HashMap<>();

     try (PreparedStatement stmt = connection.prepareStatement(sql)) {
         stmt.setInt(1, userID);
         try (ResultSet rs = stmt.executeQuery()) {
             while (rs.next()) {
                 int bookID = rs.getInt("BookID");
                 String title = rs.getString("Title");
                 String author = rs.getString("Author");
                 String ISBN = rs.getString("ISBN");
                 Book book = Book.create(bookID, author, ISBN, title);
                 favoriteBooks.put(bookID, book);
             }
         }
     } catch (SQLException e) {
         throw new SQLException("Failed to retrieve favorite books for user ID: " + userID, e);
     }

     return favoriteBooks;
 }
    	
    /**
     * Insert favorite books record into FavBooks join table
     * NOTE: This does NOT user the Books table. 
     * @param userID
     * @param bookID
     * @throws SQLException
     */
    public void addFavorite(int userID, int bookID) throws SQLException {
        String sql = "INSERT INTO FavBooks (UserID, BookID) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, bookID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Failed to add favorite book for user ID: " + userID + " and book ID: " + bookID, e);
        }
    }
    
    /**
     * Remove a favorite from the FavBooks join table based on userID and bookID
     * Will do nothing and report no errors if there are no records matching userID and bookID
     * @param userID
     * @param bookID
     * @throws SQLException
     */
    public void removeFavorite(int userID, int bookID) throws SQLException {
        String sql = "DELETE FROM FavBooks WHERE UserID = ? AND BookID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, bookID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Failed to remove favorite book for user ID: " + userID + " and book ID: " + bookID, e);
        }
    }
    
    /**
     * Returns a Map of recent books from the RecentBooks table
     * @param userID the User in question
     * @param days how days in the past to look
     * @return
     * @throws SQLException
     */
    public Map<Integer, Date> getRecentBooks(int userID, int days) throws SQLException {
        String sql = "SELECT BookID, Date "
                   + "FROM RecentBooks "
                   + "WHERE UserID = ? AND ActivityDate >= DATE('now', '-? days')";
        
        Map<Integer, Date> recentBooks = new HashMap<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, days);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int bookID = rs.getInt("BookID");
                    Date date = rs.getDate("Date");
                    recentBooks.put(bookID, date);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to retrieve recent books for user ID: " + userID, e);
        }

        return recentBooks;
    }

    /**
     * Add a book to the recent books list
     * @param userID the user to whom it should be added
     * @param bookID the book to add
     * @param date the date on which the book was accessed
     * @throws SQLException
     */
    public void addRecentBook(int userID, int bookID, Date date) throws SQLException {
        String sql = "INSERT INTO RecentBooks (UserID, BookID, ActivityDate) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, bookID);
            stmt.setDate(3, (java.sql.Date) date);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Failed to add recent book for user ID: " + userID + " and book ID: " + bookID, e);
        }
    }

    /**
     * Deletes the recent books records from RecentBooks table specified by params
     * @param userID the user in question
     * @param bookID the book in question
     * @param date the recency date
     * @return true if 1 or more records deleted else false
     * @throws SQLException
     */
    public boolean removeRecentBook(int userID, int bookID, Date date) throws SQLException {
        String sql = "DELETE FROM RecentBooks WHERE UserID = ? AND BookID = ? AND ActivityDate = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, bookID);
            stmt.setDate(3, (java.sql.Date) date);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                // No rows were affected, meaning no matching record was found
                return false;
            }
            return true;
        } catch (SQLException e) {
            throw new SQLException("Failed to remove recent book for user ID: " + userID + " and book ID: " + bookID, e);
        }
    }

    /**
     * Convenience function returns books from the last 30 days 
     * @param userID user in question
     * @return books accessed w/in the last 30 days
     * @throws SQLException
     */
    public Map<Integer, Date> getRecentBooks(int userID) throws SQLException{
    	return getRecentBooks(userID, 30);
    }


}
