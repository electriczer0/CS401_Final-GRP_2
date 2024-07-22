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

public class SMGroup_Access extends Table_Access<Group> {
	protected final String table_name = "SMGroups";
	protected final String primary_key = "GroupID";
	protected Connection connection; 
	private final List<String> schema =
			Arrays.asList(
					primary_key + " INTEGER PRIMARY KEY AUTOINCREMENT",
					"OwnerID INTEGER NOT NULL",
					"Type TEXT NOT NULL",
					"Description TEXT",
					"Name TEXT NOT NULL",
					"MeetingLocation TEXT",
					"MeetingGroupID INTEGER",
					"MeetingTimestamp DATETIME",
					"Timestamp DATETIME NOT NULL",
					"FOREIGN KEY (OwnerID) REFERENCES Users(UserID)"
					);

	private final Map<String, Method> columnGetterMap = new HashMap<>();
    private final Map<String, Method> columnSetterMap = new HashMap<>();
	
   	
    //initialize column getter/setter maps so that generic methods from 
  	//abstract super class can be used. 
  	{
  		
          try {
              columnGetterMap.put(primary_key, Group.class.getMethod("getID"));
              columnGetterMap.put("OwnerID", Group.class.getMethod("getOwnerId"));
              columnGetterMap.put("Description", Group.class.getMethod("getDescription"));
              columnGetterMap.put("Name", Group.class.getMethod("getName"));
              columnGetterMap.put("Timestamp", Group.class.getMethod("getTimestamp"));
              columnGetterMap.put("Type", Group.class.getMethod("getType"));

              columnSetterMap.put(primary_key, Group.class.getMethod("setID", int.class));
              columnSetterMap.put("OwnerID", Group.class.getMethod("setOwnerId", int.class));
              columnSetterMap.put("Description", Group.class.getMethod("setDescription", String.class));
              columnSetterMap.put("Name", Group.class.getMethod("setName", String.class));
              columnSetterMap.put("Timestamp", Group.class.getMethod("setTimestamp", Date.class));
              columnSetterMap.put("Type", Group.class.getMethod("setType", String.class));
          } catch (NoSuchMethodException e) {
              throw new RuntimeException("Failed to initialize column getter/setter maps", e);
          }
      
  	}
	
	
	protected SMGroup_Access() {
		super(Group.class);
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
    
    public static SMGroup_Access getInstance() {
    	return Table_Access.getInstance(SMGroup_Access.class);
    }

    @Override
    public Map<Integer, Group> find(HashMap<String, String> searchParams, int offset, int limit) throws SQLException{
    	/**
		 * Find operation. Queries table and returns records found. Null parameters
		 * will return all records. 
		 * @param searchParams a HashMap<String, String> of key, value pairs representing
		 * the query column and search value. This method automatically adds Type=Group
		 * @param limit the maximum records to return
		 * @param offset the first record to return 
		 * @return a Map<Integer, T> where key is the record ID, and value is the record 
    	 */
    	searchParams.put("Type", "Group");
    	return super.find(searchParams, offset, limit);
    	
    }
    
    public Map<Integer, User> getGroupMembers(int groupID) throws SQLException {
        String sql = "SELECT u.UserID, u.NameFirst, u.NameLast, u.Type "
                   + "FROM Users u "
                   + "INNER JOIN SMGroupMembers gm ON u.UserID = gm.UserID "
                   + "INNER JOIN SMGroups grp ON gm.GroupID = grp.GroupID AND grp.Type='Group' "
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

    /**
     * Add a user to a group by creating a record in SMGroupMembers
     * @param userID
     * @param groupID
     * @throws SQLException
     */
    public void addUser(int userID, int groupID) throws SQLException {
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
     * Removes a user's group affiliation be deleting from SMGroupMembers
     * @param userID
     * @param groupID
     * @throws SQLException
     */
    public void remUser(int userID, int groupID) throws SQLException {
        String sql = "DELETE FROM SMGroupMembers WHERE UserID = ? AND GroupID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, groupID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Failed to remove group for user ID: " + userID + " and group ID: " + groupID, e);
        }
    }

    


    
    
}