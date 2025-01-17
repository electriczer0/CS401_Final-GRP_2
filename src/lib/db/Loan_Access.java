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

public class Loan_Access extends Table_Access<Loan> {

    protected final String primary_key = "LoanID"; // Set the primary key field for the Loan table
    protected final String table_name = "Loans"; // Set the table name for the Loan table
    protected Connection connection;
    private final List<String> schema =
			Arrays.asList(
					primary_key + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			                + "CopyID INTEGER, "
			                + "UserID INTEGER, "
			                + "DateOut DATE, "
			                + "DateDue DATE, "
			                + "IsActive BOOLEAN, "
			                + "FOREIGN KEY (CopyID) REFERENCES Copy(CopyID) ON DELETE CASCADE, " // DB will delete related Loan records on deletion of related Copy or User
			                + "FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE" // However, User table schema should prevent deletion of user when a loan is outstanding
					);
 
    // Maps for getter and setter methods
    private final Map<String, Method> columnGetterMap;
    private final Map<String, Method> columnSetterMap;
    
    //initialize the maps
    {
        columnGetterMap = new HashMap<>();
        columnSetterMap = new HashMap<>();
        try {
            columnGetterMap.put("LoanID", Loan.class.getMethod("getID"));
            columnGetterMap.put("CopyID", Loan.class.getMethod("getCopyID"));
            columnGetterMap.put("UserID", Loan.class.getMethod("getUserID"));
            columnGetterMap.put("DateOut", Loan.class.getMethod("getDateOut"));
            columnGetterMap.put("DateDue", Loan.class.getMethod("getDateDue"));
            columnGetterMap.put("IsActive", Loan.class.getMethod("isActive"));

            columnSetterMap.put("LoanID", Loan.class.getMethod("setID", int.class));
            columnSetterMap.put("CopyID", Loan.class.getMethod("setCopyID", int.class));
            columnSetterMap.put("UserID", Loan.class.getMethod("setUserID", int.class));
            columnSetterMap.put("DateOut", Loan.class.getMethod("setDateOut", Date.class));
            columnSetterMap.put("DateDue", Loan.class.getMethod("setDateDue", Date.class));
            columnSetterMap.put("IsActive", Loan.class.getMethod("setActive", Boolean.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize column getter/setter maps", e);
        }
    }

    // Private constructor to enforce singleton pattern
    protected Loan_Access() {
        super(Loan.class);
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
    public Boolean hasActiveLoans(int User_Id) throws SQLException {
    	/**
    	 * Checks for active loans issued to the user represented by User_Id
    	 * @param User_Id int representation of User primary key 
    	 * @return Boolean True if User has >0 active loan records outstanding 
    	 */
	    String checkActiveLoansQuery = "SELECT COUNT(*) FROM Loans WHERE UserID = ? AND IsActive = TRUE";
	    try (var stmt = connection.prepareStatement(checkActiveLoansQuery)) {
	        stmt.setInt(1, User_Id);
	        try (var rs = stmt.executeQuery()) {
	            if (rs.next() && rs.getInt(1) > 0) {
	                return true;
	            }
	        }
	    }
	    return false;
	}
    public static Loan_Access getInstance() {
    	return Table_Access.getInstance(Loan_Access.class);
    }
    
    public Map<Integer, Loan> readAllActive() throws SQLException{
    	String sql = "SELECT LoanID, CopyID, UserID, DateOut, DateDue, IsActive "
                + "FROM Loans "
                + "WHERE IsActive = TRUE";

     Map<Integer, Loan> activeLoans = new HashMap<>();

     try (PreparedStatement stmt = connection.prepareStatement(sql)) {
         try (ResultSet rs = stmt.executeQuery()) {
             while (rs.next()) {
                 int loanID = rs.getInt("LoanID");
                 int copyID = rs.getInt("CopyID");
                 int id = rs.getInt("UserID");
                 Date dateOut =  Date.class.cast(parseValue(Date.class, rs.getObject("DateOut")));
                 Date dateDue = Date.class.cast(parseValue(Date.class, rs.getObject("DateDue")));
                 boolean isActive = rs.getBoolean("IsActive");
                 Loan loan = Loan.create(loanID, copyID, id, dateOut, dateDue, isActive);
                 activeLoans.put(loanID, loan);
             }
         }
     } catch (SQLException e) {
         throw new SQLException("Failed to retrieve all active loans ", e);
     }

     return activeLoans;
 }

}

