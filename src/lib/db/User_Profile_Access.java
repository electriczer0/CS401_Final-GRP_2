package lib.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.model.*;

public class User_Profile_Access extends Table_Access<User_Profile> {

    protected final String primary_key = "ProfileID"; // Set the primary key field for the SMUserProfiles table
    protected final String table_name = "SMUserProfiles"; // Set the table name for the SMUserProfiles table
    protected Connection connection; 
    private final List<String> schema =
			Arrays.asList(
					primary_key + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			                + "UserID INTEGER NOT NULL, "
							+ "Username String NOT NULL,"
			                + "LitPrefs String,"
							+ "ReadingHabits String,"
			                + "FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE"
			                //DB will delete related SMUserProfiles records on deletion of related book
					);
 
    // Maps for getter and setter methods
    private final Map<String, Method> columnGetterMap;
    private final Map<String, Method> columnSetterMap;
    
    //initialize the maps
    {
        columnGetterMap = new HashMap<>();
        columnSetterMap = new HashMap<>();
        try {
            columnGetterMap.put(primary_key, User_Profile.class.getMethod("getID"));
            columnGetterMap.put("UserID", User_Profile.class.getMethod("getUserId"));
            columnGetterMap.put("Username", User_Profile.class.getMethod("getUsername"));
            columnGetterMap.put("ReadingHabits", User_Profile.class.getMethod("getReadingHabits"));
            columnGetterMap.put("LitPrefs", User_Profile.class.getMethod("getLiteraryPreferences"));

            columnSetterMap.put(primary_key, User_Profile.class.getMethod("setID", int.class));
            columnSetterMap.put("UserID", User_Profile.class.getMethod("setUserId", int.class));
            columnSetterMap.put("Username", User_Profile.class.getMethod("setUsername", String.class));
            columnSetterMap.put("LitPrefs", User_Profile.class.getMethod("setLiteraryPreferences", String.class));
            columnSetterMap.put("ReadingHabits", User_Profile.class.getMethod("setReadingHabits", String.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize column getter/setter maps", e);
        }
    }

    // Private constructor to enforce singleton pattern
    protected User_Profile_Access() { super(User_Profile.class); }
    protected  List<String> getTableSchema(){ return this.schema; }
    protected  Map<String, Method> getColumnGetterMap(){ return this.columnGetterMap;}
    protected  Map<String, Method> getColumnSetterMap(){return this.columnSetterMap;}
    protected Connection getConnection() {	return this.connection;  }
    protected void setConnection(Connection connection) {this.connection = connection; }
    protected String getTableName() {return this.table_name;}
    protected String getPrimaryKey() {return this.primary_key;}
    public static User_Profile_Access getInstance() {
    	return Table_Access.getInstance(User_Profile_Access.class);
    }
}
