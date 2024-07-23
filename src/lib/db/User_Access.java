package lib.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.model.*;

public class User_Access extends Table_Access<User> {

    protected final String primary_key = "UserID"; // Set the primary key field for the User table
    protected final String table_name = "Users"; // Set the table name for the User table
    protected Connection connection;
    private final List<String> schema =
            Arrays.asList(
                    primary_key + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NameFirst TEXT, "
                    + "NameLast TEXT, "
                    + "Type TEXT"
            );
 
    // Maps for getter and setter methods
    private final Map<String, Method> columnGetterMap;
    private final Map<String, Method> columnSetterMap;
    
    //initialize the maps
    {
        columnGetterMap = new HashMap<>();
        columnSetterMap = new HashMap<>();
        try {
            columnGetterMap.put("UserID", User.class.getMethod("getID"));
            columnGetterMap.put("NameFirst", User.class.getMethod("getFirstName"));
            columnGetterMap.put("NameLast", User.class.getMethod("getLastName"));
            columnGetterMap.put("Type", User.class.getMethod("getType"));

            columnSetterMap.put("UserID", User.class.getMethod("setID", int.class));
            columnSetterMap.put("NameFirst", User.class.getMethod("setFirstName", String.class));
            columnSetterMap.put("NameLast", User.class.getMethod("setLastName", String.class));
            columnSetterMap.put("Type", User.class.getMethod("setType", String.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize column getter/setter maps", e);
        }
    }

    // Private constructor to enforce singleton pattern
    protected User_Access() {
        super(User.class);
    }

    //Getter / Setters
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
    public static User_Access getInstance() {
    	return Table_Access.getInstance(User_Access.class);
    }


    /**
     * Add a user to a group by creating a record in SMGroupMembers
     * @param userID
     * @param groupID
     * @throws SQLException
     */
    public void addGroup(int userID, int groupID) throws SQLException {
        String sql = "INSERT INTO SMGroupMembers (UserID, GroupID) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, groupID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Failed to add group for user ID: " + userID + " and group ID: " + groupID, e);
        }
    }

    /**
     * Return groups associated with a user
     * @param userID
     * @return a Map<Integer, Group> where Integer is the GroupID
     * @throws SQLException
     */
    public Map<Integer, Group> getGroups(int userID) throws SQLException {
        String sql = "SELECT g.GroupID, g.OwnerID, g.Type, g.Description, g.Name, g.Timestamp "
                   + "FROM SMGroups g "
                   + "INNER JOIN SMGroupMembers gm ON g.GroupID = gm.GroupID "
                   + "INNER JOIN SMGroups grp ON gm.GroupID = grp.GroupID AND grp.Type='Group' "
                   + "WHERE gm.UserID = ?";

        Map<Integer, Group> groups = new HashMap<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int groupID = rs.getInt("GroupID");
                    int ownerID = rs.getInt("OwnerID");
                    String type = rs.getString("Type");
                    String description = rs.getString("Description");
                    String name = rs.getString("Name");
                    java.sql.Date timestamp = rs.getDate("Timestamp");

                    Group group = Group.create(groupID, ownerID, name, description, timestamp);
                    group.setType(type);
                    groups.put(groupID, group);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to retrieve groups for user ID: " + userID, e);
        }

