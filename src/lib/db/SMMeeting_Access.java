package lib.db;
//TODO overload DB methods to add a type filter on queries


import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.db.*;
import lib.model.*;

import java.lang.reflect.Method;
import java.sql.Connection;

public class SMMeeting_Access extends Table_Access<Meeting> {
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
					"MeetingTimestamp DATE NOT NULL",
					"Timestamp DATE NOT NULL",
					"FOREIGN KEY (OwnerID) REFERENCES Users(UserID)",
					"FOREIGN KEY (MeetingGroupID) REFERENCES SMGroups(GroupID) ON DELETE CASCADE"
					);

	private final Map<String, Method> columnGetterMap = new HashMap<>();
    private final Map<String, Method> columnSetterMap = new HashMap<>();
	
   	
    //initialize column getter/setter maps so that generic methods from 
  	//abstract super class can be used. 
  	{
  		
          try {
              columnGetterMap.put(primary_key, Meeting.class.getMethod("getID"));
              columnGetterMap.put("OwnerID", Meeting.class.getMethod("getOwnerId"));
              columnGetterMap.put("GroupID", Meeting.class.getMethod("getGroupId"));
              columnGetterMap.put("Description", Meeting.class.getMethod("getDescription"));
              columnGetterMap.put("Name", Meeting.class.getMethod("getName"));
              columnGetterMap.put("Timestamp", Meeting.class.getMethod("getTimestamp"));
              columnGetterMap.put("MeetingLocation", Meeting.class.getMethod("getMeetingLocation"));
              columnGetterMap.put("MeetingTimestamp", Meeting.class.getMethod("getMeetingTime"));

              columnSetterMap.put(primary_key, Meeting.class.getMethod("setID", int.class));
              columnSetterMap.put("OwnerID", Meeting.class.getMethod("setOwnerId", int.class));
              columnSetterMap.put("GroupID", Meeting.class.getMethod("setGroupId", int.class));
              columnSetterMap.put("Description", Meeting.class.getMethod("setDescription", String.class));
              columnSetterMap.put("Name", Meeting.class.getMethod("setName", String.class));
              columnSetterMap.put("Timestamp", Meeting.class.getMethod("setTimestamp", Date.class));
              columnSetterMap.put("MeetingLocation", Meeting.class.getMethod("setMeetingLocation", String.class));
              columnSetterMap.put("MeetingTimestamp", Meeting.class.getMethod("setMeetingTimestamp", Date.class));
              
              
          } catch (NoSuchMethodException e) {
              throw new RuntimeException("Failed to initialize column getter/setter maps", e);
          }
      
  	}
	
	
	protected SMMeeting_Access() {
		super(Meeting.class);
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
}