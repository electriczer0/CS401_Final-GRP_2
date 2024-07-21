package lib.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.model.*;

public class Group_Member_Access extends Table_Access<GroupMember> {

    protected final String primary_key = "ID"; // Set the primary key field for the Copy table
    protected final String table_name = "GroupMembers"; // Set the table name for the Copy table
    protected Connection connection; 
    private final List<String> schema =
			Arrays.asList(
					primary_key + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			                + "GroupID INTEGER NOT NULL, "
							+ "UserID INTEGER NOT NULL,"
			                + "FOREIGN KEY (UserID) REFERENCES Users(UserID),"
			                + "FOREIGN KEY (GroupID) REFERENCES SMGroups(GroupID) ON DELETE CASCADE" //DB will delete related Copy records on deletion of related book
					);
 
    // Maps for getter and setter methods
    private final Map<String, Method> columnGetterMap;
    private final Map<String, Method> columnSetterMap;
    
    //initialize the maps
    {
        columnGetterMap = new HashMap<>();
        columnSetterMap = new HashMap<>();
        try {
            columnGetterMap.put(primary_key, Copy.class.getMethod("getID"));
            columnGetterMap.put("GroupID", Copy.class.getMethod("getGroupId"));
            columnGetterMap.put("UserID", Copy.class.getMethod("getUserId"));

            columnSetterMap.put(primary_key, Copy.class.getMethod("setID", int.class));
            columnSetterMap.put("GroupID", Copy.class.getMethod("setGroupID", int.class));
            columnSetterMap.put("UserID", Copy.class.getMethod("setUserID", int.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize column getter/setter maps", e);
        }
    }

    // Private constructor to enforce singleton pattern
    protected Group_Member_Access() { super(GroupMember.class); }
    protected  List<String> getTableSchema(){ return this.schema; }
    protected  Map<String, Method> getColumnGetterMap(){ return this.columnGetterMap;}
    protected  Map<String, Method> getColumnSetterMap(){return this.columnSetterMap;}
    protected Connection getConnection() {	return this.connection;  }
    protected void setConnection(Connection connection) {this.connection = connection; }
    protected String getTableName() {return this.table_name;}
    protected String getPrimaryKey() {return this.primary_key;}
    public static Copy_Access getInstance() {
    	return Table_Access.getInstance(Copy_Access.class);
    }
}
