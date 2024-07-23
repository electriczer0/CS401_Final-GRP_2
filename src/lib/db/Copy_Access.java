package lib.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.model.*;

public class Copy_Access extends Table_Access<Copy> {

    protected final String primary_key = "CopyID"; // Set the primary key field for the Copy table
    protected final String table_name = "Copy"; // Set the table name for the Copy table
    protected Connection connection; 
    private final List<String> schema =
			Arrays.asList(
					primary_key + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			                + "BookID INTEGER, "
			                + "FOREIGN KEY (BookID) REFERENCES Book(BookID) ON DELETE CASCADE" //DB will delete related Copy records on deletion of related book
					);
 
    // Maps for getter and setter methods
    private final Map<String, Method> columnGetterMap;
    private final Map<String, Method> columnSetterMap;
    
    //initialize the maps
    {
        columnGetterMap = new HashMap<>();
        columnSetterMap = new HashMap<>();
        try {
            columnGetterMap.put("CopyID", Copy.class.getMethod("getID"));
            columnGetterMap.put("BookID", Copy.class.getMethod("getBookID"));

            columnSetterMap.put("CopyID", Copy.class.getMethod("setID", int.class));
            columnSetterMap.put("BookID", Copy.class.getMethod("setBookID", int.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize column getter/setter maps", e);
        }
    }

    // Private constructor to enforce singleton pattern
    protected Copy_Access() {
        super(Copy.class);
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
    public static Copy_Access getInstance() {
    	return Table_Access.getInstance(Copy_Access.class);
    }
    
    public Loan getActiveLoan(int copyID) throws SQLException{
    	String sql = "SELECT LoanID, CopyID, UserID, DateOut, DateDue, IsActive "
                + "FROM Loans "
                + "WHERE CopyID = ? AND IsActive = TRUE";

     Loan activeLoan = null;

     try (PreparedStatement stmt = connection.prepareStatement(sql)) {
         stmt.setInt(1, copyID);
         try (ResultSet rs = stmt.executeQuery()) {
             if (rs.next()) {
                 int loanID = rs.getInt("LoanID");
                 int id = rs.getInt("CopyID");
                 int userID = rs.getInt("UserID");
                 Date dateOut = Date.class.cast(parseValue(Date.class,rs.getObject("DateOut")));
                 Date dateDue = Date.class.cast(parseValue(Date.class, rs.getObject("DateDue")));
                 boolean isActive = rs.getBoolean("IsActive");
                 activeLoan = Loan.create(loanID, id, userID, dateOut, dateDue, isActive);
             }
         }
     } catch (SQLException e) {
         throw new SQLException("Failed to retrieve active loans for Copy: " + copyID, e);
     }

     return activeLoan;
 }
    
}
