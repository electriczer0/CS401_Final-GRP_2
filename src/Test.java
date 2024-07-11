import java.sql.Connection;
import java.sql.SQLException;

import lib.db.*;
import lib.model.*;
import lib.utilities.*;



public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Book_Access bookTable = null;
		Copy_Access copyTable = null;
		Loan_Access loanTable = null;
		User_Access userTable = null;
		User_Address_Access userAddressTable = null;
		
		try {
			Connection libraryConnection = Connection_Factory.getConnection(DB_PARAMS.dbURL,
				DB_PARAMS.dbInitSchema);
			bookTable = Table_Access.getInstance(libraryConnection, Book_Access.class);
			copyTable = Table_Access.getInstance(libraryConnection, Copy_Access.class);
			loanTable = Table_Access.getInstance(libraryConnection, Loan_Access.class);
			userTable = Table_Access.getInstance(libraryConnection, User_Access.class);
			userAddressTable = Table_Access.getInstance(libraryConnection, User_Address_Access.class);
		} catch (SQLException e) {
            e.printStackTrace();
        }
		try {
			// DataLoader.loadBooksFromCSV(bookTable, "Data/books.csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
