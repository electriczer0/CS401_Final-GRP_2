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

public class SMInteraction_Access extends Table_Access<Interaction> {
	protected final String table_name = "SMInteractions";
	protected final String primary_key = "InteractionID";
	protected Connection connection; 
	private final List<String> schema =
			Arrays.asList(
					primary_key + " INTEGER PRIMARY KEY AUTOINCREMENT",
					"TargetID INTEGER NOT NULL",
					"UserID INTEGER NOT NULL",
					"GroupID INTEGER NOT NULL",
					"Content TEXT",
					"Type TEXT NOT NULL",
					"Timestamp DATE NOT NULL",
					"FOREIGN KEY (UserID) REFERENCES Users(UserID)",
					"FOREIGN KEY (GroupID) REFERENCES SMGroups(GroupID) ON DELETE CASCADE"
					);

	private final Map<String, Method> columnGetterMap = new HashMap<>();
    private final Map<String, Method> columnSetterMap = new HashMap<>();
	
   	
    //initialize column getter/setter maps so that generic methods from 
  	//abstract super class can be used. 
  	{
  		
          try {
              columnGetterMap.put(primary_key, Interaction.class.getMethod("getID"));
              columnGetterMap.put("UserID", Interaction.class.getMethod("getUserId"));
              columnGetterMap.put("TargetID", Interaction.class.getMethod("getTargetId"));
              columnGetterMap.put("Content", Interaction.class.getMethod("getContent"));
              columnGetterMap.put("Type", Interaction.class.getMethod("getType"));
              columnGetterMap.put("Timestamp", Interaction.class.getMethod("getTimestamp"));
              columnGetterMap.put("GroupID", Interaction.class.getMethod("getGroupId"));

              columnSetterMap.put(primary_key, Interaction.class.getMethod("setID", int.class));
              columnSetterMap.put("UserID", Interaction.class.getMethod("setUserId", int.class));
              columnSetterMap.put("TargetID", Interaction.class.getMethod("setTargetId", int.class));
              columnSetterMap.put("Content", Interaction.class.getMethod("setContent", String.class));
              columnSetterMap.put("Type", Interaction.class.getMethod("setType", Interaction.Interaction_Type.class));
              columnSetterMap.put("Timestamp", Interaction.class.getMethod("setTimestamp", Date.class));
              columnSetterMap.put("GroupID", Interaction.class.getMethod("setGroupId", int.class));
          } catch (NoSuchMethodException e) {
              throw new RuntimeException("Failed to initialize column getter/setter maps", e);
          }
      
  	}
	
	
	protected SMInteraction_Access() {
		super(Interaction.class);
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
    
    public static SMInteraction_Access getInstance() {
    	return Table_Access.getInstance(SMInteraction_Access.class);
    }
}