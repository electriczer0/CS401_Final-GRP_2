package lib.db;

import java.lang.reflect.Method;
import java.sql.Connection;
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

}