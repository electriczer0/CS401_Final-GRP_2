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

    // Override delete methods to check for active loans before deleting a user
    // Users cannot be deleted if a loan is outstanding
    @Override
    public void delete(int User_Id) throws SQLException {
        String checkActiveLoansQuery = "SELECT COUNT(*) FROM Loans WHERE UserID = ? AND IsActive = TRUE";
        try (var stmt = connection.prepareStatement(checkActiveLoansQuery)) {
            stmt.setInt(1, User_Id);
            try (var rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Cannot delete user with active loans");
                }
            }
        }
        super.delete(User_Id);
    }
    
    @Override
    public void delete(User user) throws SQLException {
    	this.delete(user.getID());
    	
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