package lib.db;
//TODO Add methods to access Group / User joints from Meeting type

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
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
        String sql = "SELECT g.GroupID, g.Type, g.Description, g.Name, g.MeetingLocation, g.MeetingGroupID, g.MeetingTimestamp, g.Timestamp "
                   + "FROM SMGroups g "
                   + "INNER JOIN SMGroupMembers gm ON g.GroupID = gm.GroupID "
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

    public Map<Integer, User> getGroupMembers(int groupID) throws SQLException {
        String sql = "SELECT u.UserID, u.Username, u.OtherFields " // Replace u.OtherFields with actual fields
                   + "FROM Users u "
                   + "INNER JOIN SMGroupMembers gm ON u.UserID = gm.UserID "
                   + "WHERE gm.GroupID = ?";

        Map<Integer, User> groupMembers = new HashMap<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, groupID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userID = rs.getInt("UserID");
                    String firstName = rs.getString("NameFirst");
                    String lastName = rs.getString("NameLast");
                    String type = rs.getString("Type");
                    User user = User.create(userID, firstName, lastName, type);
                    groupMembers.put(userID, user);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to retrieve group members for group ID: " + groupID, e);
        }

        return groupMembers;
    }
}