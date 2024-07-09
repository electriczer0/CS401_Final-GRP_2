package library.db;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import library.model.*;

public class User_Address_Access extends Table_Access<User_Address> {

    protected final String primary_key = "AddressID"; // Set the primary key field for the UserAddress table
    protected final String table_name = "UserAddresses"; // Set the table name for the UserAddress table
    private final List<String> schema =
            Arrays.asList(
                    primary_key + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "UserID INTEGER, "
                    + "Street1 TEXT, "
                    + "Street2 TEXT, "
                    + "City TEXT, "
                    + "Zip TEXT, "
                    + "State TEXT, "
                    + "FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE)" //DB will delete related UserAddress records on deletion of related user
            );
 
    // Maps for getter and setter methods
    private final Map<String, Method> columnGetterMap;
    private final Map<String, Method> columnSetterMap;
    
    //initialize the maps
    {
        columnGetterMap = new HashMap<>();
        columnSetterMap = new HashMap<>();
        try {
            columnGetterMap.put("AddressID", User_Address.class.getMethod("getID"));
            columnGetterMap.put("UserID", User_Address.class.getMethod("getUserID"));
            columnGetterMap.put("Street1", User_Address.class.getMethod("getStreet1"));
            columnGetterMap.put("Street2", User_Address.class.getMethod("getStreet2"));
            columnGetterMap.put("City", User_Address.class.getMethod("getCity"));
            columnGetterMap.put("Zip", User_Address.class.getMethod("getZip"));
            columnGetterMap.put("State", User_Address.class.getMethod("getState"));

            columnSetterMap.put("AddressID", User_Address.class.getMethod("setID", Integer.class));
            columnSetterMap.put("UserID", User_Address.class.getMethod("setUserID", Integer.class));
            columnSetterMap.put("Street1", User_Address.class.getMethod("setStreet1", String.class));
            columnSetterMap.put("Street2", User_Address.class.getMethod("setStreet2", String.class));
            columnSetterMap.put("City", User_Address.class.getMethod("setCity", String.class));
            columnSetterMap.put("Zip", User_Address.class.getMethod("setZip", String.class));
            columnSetterMap.put("State", User_Address.class.getMethod("setState", String.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize column getter/setter maps", e);
        }
    }

    // Private constructor to enforce singleton pattern
    private User_Address_Access() {
        super(User_Address.class);
    }

   
}
