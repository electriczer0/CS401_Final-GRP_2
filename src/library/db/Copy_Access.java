package library.db;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import library.model.*;

public class Copy_Access extends Table_Access<Copy> {

    protected final String primary_key = "CopyID"; // Set the primary key field for the Copy table
    protected final String table_name = "Copy"; // Set the table name for the Copy table
    private final List<String> schema =
			Arrays.asList(
					primary_key + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			                + "BookID INTEGER, "
			                + "FOREIGN KEY (BookID) REFERENCES Book(BookID) ON DELETE CASCADE)" //DB will delete related Copy records on deletion of related book
					);
 
    // Maps for getter and setter methods
    private final Map<String, Method> columnGetterMap;
    private final Map<String, Method> columnSetterMap;
    
    //initialize the maps
    {
        columnGetterMap = new HashMap<>();
        columnSetterMap = new HashMap<>();
        try {
            columnGetterMap.put("CopyID", Copy.class.getMethod("getId"));
            columnGetterMap.put("BookID", Copy.class.getMethod("getBookID"));

            columnSetterMap.put("CopyID", Copy.class.getMethod("setId", Integer.class));
            columnSetterMap.put("BookID", Copy.class.getMethod("setBookID", Integer.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize column getter/setter maps", e);
        }
    }

    // Private constructor to enforce singleton pattern
    private Copy_Access() {
        super(Copy.class);
    }

    
}
