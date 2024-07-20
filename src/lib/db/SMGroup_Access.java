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

public class SMGroup_Access extends Table_Access<Group> {
	protected final String table_name = "SMGroups";
	protected final String primary_key = "GroupID";
	protected Connection connection; 
	private final List<String> schema =
			Arrays.asList(
					primary_key + " INTEGER PRIMARY KEY AUTOINCREMENT",
					"OwnerID INTEGER NOT NULL",
					"Description TEXT",
					"Name TEXT NOT NULL",
					"Timestamp DATE NOT NULL",
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

              columnSetterMap.put(primary_key, Group.class.getMethod("setID", int.class));
              columnSetterMap.put("OwnerID", Group.class.getMethod("setOwnerId", int.class));
              columnSetterMap.put("Description", Group.class.getMethod("setDescription", String.class));
              columnSetterMap.put("Name", Group.class.getMethod("setName", String.class));
              columnSetterMap.put("Timestamp", Group.class.getMethod("setTimestamp", Date.class));
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
}