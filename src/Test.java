import library.db.*;
import library.model.*;
import java.sql.Connection;
import java.sql.SQLException;



public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			Connection libraryConnection = Connection_Factory.getConnection(DB_PARAMS.dbURL,
				DB_PARAMS.dbInitSchema);
			Book_Access bookTable = Table_Access.getInstance(libraryConnection, Book_Access.class);
			Copy_Access copyTable = Table_Access.getInstance(libraryConnection, Copy_Access.class);
			Loan_Access loanTable = Table_Access.getInstance(libraryConnection, Loan_Access.class);
			User_Access userTable = Table_Access.getInstance(libraryConnection, User_Access.class);
			User_Address_Access userAddressTable = Table_Access.getInstance(libraryConnection, User_Address_Access.class);
		} catch (SQLException e) {
            e.printStackTrace();
        }
		

	}

}