        return groups;
    }
    
    /**
     * Return meetings associated with a user
     * @param userID
     * @return a Map<Integer, Group> where Integer is the GroupID
     * @throws SQLException
     */
    public Map<Integer, Meeting> getMeetings(int userID) throws SQLException {
        String sql = "SELECT g.GroupID, g.OwnerID, g.Type, g.Description, g.Name, g.MeetingLocation, g.MeetingGroupID, g.MeetingTimestamp, g.Timestamp "
                   + "FROM SMGroups g "
                   + "INNER JOIN SMGroupMembers gm ON g.GroupID = gm.GroupID "
                   + "INNER JOIN SMGroups grp ON gm.GroupID = grp.GroupID AND grp.Type='Meeting' "
                   + "WHERE gm.UserID = ?";

        Map<Integer, Meeting> meetings = new HashMap<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int groupID = rs.getInt("GroupID");
                    int ownerID = rs.getInt("OwnerID");
                    String type = rs.getString("Type");
                    String description = rs.getString("Description");
                    String name = rs.getString("Name");
                    String meetingLocation = rs.getString("MeetingLocation");
                    int meetingGroupID = rs.getInt("MeetingGroupID");
                    java.sql.Date meetingTimestamp = rs.getDate("MeetingTimestamp");
                    java.sql.Date timestamp = rs.getDate("Timestamp");

                    Meeting meeting = Meeting.create(groupID, ownerID, meetingGroupID, name, description, meetingLocation, meetingTimestamp, timestamp);
                    meeting.setType(type);
                    meetings.put(groupID, meeting);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to retrieve groups for user ID: " + userID, e);
        }

        return meetings;
    }

    /**
     * Removes a user's group affiliation be deleting from SMGroupMembers
     * @param userID
     * @param groupID
     * @throws SQLException
     */
    public void remGroup(int userID, int groupID) throws SQLException {
        String sql = "DELETE FROM SMGroupMembers WHERE UserID = ? AND GroupID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, groupID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Failed to remove group for user ID: " + userID + " and group ID: " + groupID, e);
        }
    }

    /**
     * Returns a user's favorite books by querying a join table 
     * @param userID The user in question
     * @return Map<Integer, Book> where Integer is the BookID. 
     * @throws SQLException
     */
    public Map<Integer, Book> getFavorites(int userID) throws SQLException{
    	String sql = "SELECT b.BookID, b.Title, b.Author, b.ISBN "
                + "FROM Books b "
                + "INNER JOIN FavBooks fb ON b.BookID = fb.BookID "
                + "WHERE fb.UserID = ?";

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
     * Returns a list of all active loans associated with the userID
     * @param userID
     * @return
     * @throws SQLException
     */
    public Map<Integer, Loan> getActiveLoans(int userID) throws SQLException{
    	String sql = "SELECT LoanID, CopyID, UserID, DateOut, DateDue, IsActive "
                + "FROM Loans "
                + "WHERE UserID = ? AND IsActive = TRUE";

     Map<Integer, Loan> activeLoans = new HashMap<>();

     try (PreparedStatement stmt = connection.prepareStatement(sql)) {
         stmt.setInt(1, userID);
         try (ResultSet rs = stmt.executeQuery()) {
             while (rs.next()) {
                 int loanID = rs.getInt("LoanID");
                 int copyID = rs.getInt("CopyID");
                 int id = rs.getInt("UserID");
                 Date dateOut = rs.getDate("DateOut");
                 Date dateDue = rs.getDate("DateDue");
                 boolean isActive = rs.getBoolean("IsActive");
                 Loan loan = Loan.create(loanID, copyID, id, dateOut, dateDue, isActive);
                 activeLoans.put(loanID, loan);
             }
         }
     } catch (SQLException e) {
         throw new SQLException("Failed to retrieve favorite loans for user ID: " + userID, e);
     }

     return activeLoans;
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
    public Map<Integer, Book> getRecentBooks(int userID, int days) throws SQLException {
        String sql = "SELECT b.BookID, b.Title, b.Author, b.ISBN"
        		   + "FROM Books b"
        		   + "INNER JOIN RecentBooks rb ON b.BookID = rb.BookID"
                   + "WHERE rb.UserID = ? AND rb.ActivityDate >= DATE('now', '-? days')";
        
        Map<Integer, Book> recentBooks = new HashMap<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, days);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int bookID = rs.getInt("BookID");
                    String title = rs.getString("Title");
                    String author = rs.getString("Author");
                    String isbn = rs.getString("ISBN");
                    Book book = Book.create(bookID, author, isbn, title);
                    recentBooks.put(bookID, book);
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
    public Map<Integer, Book> getRecentBooks(int userID) throws SQLException{
    	return getRecentBooks(userID, 30);
    }


}