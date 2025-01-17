import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import lib.controller.LibraryController;
import lib.controller.UserController;
import lib.db.*;
import lib.model.User;
import lib.utilities.*;
import lib.view.LibraryView;
import lib.view.SocialView;

// Broadly the state diagram of this app switches between being in the library and interacting with books,
// and interacting with the social media platform.

enum cli_state {
	LIBRARY_MANAGEMENT,
	SOCIAL_MEDIA_PLATFORM
};

public class LibraryProgram { //<--Refactor

	private static cli_state state = cli_state.LIBRARY_MANAGEMENT;
	private static boolean exiting = false;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Book_Access bookTable = null;
		Copy_Access copyTable = null;
		Loan_Access loanTable = null;
		User_Access userTable = null;
		User_Address_Access userAddressTable = null;
		SMGroup_Access groupTable = null;
		SMInteraction_Access interactionTable = null;
		SMMeeting_Access meetingTable = null;
		
		try {
			Connection libraryConnection = Connection_Factory.getConnection(DB_PARAMS.dbURL,
				DB_PARAMS.dbInitSchema);
			bookTable = Table_Access.getInstance(libraryConnection, Book_Access.class);
			copyTable = Table_Access.getInstance(libraryConnection, Copy_Access.class);
			loanTable = Table_Access.getInstance(libraryConnection, Loan_Access.class);
			userTable = Table_Access.getInstance(libraryConnection, User_Access.class);
			userAddressTable = Table_Access.getInstance(libraryConnection, User_Address_Access.class);
			groupTable = Table_Access.getInstance(libraryConnection, SMGroup_Access.class);
			interactionTable = Table_Access.getInstance(libraryConnection, SMInteraction_Access.class);
			meetingTable = Table_Access.getInstance(libraryConnection, SMMeeting_Access.class);
			
		} catch (SQLException e) {
            e.printStackTrace();
        }
		try {
			//Let's load books and create some users here. Need to at a minimum create the 'Librarian' user.
			///DataLoader.loadBooksFromCSV(bookTable, "Data/books.csv");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Library Manager initialized.");

		Scanner sc = new Scanner(System.in);
		initUser(sc);

		while (!exiting && UserController.getCurrentUser() != null){
			int rtn = -1;
			switch(state) {
				case LIBRARY_MANAGEMENT:
					rtn = LibraryView.basePrompt(sc);
					break;
				case SOCIAL_MEDIA_PLATFORM:
					rtn = SocialView.basePrompt(sc);
					break;
			}
			if (rtn == 1){
				switchState();
			} else {
				exiting = true;
			}
		}

	}

	private static void switchState(){
		if (state == cli_state.LIBRARY_MANAGEMENT) {
			state = cli_state.SOCIAL_MEDIA_PLATFORM;
		} else {
			state = cli_state.LIBRARY_MANAGEMENT;
		}

	}

	public static void initUser(Scanner sc){
		while (UserController.getCurrentUser() == null) {
			String input = null;
			Integer userID = null;
			boolean validEntry = false;
			User user = null; 
	    	while(!validEntry) {
	    		System.out.println("Who is using this system? Enter an id, or type 'list' to list all users, or 'exit' to quit.");
	        	input = sc.next();
	        	try {
	        		if (input.equals("exit")) {
	    				return;
	    			}
	    			if (input.equals("list")) {
	    				LibraryView.listUsers();
	    				continue;
	    			} else {
		        		userID = Integer.parseInt(input);
		        		if(userID == null || userID <0 ) {
		        			System.out.println("Invalid Entry!");
		        			continue;
		        		}
		        		user = UserController.getUserById(userID);
		        		if (user == null) { continue; }
		        		validEntry = true; 
		        		}
	        		} catch (NumberFormatException e) {
	                System.out.println("Invalid input. Please enter a valid number >0.");
	            }
	        	
	    	}
				UserController.setCurrentUserById(user.getID());
		}
	}

}
