package db;

//TODO Write additional Find() cases
//TODO write test for Loan_Access.hasActiveLoans()




import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Calendar;

import lib.db.*;
import lib.model.*;
import lib.model.Interaction.Interaction_Type;

@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(Lifecycle.PER_CLASS)
class TableAccessTest {
	private static Book_Access bookTable = null;
	private static Copy_Access copyTable = null;
	private static Loan_Access loanTable = null;
	private static User_Access userTable = null;
	private static SMInteraction_Access interactionTable = null;
	private static SMMeeting_Access meetingTable = null;
	private static SMGroup_Access groupTable = null; 
	private static User_Profile_Access userProfileTable = null;
	
	
	private static User_Address_Access userAddressTable = null;
	private static final String testDBLocation = "Data/Test.db";
	private static final String activeDBLocation = "ActiveTest.db"; 
	private static final String testDBURL= "jdbc:sqlite:" + activeDBLocation;
	private static Connection libraryConnection;

	@BeforeAll
	static void setUpAll() throws Exception {
		
		
		
		
	}

	@AfterAll
	static void tearDownAll() throws Exception {
		
		if(libraryConnection != null && !libraryConnection.isClosed()) {
			libraryConnection.close(); //close out the DB connection
		}
		
		
	}

	/**
	 * Sets up database environment for tests
	 * Uses the file defined at testDBLocation 
	 * Code assumes that it can safely insert records with
	 * primary keys between 10,000 and 100,000
	 * The tables' primary key sequence should be set to 100,000 or more
	 * And there should be no existing keys in the range of 10,000 to 100,000
	 * We run this as a BeforeEach method so that the test environment is clean
	 * and we need not worry about conflicting actions in the test environ 
	 * eg a test which deletes a record expected to exist by another method. 
	 */
	@BeforeEach
	void setUp() throws Exception {
		
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
			interactionTable = Table_Access.getInstance(libraryConnection, SMInteraction_Access.class);
			groupTable = Table_Access.getInstance(libraryConnection, SMGroup_Access.class);
			meetingTable = Table_Access.getInstance(libraryConnection, SMMeeting_Access.class);
			userProfileTable = Table_Access.getInstance(libraryConnection, User_Profile_Access.class);
		} catch (SQLException e) {
            e.printStackTrace();
        }
				
