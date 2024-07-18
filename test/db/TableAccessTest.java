package db;

//TODO Implement Find() test
//TODO Implement readAll() test
//TODO Implement update() test 
//TODO Review and fix parse value error on date string from DB (sometimes it's returning YYYY-MM-DD string format, sometimes its a unix epoch code)
//TODO review "failed to set fields via reflection" error if the above does not fix this




import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path; 
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.util.Date;
import java.util.Calendar;

import lib.db.*;
import lib.model.*;

class TableAccessTest {
	private static Book_Access bookTable = null;
	private static Copy_Access copyTable = null;
	private static Loan_Access loanTable = null;
	private static User_Access userTable = null;
	private static User_Address_Access userAddressTable = null;
	private static final String testDBLocation = "Data/Test.db";
	private static final String activeDBLocation = "ActiveTest.db"; 
	private static final String testDBURL= "jdbc:sqlite:" + activeDBLocation;
	private static Connection libraryConnection;

	@BeforeAll
	static void setUpAll() throws Exception {
		/**
		 * Sets up database environment for tests
		 * Uses the file defined at testDBLocation 
		 * Code assumes that it can safely insert records with
		 * primary keys between 10,000 and 100,000
		 * The tables' primary key sequence should be set to 100,000 or more
		 * And there should be no existing keys in the range of 10,000 to 100,000
		 */
		
		//Copy the test database so that we maintain a clean test source
		Path sourcePath = Paths.get(testDBLocation);
		Path destinationPath = Paths.get(activeDBLocation);	
		try {
			Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			libraryConnection = Connection_Factory.getConnection(testDBURL,
				DB_PARAMS.dbInitSchema);
			bookTable = Table_Access.getInstance(libraryConnection, Book_Access.class);
			copyTable = Table_Access.getInstance(libraryConnection, Copy_Access.class);
			loanTable = Table_Access.getInstance(libraryConnection, Loan_Access.class);
			userTable = Table_Access.getInstance(libraryConnection, User_Access.class);
			userAddressTable = Table_Access.getInstance(libraryConnection, User_Address_Access.class);
		} catch (SQLException e) {
            e.printStackTrace();
        }
		
	}

