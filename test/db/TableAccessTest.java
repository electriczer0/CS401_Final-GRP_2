package db;

//TODO Implement Find() test
//TODO Implement readAll() test
//TODO Implement update() test 


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
		
	public static Stream<Object[]> userData(){
		return Stream.of(
				//int User_Id, String FirstName, String LastName, String Type

				new Object[] {-1, "Joe", "Simms", "Patron"},
				new Object [] {1000, "Jess", "King", "Patron"},
				new Object[] {1001, "jim", "simms", "Patron"}
		);
		
	}
	
	@ParameterizedTest
	@MethodSource("userData")
	public void testUserDB(int User_Id, String FirstName, String LastName, String Type) {
		User user1 = User.create(User_Id, FirstName, LastName, Type); //we use this as our input
		User user2 = new User(); //we use this to test setters
		User user4 = null;
		user2.setID(User_Id);
		user2.setFirstName(FirstName);
		user2.setLastName(LastName);
		user2.setType(Type);
		User user3 = User.create(User_Id, FirstName, LastName, Type); //we use this to test the Table_Access class
		//user3 is different from user1 because Table_Access class may change User_Id
		
		try{
			userTable.insert(user3);
		}catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to Insert Record: " + user3);
        }
		
		//confirm that the userID is now set if it was not previously. 
		assertNotEquals(-1, user3.getID(), "UserID not correctly set by table object");
		
		//if User_Id was not set by input, it should have been set by the insert action
		if(user1.getID() == -1) {
			user1.setID(user3.getID());
			user2.setID(user3.getID());
		}
		
		//Test the read function to ensure the record was properly inserted. 
		try{
			user4 = userTable.read(user3.getID());
		}catch (SQLException e) {
            e.printStackTrace();
        }
		
		assertEquals(user1, user2);
		assertEquals(user2, user3);
		assertEquals(user3, user4);
		
	
		}

	public static Stream<Object[]> bookData(){
		return Stream.of(
				//int Book_Id, String Author, String ISBN, String Title

				new Object[] {-1, "Keringham & Ritchie", "1234567890", "The C Programming Language"},
				new Object[] {245, "Michael Chricton", "2345678901", "Jurassic Park"},
				new Object[] {20, "laksdf", "3456789012", "jfjei"}
		);
		
	}

	@ParameterizedTest
	@MethodSource("bookData")
	public void testBookDB(int Book_Id, String author, String isbn, String title) {
		Book book1 = Book.create(Book_Id, author, isbn, title); //we use this as our input
		Book book2 = new Book(); //we use this to test setters
		Book book4 = null;
		book2.setID(Book_Id);
		book2.setAuthor(author);
		book2.setISBN(isbn);
		book2.setTitle(title);
		Book book3 = Book.create(Book_Id, author, isbn, title); //we use this to test the Table_Access class
		//user3 is different from user1 because Table_Access class may change User_Id
		
		try{
			bookTable.insert(book3);
		}catch (SQLException e) {
            e.printStackTrace();
        }
		
		//confirm that the userID is now set if it was not previously. 
		assertNotEquals(-1, book3.getID(), "UserID not correctly set by table object");
		
		//if User_Id was not set by input, it should have been set by the insert action
		if(book1.getID() == -1) {
			book1.setID(book3.getID());
			book2.setID(book3.getID());
		}
		
		//Test the read function to ensure the record was properly inserted. 
		try{
			book4 = bookTable.read(book3.getID());
		}catch (SQLException e) {
            e.printStackTrace();
        }
		
		assertEquals(book1, book2);
		assertEquals(book2, book3);
		assertEquals(book3, book4);
		
	
		}
	
	public static Stream<Object[]> copyData(){
		return Stream.of(
				//int Copy_Id, int Book_Id

				new Object[] {-1, 748},
				new Object [] {1515, 749},
				new Object[] {111, 750}
		);
		
	}
	
	@ParameterizedTest
	@MethodSource("copyData")
	public void testCopyDB(int Copy_Id, int Book_Id) {
		Copy copy1 = Copy.create(Copy_Id, Book_Id); //we use this as our input
		Copy copy2 = new Copy(); //we use this to test setters
		Copy copy4 = null;
		copy2.setID(Copy_Id);
		copy2.setBookID(Book_Id);
		
		Copy copy3 = Copy.create(Copy_Id, Book_Id); //we use this to test the Table_Access class
		//copy3 is different from copy1 because Table_Access class may change User_Id
		
		try{
			copyTable.insert(copy3);
		}catch (SQLException e) {
            e.printStackTrace();
        }
		
		//confirm that the userID is now set if it was not previously. 
		assertNotEquals(-1, copy3.getID(), "UserID not correctly set by table object");
		
		//if User_Id was not set by input, it should have been set by the insert action
		if(copy1.getID() == -1) {
			copy1.setID(copy3.getID());
			copy2.setID(copy3.getID());
		}
		
		//Test the read function to ensure the record was properly inserted. 
		try{
			copy4 = copyTable.read(copy3.getID());
		}catch (SQLException e) {
            e.printStackTrace();
        }
		
		assertEquals(copy1, copy2);
		assertEquals(copy2, copy3);
		assertEquals(copy3, copy4);
		
	
		}

	public static Stream<Object[]> loanData(){
		
		/**
		 * Parameterized data function for loan object testing. 
		 * int Loan_Id, int Copy_Id, int User_Id, Date Date_Out, Date Date_Due, boolean Active
		 */
		
		return Stream.of(
			
				new Object[] {-1, 1, 1, createDate(2024, 5, 1), createDate(2024, 6, 1), true},
				new Object[] {20, 5, 2, createDate(2023, 1, 1), createDate(2023, 2, 1), false},
				new Object[] {21, 6, 3, createDate(2024, 6, 1), createDate(2024, 7, 1), true},
				new Object[] {22, 4, 4, createDate(2024,7,1), createDate(2024,8,1), false}
		);
		
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
	@MethodSource("loanData")
	public void testLoanDB(int Loan_Id, int Copy_Id, int User_Id, Date Date_Out, Date Date_Due, boolean Active) {
		/**
		 * Unit test for loan objects and the loanTable controller. 
		 * @param Loan_Id the primary key for the loan object may be set to -1 as a null value
		 * @param Copy_Id foreign key of the copy loaned out, foreign key must exist in Copy table
		 * @param User_Id foreign key to user table representing user to whom loan was issued, must exist in user table
		 * @param Date_Out Date object representing the date the loan was issued
		 * @param Date_Due Date object representing the date the loan is due
		 * @param Active boolean representing whether the loan is still outstanding 
		 */
		
		Loan loan1 = LoanFactory.create(Loan_Id, Copy_Id, User_Id, Date_Out, Date_Due, Active); //we use this as our input
		Loan loan2 = new Loan();
		loan2.setID(Loan_Id);
		loan2.setCopyID(Copy_Id);
		loan2.setUserID(User_Id);
		loan2.setDateOut(Date_Out);
		loan2.setDateDue(Date_Due);
		loan2.setActive(Active);
		
		Loan loan3 = LoanFactory.create(Loan_Id, Copy_Id, User_Id, Date_Out, Date_Due, Active); //we use this to test the Table_Access class
		//loan3 is needed because the Loan passed to the read operation is mutable; its primary key may change.
		
		Loan loan4 = null; // used for read operation
		
		
		
		
		
		try{
			loanTable.insert(loan3);
		}catch (SQLException e) {
            e.printStackTrace();
        }
		
		//confirm that the userID is now set if it was not previously. 
		assertNotEquals(-1, loan3.getID(), "LoanID not correctly set by table object");
		
		//if User_Id was not set by input, it should have been set by the insert action
		if(loan1.getID() == -1) {
			loan1.setID(loan3.getID());
			loan2.setID(loan3.getID());
		}
		
		//Test the read function to ensure the record was properly inserted. 
		try{
			loan4 = loanTable.read(loan3.getID());
		}catch (SQLException e) {
            e.printStackTrace();
        }
		
		assertEquals(loan1, loan2);
		assertEquals(loan2, loan3);
		assertEquals(loan3, loan4);
		
	
		}

	public static Stream<Object[]> addressData(){
		
		/**
		 * Parameterized data function for User_Address object testing. 
		 * int Address_Id, int User_Id, String Street1, String Street2, String City, String State, String Zip
		 */
		
		return Stream.of(
				new Object[] {-1, 1, "123 Main St", "Appt 2", "Labanon", "KY", "12345"},
				new Object[] {100, 2, "888 Main St", "", "Labanon", "KY", "12345"},
				new Object[] {101, 3, "999 Main St", "Unit z", "San Francisco", "CA", "12345"},
				new Object[] {102, 4, "1001 Main St", "", "New York", "NY", "12345"}
		);
		
	}

	
	@ParameterizedTest
	@MethodSource("addressData")
	public void testAddressDB(int Address_Id, int User_Id, String Street1, String Street2, String City, String State, String Zip) {
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
		
		User_Address address1 = User_Address.create(Address_Id, User_Id, Street1, Street2, City, State, Zip); //we use this as our input
		User_Address address2 = new User_Address();
		address2.setID(Address_Id);
		address2.setUserID(User_Id);
		address2.setStreet1(Street1);
		address2.setStreet2(Street2);
		address2.setCity(City);
		address2.setState(State);
		address2.setZip(Zip);
		
	
		
		User_Address address3 = User_Address.create(Address_Id, User_Id, Street1, Street2, City, State, Zip); //we use this to test the Table_Access class
		//address3 is needed because the Loan passed to the read operation is mutable; its primary key may change.
		
		User_Address address4 = null; // used for read operation
		
		
		
		
		
		try{
			userAddressTable.insert(address3);
		}catch (SQLException e) {
            e.printStackTrace();
        }
		
		//confirm that the userID is now set if it was not previously. 
		assertNotEquals(-1, address3.getID(), "LoanID not correctly set by table object");
		
		//if User_Id was not set by input, it should have been set by the insert action
		if(address1.getID() == -1) {
			address1.setID(address3.getID());
			address2.setID(address3.getID());
		}
		
		//Test the read function to ensure the record was properly inserted. 
		try{
			address4 = userAddressTable.read(address3.getID());
		}catch (SQLException e) {
            e.printStackTrace();
        }
		
		assertEquals(address1, address2);
		assertEquals(address2, address3);
		assertEquals(address3, address4);
		
	
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
}