		assert bookTable != null && copyTable != null &&
				loanTable != null && userTable != null &&
				userAddressTable != null && interactionTable != null &&
				groupTable != null && meetingTable != null &&
				userProfileTable != null : "Caught null tables"; 
	}

	@AfterEach
	void tearDown() throws Exception {
		
		if(libraryConnection != null && !libraryConnection.isClosed()) {
			libraryConnection.close(); //close out the DB connection
		}
		
		Table_Access.removeInstance(Book_Access.class);
		Table_Access.removeInstance(Copy_Access.class);
		Table_Access.removeInstance(Loan_Access.class);
		Table_Access.removeInstance(User_Access.class);
		Table_Access.removeInstance(User_Address_Access.class);
		Table_Access.removeInstance(SMInteraction_Access.class);
		Table_Access.removeInstance(SMGroup_Access.class);
		Table_Access.removeInstance(SMMeeting_Access.class);
		Table_Access.removeInstance(User_Profile_Access.class);
		
		bookTable=null;
		copyTable=null;
		loanTable=null;
		userTable=null;
		userAddressTable=null;
		interactionTable = null; 
		groupTable = null;
		meetingTable = null;
		userProfileTable = null;
		
		//Delete test db file if it exists
		File dbFile = new File(activeDBLocation);
		if (dbFile.exists()) {
			dbFile.delete();
		}
		
		
		
				
		
	}
	/**
	 * Helper function to generate Date objects for use with various unit tests
	 * @param year - the year
	 * @param month - the month
	 * @param day - the day
	 * @return A Date object of the specified day at time 00:00:00
	 */
	private static Date createDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year,  month, day, 0,0,0);
		calendar.set(Calendar.MILLISECOND,  0);
		return calendar.getTime();
		
	}	
	
	/**
	 * Unit test which tests the delete() method for any concrete Table_Access class. 
	 * @param table Instance of the Table_Access class that we're testing
	 * @param RecordID the ID which should be deleted
	 */
	@ParameterizedTest
	@MethodSource("deleteData")
	public <T extends Table_Access<?>> void testDelete(Class<T> clazz, int recordID) {
		
		try {
			Boolean recordExistsPre = Table_Access.getInstance(clazz).read(recordID) != null;
			Table_Access.getInstance(clazz).delete(recordID);
			Boolean recordExistsPost = Table_Access.getInstance(clazz).read(recordID) != null;
			
			if(!recordExistsPre) {
				assertEquals(recordExistsPre, recordExistsPost, "Value initially absent, and then found");
			}else {
				assertNotEquals(recordExistsPre, recordExistsPost, "Value was not deleted");
			}
		}catch (SQLException e) {
			SQLException e2 = new SQLException("Caught exception deleting record " + recordID + " from " + clazz, e);
            e2.printStackTrace();
        }
		
		
	}
	
	/**
	 * Parameterized data function to test delete operation.
	 * testDelete(Table_Access<T> table, int recordID) 
	 * Table_Access<T> table is an instance of the table we're testing
	 * recordID is the primary key of the record to delete. 
	 */
	public static Stream<Object[]> deleteData(){
		
		return Stream.of(
				//-1 tests record not found logic
				new Object[] {Book_Access.class, -1},
				new Object[] {Copy_Access.class, -1},
				new Object[] {Loan_Access.class, -1},
				new Object[] {User_Access.class, -1},
				new Object[] {User_Profile_Access.class, -1},
				new Object[] {User_Address_Access.class, -1},
				new Object[] {SMInteraction_Access.class, -1},
				new Object[] {SMMeeting_Access.class, -1},
				new Object[] {SMGroup_Access.class, -1},
				// test actual record deletions
				//these records must exist in the Test.DB
				new Object[] {Book_Access.class, 750},
				new Object[] {Copy_Access.class, 2},
				new Object[] {Loan_Access.class, 2033519},
				new Object[] {User_Access.class, 8},
				new Object[] {User_Address_Access.class, 10},
				new Object[] {User_Profile_Access.class, 1},
				new Object[] {SMInteraction_Access.class, 1},
				new Object[] {SMMeeting_Access.class, 2},
				new Object[] {SMGroup_Access.class, 1}
		);
		
	}

	public static Stream<Object[]> readWriteData(){
		return Stream.of(
				//Class<T> clazz, T record

				new Object[] {User_Access.class, User.create( -1, "Joe", "Simms", "Patron")},
				new Object[] {User_Access.class, User.create(1000, "Jess", "King", "Patron")},
				new Object[] {User_Access.class, User.create(1001, "jim", "simms", "Patron")},
				new Object[] {Book_Access.class, Book.create(-1, "Keringham & Ritchie", "1234567890", "The C Programming Language")},
				new Object[] {Book_Access.class, Book.create(245, "Michael Chricton", "2345678901", "Jurassic Park")},
				new Object[] {Book_Access.class, Book.create(20, "laksdf", "3456789012", "jfjei")},
				new Object[] {Copy_Access.class, Copy.create(-1, 748)},
				new Object[] {Copy_Access.class, Copy.create(1515, 749)},
				new Object[] {Copy_Access.class, Copy.create(111, 755)},
				new Object[] {Loan_Access.class, Loan.create(-1, 1, 1, createDate(2024, 5, 1), createDate(2024, 6, 1), true)},
				new Object[] {Loan_Access.class, Loan.create(20, 5, 2, createDate(2023, 1, 1), createDate(2023, 2, 1), false)},
				new Object[] {Loan_Access.class, Loan.create(21, 6, 3, createDate(2024, 6, 1), createDate(2024, 7, 1), true)},
				new Object[] {Loan_Access.class, Loan.create(22, 4, 4, createDate(2024,7,1), createDate(2024,8,1), false)},
				new Object[] {User_Address_Access.class,User_Address.create(-1, 1, "123 Main St", "Appt 2", "Labanon", "KY", "12345")},
				new Object[] {User_Address_Access.class, User_Address.create(100, 2, "888 Main St", "", "Labanon", "KY", "12345")},
				new Object[] {User_Address_Access.class, User_Address.create(101, 3, "999 Main St", "Unit z", "San Francisco", "CA", "12345")},
				new Object[] {User_Address_Access.class, User_Address.create(102, 4, "1001 Main St", "", "New York", "NY", "12345")},
				new Object[] {SMGroup_Access.class, Group.create(-1, 10, "newgroup1", "Dan's new group", new Date()) },
				new Object[] {SMMeeting_Access.class, Meeting.create(-1, 3, 6, "my study session", "crushing cs401", "123 main st", new Date(), new Date())},
				new Object[] {SMMeeting_Access.class, Meeting.create(-1, 3, -1, "my study session2", "crushing cs401 again", "123443 2nd street", new Date(), new Date())},
				new Object[] {SMInteraction_Access.class, Interaction.create(-1, 1, 1, 1, Interaction_Type.COMMENT_ON_CONTENT, "My comment here" , new Date())}, 
				new Object[] {User_Profile_Access.class, User_Profile.create(-1, 4, "crashTestDummy", "abstract", "picture books plz")} 
		);
	}
	
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
	@ParameterizedTest
	@MethodSource("readWriteData")
	public <T extends Has_ID & Has_Copy<T>, J extends Table_Access<T>> void testReadWrite(Class<J> clazz, T record1) {
		
		T record2 = record1.copy(); //we use this to test the Table_Access class
		//address3 is needed because the Loan passed to the read operation is mutable; its primary key may change.
		
		T record3 = null; // used for read operation
		try{
			Table_Access.getInstance(clazz).insert(record2);
		}catch (SQLException e) {
			SQLException e2 = new SQLException("Caught exception inserting: " + record1, e);
            e2.printStackTrace();
        }
		
		//confirm that the userID is now set if it was not previously. 
		assertNotEquals(-1, record2.getID(), "record ID not correctly set by table object");
		
		//if User_Id was not set by input, it should have been set by the insert action
		if(record1.getID() == -1) {
			record1.setID(record2.getID());
		}
		
		//Test the read function to ensure the record was properly inserted. 
		try{
			record3 = Table_Access.getInstance(clazz).read(record1.getID());
		}catch (SQLException e) {
			SQLException e2 = new SQLException("Caught exception reading: " + record1, e);
            e2.printStackTrace();
        }
		
		assertEquals(record1, record2);
		assertEquals(record2, record3);
	}

	/**
	 * Basic update functionality test
	 * @param clazz the Table_Access class we're updating to
	 * @param record1 the entity to be updated. primary key must match existing record and at least 1 field must be updated
	 */
	@ParameterizedTest
	@MethodSource("updateData")
	public <T extends Has_ID & Has_Copy<T>, J extends Table_Access<T>> void testUpdate(Class<J> clazz, T record1) {
		T record2 = null; //the original record before update
		T record3 = null; //the updated record
		try {
			record2 = Table_Access.getInstance(clazz).read(record1.getID());
		}catch (SQLException e) {
			SQLException e2 = new SQLException("Caught exception reading: " + record1.getClass() + " " + record1.getID(), e);
            e2.printStackTrace();
		}
		
		try {
			Table_Access.getInstance(clazz).update(record1);
		}catch (SQLException e) {
			SQLException e2 = new SQLException("Caught exception updating: " + record1, e);
            e2.printStackTrace();
		}
		
		try {
			record3 = Table_Access.getInstance(clazz).read(record1.getID());
		}catch (SQLException e) {
			SQLException e2 = new SQLException("Caught exception reading: " + record1.getClass() + " " + record1.getID(), e);
            e2.printStackTrace();
		}
		
		assertEquals(record1, record3);
		assertNotEquals(record2, record3);
	}
	
	public static Stream<Object[]> updateData(){
		return Stream.of(
				//Class<T> clazz, T record

				new Object[] {User_Access.class, User.create(1, "Joe", "Simms", "Patron")},
				new Object[] {User_Access.class, User.create(2, "Jess", "King", "Patron")},
				new Object[] {User_Access.class, User.create(3, "jim", "simms", "Patron")},
				new Object[] {Book_Access.class, Book.create(748, "Keringham & Ritchie", "1234567890", "The C Programming Language")},
				new Object[] {Book_Access.class, Book.create(749, "Michael Chricton", "2345678901", "Jurassic Park")},
				new Object[] {Book_Access.class, Book.create(750, "laksdf", "3456789012", "jfjei")},
				new Object[] {Copy_Access.class, Copy.create(5, 748)},
				new Object[] {Copy_Access.class, Copy.create(6, 749)},
				new Object[] {Copy_Access.class, Copy.create(7, 755)},
				new Object[] {Loan_Access.class, Loan.create(2033519, 1, 1, createDate(2024, 5, 1), createDate(2024, 6, 1), true)},
				new Object[] {Loan_Access.class, Loan.create(2033520, 5, 2, createDate(2023, 1, 1), createDate(2023, 2, 1), false)},
				new Object[] {Loan_Access.class, Loan.create(2033521, 6, 3, createDate(2024, 6, 1), createDate(2024, 7, 1), true)},
				new Object[] {Loan_Access.class, Loan.create(2033522, 4, 4, createDate(2024,7,1), createDate(2024,8,1), false)},
				new Object[] {User_Address_Access.class,User_Address.create(1, 1, "123 Main St", "Appt 2", "Labanon", "KY", "12345")},
				new Object[] {User_Address_Access.class, User_Address.create(2, 2, "888 Main St", "", "Labanon", "KY", "12345")},
				new Object[] {User_Address_Access.class, User_Address.create(3, 3, "999 Main St", "Unit z", "San Francisco", "CA", "12345")},
				new Object[] {User_Address_Access.class, User_Address.create(4, 4, "1001 Main St", "", "New York", "NY", "12345")},
				new Object[] {User_Profile_Access.class, User_Profile.create(1, 1, "Kripsy", "Fiction", "alskdjf")},
				new Object[] {SMInteraction_Access.class, Interaction.create(1, 20, 1, 3, Interaction_Type.SHARE_ON_CONTENT, "EDIT: I LOVEXXX THIS!", new Date())},
				new Object[] {SMMeeting_Access.class, Meeting.create(2, 19, 1, "Updated Group Meeting", "To celebrate!", "MY HOUSE!*$", new Date(), new Date())},
				new Object[] {SMGroup_Access.class, Group.create(1, 18, "a book club!" , "drinking and books! Duh!", new Date())}
		);
	}
	
	/**
	 * Basic find functionality test
	 * @param clazz the Table_Access class we're searching
	 * @param queryParams a HashMap<String, String> representing the search query where key = column name, and value = search term. 
	 * @param expected a List<T> representing the list that should be returned by the DB
	 */
	@ParameterizedTest
	@MethodSource("findData")
	public <T extends Has_ID & Has_Copy<T>, J extends Table_Access<T>> void testFind(Class<J> clazz,HashMap<String, String> queryParams, Map<Integer, T> expected) {
		Map<Integer, T> found = null;
		try {
			found = Table_Access.getInstance(clazz).find(queryParams);
		}catch (SQLException e) {
			SQLException e2 = new SQLException("Caught exception executing find on table: " + clazz + " query: " + queryParams, e);
            e2.printStackTrace();
		}
		
		assertEquals(expected, found);
	}
	
	@SuppressWarnings("serial")
	public static Stream<Arguments> findData(){
		return Stream.of(
				//Class<J> clazz,HashMap<String, String> queryParams, List <T> expected
				
				Arguments.of(
						User_Address_Access.class,
						new HashMap<String, String>() {{
							put("State", "NM");
						}},
						new HashMap<Integer, User_Address>() {{ 
							put(9, User_Address.create(9, 9, "24700 Garcia Street Suite 249", "Suite 643", "West Maryton", "NM", "23231"));
							put(12, User_Address.create(12, 12, "192 Avila Mission", "Suite 034", "Waltonchester", "NM", "52659"));
						}}
						)
		);
	}
	
	/**
	 * Simple test confirms that findAll() returns the same results as find()
	 */
	@ParameterizedTest
	@MethodSource("readAllData")
	public <T extends Has_ID & Has_Copy<T>, J extends Table_Access<T>> void testReadAll(Class<J> clazz) {
		
		
		Map<Integer, T> found1 = null;
		Map<Integer, T> found2 = null;
		HashMap<String, String> blankQuery = new HashMap();
		try {
			found1 = Table_Access.getInstance(clazz).find(blankQuery);
			found2 = Table_Access.getInstance(clazz).readAll(); 
		}catch (SQLException e) {
			SQLException e2 = new SQLException("Caught exception executing find / readall on table: " + clazz, e);
            e2.printStackTrace();
		}
		
		assertEquals(found1, found2);
			
		
		
	}
	
	public static Stream<Arguments> readAllData(){
		return Stream.of(
				//Class<J> clazz
				
				Arguments.of(Book_Access.class),
				Arguments.of(Copy_Access.class),
				Arguments.of(Loan_Access.class),
				Arguments.of(User_Access.class),
				Arguments.of(User_Address_Access.class)
		);
	}

	@Test
	public static void testUserBookLists() {
		Map<Integer, Book> faveExpected = new HashMap<>();
		Map<Integer, Book> faveActual = null;
		Map<Integer, Book> recentExpected = new HashMap<>();
		Map<Integer, Book> recentActual = null; 
		try {
			faveExpected.put(754, bookTable.read(754));
			faveExpected.put(760, bookTable.read(760));
			recentExpected.put(749, bookTable.read(749));
			recentExpected.put(750, bookTable.read(750));
			faveActual = userTable.getFavorites(1);
			recentActual = userTable.getRecentBooks(1);
		}catch (SQLException e) {
			SQLException e2 = new SQLException("Caught exception testUserBookLists test", e);
            e2.printStackTrace();
		}
		
		assertEquals(faveExpected, faveActual);
		assertEquals(recentExpected, recentActual);
		
	}
	
	/**
	 * Tests the accessor methods for the several join queries
	 * including getGroupMembers, getGroups, and getMeetings
	 */
	@Test
	public static void testGroupMemberLists() {
		Map<Integer, User> usersExpected = new HashMap<>();
		Map<Integer, User> usersActual = null;
		Map<Integer, Group> groupsExpected = new HashMap<>();
		Map<Integer, Group> groupsActual = null; 
		Map<Integer, Meeting> meetingsExpected = new HashMap<>();
		Map<Integer, Meeting> meetingsActual = null; 
		try {
			usersExpected.put(1, userTable.read(1));
			usersExpected.put(4, userTable.read(4));
			groupsExpected.put(1, groupTable.read(1));
			meetingsExpected.put(2, meetingTable.read(2));
			meetingsExpected.put(3, meetingTable.read(3));
			
			
			
			usersActual = groupTable.getGroupMembers(1);
			groupsActual = userTable.getGroups(1);
			meetingsActual = userTable.getMeetings(1);
			
		}catch (SQLException e) {
			SQLException e2 = new SQLException("Caught exception testGroupMemberLists test", e);
            e2.printStackTrace();
		}
		
		assertEquals(usersExpected, usersActual);
		assertEquals(groupsExpected, groupsActual);
		assertEquals(meetingsExpected, meetingsActual);
		
	}
}