	@AfterAll
	static void tearDownAll() throws Exception {
		
		libraryConnection.close(); //close out the DB connection
		
		//Delete test db file if it exists
		File dbFile = new File(activeDBLocation);
		if (dbFile.exists()) {
			dbFile.delete();
		}
		
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	private static Date createDate(int year, int month, int day) {
		/**
		 * Helper function to generate Date objects for use with various unit tests
		 * @param year - the year
		 * @param month - the month
		 * @param day - the day
		 * @return A Date object of the specified day at time 00:00:00
		 */
		Calendar calendar = Calendar.getInstance();
		calendar.set(year,  month, day, 0,0,0);
		calendar.set(Calendar.MILLISECOND,  0);
		return calendar.getTime();
		
	}	
	
	@ParameterizedTest
	@MethodSource("deleteData")
	public <T extends Has_ID> void testDelete(Table_Access<T> table, int recordID) {
		/**
		 * Unit test which tests the delete() method for any concrete Table_Access class. 
		 * @param table Instance of the Table_Access class that we're testing
		 * @param RecordID the ID which should be deleted
		 */
		try {
			Boolean recordExistsPre = table.read(recordID) != null;
			table.delete(recordID);
			Boolean recordExistsPost = table.read(recordID) != null;
			
			if(!recordExistsPre) {
				assertEquals(recordExistsPre, recordExistsPost, "Value initially absent, and then found");
			}else {
				assertNotEquals(recordExistsPre, recordExistsPost, "Value was not deleted");
			}
		}catch (SQLException e) {
            e.printStackTrace();
        }
		
		
	}
	
	public static Stream<Object[]> deleteData(){
		
		/**
		 * Parameterized data function to test delete operation.
		 * testDelete(Table_Access<T> table, int recordID) 
		 * Table_Access<T> table is an instance of the table we're testing
		 * recordID is the primary key of the record to delete. 
		 */
		
		return Stream.of(
				//-1 tests record not found logic
				new Object[] {bookTable, -1},
				new Object[] {copyTable, -1},
				new Object[] {loanTable, -1},
				new Object[] {userTable, -1},
				new Object[] {userAddressTable, -1},
				// test actual record deletions
				//these records must exist in the Test.DB
				//They also must not be related to ea other so that their deletion 
				//does not cascade 
				//Also may not delete records use in other tests 
				new Object[] {bookTable, 750},
				new Object[] {copyTable, 2},
				new Object[] {loanTable, 2033519},
				new Object[] {userTable, 8},
				new Object[] {userAddressTable, 10}
		);
		
	}

	public static Stream<Object[]> readWriteData(){
		return Stream.of(
				//Table_Access<T> table, T record

				new Object[] {userTable, User.create( -1, "Joe", "Simms", "Patron")},
				new Object [] {userTable, User.create(1000, "Jess", "King", "Patron")},
				new Object[] {userTable, User.create(1001, "jim", "simms", "Patron")},
				new Object[] {bookTable, Book.create(-1, "Keringham & Ritchie", "1234567890", "The C Programming Language")},
				new Object[] {bookTable, Book.create(245, "Michael Chricton", "2345678901", "Jurassic Park")},
				new Object[] {bookTable, Book.create(20, "laksdf", "3456789012", "jfjei")},
				new Object[] {copyTable, Copy.create(-1, 748)},
				new Object[] {copyTable, Copy.create(1515, 749)},
				new Object[] {copyTable, Copy.create(111, 755)},
				new Object[] {loanTable, Loan.create(-1, 1, 1, createDate(2024, 5, 1), createDate(2024, 6, 1), true)},
				new Object[] {loanTable, Loan.create(20, 5, 2, createDate(2023, 1, 1), createDate(2023, 2, 1), false)},
				new Object[] {loanTable, Loan.create(21, 6, 3, createDate(2024, 6, 1), createDate(2024, 7, 1), true)},
				new Object[] {loanTable, Loan.create(22, 4, 4, createDate(2024,7,1), createDate(2024,8,1), false)},
				new Object[] {userAddressTable,User_Address.create(-1, 1, "123 Main St", "Appt 2", "Labanon", "KY", "12345")},
				new Object[] {userAddressTable, User_Address.create(100, 2, "888 Main St", "", "Labanon", "KY", "12345")},
				new Object[] {userAddressTable, User_Address.create(101, 3, "999 Main St", "Unit z", "San Francisco", "CA", "12345")},
				new Object[] {userAddressTable, User_Address.create(102, 4, "1001 Main St", "", "New York", "NY", "12345")}
		);
	}
	
	@ParameterizedTest
	@MethodSource("readWriteData")
	public <T extends Has_ID & Has_Copy<T>> void testReadWrite(Table_Access<T> table, T record1) {
		/**
		 * Unit test for User_Address objects and the User_Address_Access controller. 
		 * @param Address_Id the primary key for the User_Address object may be set to -1 as a null value
		 * @param User_Id foreign key of the User whose address is represented, foreign key must exist in User table
		 * @param Street1 String representation of Street 1
		 * @param Street2 String representation of street 2
		 * @param City String representation of City
		 * @param State String 2 alpha digits for state abbreviation code
		 * @param Zip string representation 5 numeric digits representing zip code 
		 */
		
		
		
	
		
		T record2 = record1.copy(); //we use this to test the Table_Access class
		//address3 is needed because the Loan passed to the read operation is mutable; its primary key may change.
		
		T record3 = null; // used for read operation
		
		
		
		
		try{
			table.insert(record2);
		}catch (SQLException e) {
            e.printStackTrace();
        }
		
		//confirm that the userID is now set if it was not previously. 
		assertNotEquals(-1, record2.getID(), "LoanID not correctly set by table object");
		
		//if User_Id was not set by input, it should have been set by the insert action
		if(record1.getID() == -1) {
			record1.setID(record2.getID());
		}
		
		//Test the read function to ensure the record was properly inserted. 
		try{
			record3 = table.read(record1.getID());
		}catch (SQLException e) {
            e.printStackTrace();
        }
		
		assertEquals(record1, record2);
		assertEquals(record2, record3);
		
	
		}
		
}