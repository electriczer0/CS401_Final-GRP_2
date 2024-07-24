package lib.view;

import lib.controller.LibraryController;
import lib.controller.UserController;
import lib.db.Book_Access;
import lib.db.Copy_Access;
import lib.db.Loan_Access;
import lib.model.Book;
import lib.model.Copy;
import lib.model.Loan;
import lib.model.User;
import lib.utilities.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This static class manages the user-facing library interactions CLI.
 * A user who is a librarian can add and remove books from the library collection, add and remove users
 * from the library, and generate reports.
 * A user who is a patron can check books out, return books, and see what books they've borrowed and when they're due.
 */

enum LibraryAction {
    ADD_USER,
    REMOVE_USER,
    LIST_USERS,
    ADD_BOOK,
    REMOVE_BOOK,
    LIST_BOOKS,
    GENERATE_LIBRARY_CHECKOUT_REPORT,
    CHECK_BOOK_COPY_AVAILABLE,
    GENERATE_BOOK_COPIES,
    CHECKOUT_BOOK,
    DEPOSIT_BOOK,
    GENERATE_USER_CHECKOUT_REPORT,
    ADD_COPY,
    REMOVE_COPY
}
public class LibraryView {

    private static boolean exiting = false;

    /**
     * Initial intake. Returning a '1' indicates the user wants to switch modes, and returning a '0' indicates
     * that the user wants to quit.
     */
    public static int basePrompt(Scanner sc){
        if (UserController.getCurrentUser() == null){ return 0; } //Should never happen



        System.out.println("Welcome to the Library Manager, " + UserController.getCurrentUser().getFirstName() + ".");
        //We re-initialize this list each time we return to the base Prompt, as the user might have changed 
        // between calls.
        ArrayList<LibraryAction> actions = new ArrayList<LibraryAction>();
        actions.add(LibraryAction.LIST_BOOKS);
        actions.add(LibraryAction.GENERATE_BOOK_COPIES);
        actions.add(LibraryAction.CHECK_BOOK_COPY_AVAILABLE);
        if (UserController.getCurrentUser().getType().equals("Librarian")){
            actions.add(LibraryAction.ADD_USER);
            actions.add(LibraryAction.REMOVE_USER);
            actions.add(LibraryAction.LIST_USERS);
            actions.add(LibraryAction.ADD_BOOK);
            actions.add(LibraryAction.REMOVE_BOOK);
            actions.add(LibraryAction.ADD_COPY);
            actions.add(LibraryAction.REMOVE_COPY);
            actions.add(LibraryAction.GENERATE_LIBRARY_CHECKOUT_REPORT);
        }
        if (UserController.getCurrentUser().getType().equals("Patron")){
        	actions.add(LibraryAction.GENERATE_USER_CHECKOUT_REPORT);
            actions.add(LibraryAction.CHECKOUT_BOOK);
            actions.add(LibraryAction.DEPOSIT_BOOK);
        }
        while (!exiting){
            System.out.println("Please select one of the actions available, or 'exit' to exit.");
            System.out.println("You'll need a book copy id to check out or deposit a book.");
            System.out.println("Type 'social' to switch to the Social Media platform.");
            for (int i = 0; i < actions.size(); i++){
                System.out.println((i + 1) + ". " + printAction(actions.get(i)));
            }
            String input = sc.next();
            if (input.equals("social")){
                return 1;
            } else if (input.equals("exit")) {
            	exiting = true;
            } else {
            	try {
            		int actionIndex = Integer.parseInt(input) - 1;
                    if (actionIndex >= 0 && actionIndex < actions.size()) {
                        LibraryAction action = actions.get(actionIndex);
                        dispatchAction(action, sc);
                    } else {
                        System.out.println("Invalid action number. Please select a valid menu action.");
                    }
            		
            	} catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number corresponding to a menu action or a valid command.");
                }
              } 
        }
        return 0;
    }

    private static String printAction(LibraryAction action){
        switch (action){
            case ADD_USER -> {return "Add a new user to the list of library patrons.";}
            case REMOVE_USER -> {return "Remove a user from the list of library patrons.";}
            case LIST_USERS -> {return "List all users.";}
            case ADD_BOOK -> {return "Add a book entry to the collection.";}
            case REMOVE_BOOK -> {return "Remove a book entry from the collection.";}
            case ADD_COPY -> {return "Add a copy of a book to the collection.";}
            case REMOVE_COPY -> {return "Remove a copy of a book from the collection.";}
            case LIST_BOOKS -> {return "List all books in the collection.";}
            case GENERATE_LIBRARY_CHECKOUT_REPORT -> {return "Print a list of all book loans.";}
            case CHECK_BOOK_COPY_AVAILABLE -> {return "Check a book copy for availability.";}
            case GENERATE_BOOK_COPIES -> { return "List all copies of a Book.";}
            case CHECKOUT_BOOK -> {return "Checkout an available book.";}
            case DEPOSIT_BOOK -> {return "Return a loaned book.";}
            case GENERATE_USER_CHECKOUT_REPORT -> {return "Print a list of your borrowed books.";}
            default -> {return "";}

        }
    }

    private static void dispatchAction(LibraryAction action, Scanner sc){
        switch (action){
            case ADD_USER -> {addNewUser(sc); break;}
            case REMOVE_USER -> {removeUser(sc); break;}
            case LIST_USERS -> {listUsers(); break;}
            case ADD_BOOK -> {addNewBook(sc); break;}
            case REMOVE_BOOK -> {removeBook(sc); break;}
            case LIST_BOOKS -> {listBooks(); break;}
            case ADD_COPY -> {addCopy(sc); break;}
            case REMOVE_COPY -> {removeCopy(sc); break;}
            case GENERATE_LIBRARY_CHECKOUT_REPORT -> {genLibReport(); break;}
            case CHECK_BOOK_COPY_AVAILABLE -> {checkAvailability(sc); break;}
            case GENERATE_BOOK_COPIES -> {listBookCopies(sc); break;}
            case CHECKOUT_BOOK -> {checkoutBook(sc); break;}
            case DEPOSIT_BOOK -> {depositBook(sc); break;}
            case GENERATE_USER_CHECKOUT_REPORT -> {genUserReport(sc); break;}
            default -> {return;}
        }
    }

    private static void listBookCopies(Scanner sc) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	List<Copy> copies = null;
    	String dueBack = null;
    	String input = null; 
    	Integer bookID = null; 
    	
    	boolean validEntry = false;
    	while(!validEntry) {
    		System.out.println("Enter the Book ID:");
        	input = sc.next();
        	try {
        		bookID = Integer.parseInt(input);
        		if(bookID == null || bookID <0) {
        			System.out.println("Invalid Entry!");
        			continue;
        		} else {
        			validEntry = true; 
        		}
        	} catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number >0.");
            }
        	
    	}
    	Book book = LibraryController.getBookById(bookID);
    	copies = LibraryController.getBookCopies(bookID);
    	if(copies != null) {
    		
    		
	    	System.out.println("------------------------------------------------------------"); //60 dashes
	    	System.out.println("| Book: " + Utils.fitString(book.getTitle(), 51)+"|");
	    	System.out.println("------------------------------------------------------------"); //60 dashes
	        System.out.println("| Copy | Available?                    | Due Back          |");
	        System.out.println("------------------------------------------------------------");
	        
	        for (Copy copy: copies){
	        	Loan relatedLoan = LibraryController.checkIfCopyHasLoan(copy.getID());
	        	boolean avail =  relatedLoan == null;
	        	dueBack = ""; 
	        	if(!avail) {
	        		dueBack = dateFormat.format(relatedLoan.getDateDue());
	        	}
	            System.out.println("| " + Utils.fitString(copy.getID() + "", 4) +
	                    " | " + Utils.fitString(String.valueOf(avail), 29) +
	                    " | " + Utils.fitString( dueBack, 17) + " |");
	        }
	        System.out.println("------------------------------------------------------------");
    	} else {
    		System.out.println("No Copies Found.");
    	}
    	return;
    }
    private static void addNewUser(Scanner sc){
    	boolean validEntry = false; 
    	boolean allValid = false; 
    	String fn = null;
    	String ln = null;
    	String input = null; 
    	Integer selection = null;
    	while(!allValid) {
    		fn = null;
    		ln = null; 
    		input = null;
    		selection = null;
    		validEntry = false; 
	    	 while(!validEntry) {
	    		 System.out.println("What is the new user's first name?");
	    	     fn = sc.nextLine();
	    	     System.out.println("What is the new user's last name?");
	    	     ln = sc.nextLine();
	    	     if(fn.isBlank() || fn.isEmpty() || ln.isBlank() || ln.isEmpty()) {
	         		System.out.println("First name and last name must be set");
	         		continue;
	    	     } else {
	         		validEntry = true; 
	    	     }
	     	}
	        validEntry = false; 
	        try {
		        while(!validEntry) {
		    		System.out.println("Select a User Role (1) Patron (2) Librarian");
		        	input = sc.next();
	        		selection = Integer.parseInt(input);
	        		if(selection != 1 && selection != 2) {
	        			System.out.println("Invalid Entry!");
	        			continue;
	        		} else {
	        			validEntry = true; 
	        		}
		    	}
	        } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid selection 1 or 2");
                continue; 
            }
	        allValid = true; 
    	}
    	String type = null; 
    	switch(selection){
    		case 1:
    			type = "Patron";
    			break;
    		case 2:
    			type = "Librarian";
    			break;
			default:
    	}
        LibraryController.createNewUser(fn, ln, type);
    }

    private static void removeUser(Scanner sc){
        System.out.println("What is the id of the user you want to remove?");
        String id = sc.next();
        LibraryController.deleteUserById(id);
    }
    //Public to permit the top level call from the init
    public static void listUsers(){
        List<User> userList = LibraryController.listUsers();
        /*
        User k = new User();
        k.setID(93);
        k.setFirstName("John");
        k.setLastName("Doo");
        k.setType("Librarian");
        userList.add(k);*/
        System.out.println("------------------------------------------------------------"); //60 dashes
        System.out.println("| Id   | Name                          | Role              |");
        System.out.println("------------------------------------------------------------");
        for (User user: userList){
            System.out.println("| " + Utils.fitString(user.getID() + "", 4) +
                    " | " + Utils.fitString(user.getFirstName() + " " + user.getLastName(), 29) +
                    " | " + Utils.fitString( user.getType(), 17) + " |");
        }
        System.out.println("------------------------------------------------------------");
    }
    private static void addNewBook(Scanner sc){
    	if (sc.hasNextLine()) {
    		sc.nextLine();
    	}
        System.out.println("What is the new book's title?");
        String title = sc.nextLine();
        System.out.println("Who is the author?");
        String author = sc.nextLine();
        boolean validEntry = false; 
        String isbn = null; 
    	 while(!validEntry) {
    		 System.out.println("What's the ISBN?");
    	     isbn = sc.nextLine();
    	     int length = isbn.length();
    	     if(!isbn.matches("[a-zA-Z0-9]+") || ( length != 10 && length != 13)) {
         		System.out.println("Invalid ISBN must be 10 or 13 digit alphanumeric");
    	     } else {
         		validEntry = true; 
    	     }
    	 }
        LibraryController.addNewBook(title, author, isbn);
    }
    private static void removeBook(Scanner sc){
    	
    	boolean validEntry = false; 
    	String input = null; 
    	Integer id = null; 
    	
    	while(!validEntry) {
    		System.out.println("Enter the id of the book record you'd like to delete.");
        	input = sc.next();
        	try {
        		id = Integer.parseInt(input);
        		if(id == null || id <0) {
        			System.out.println("Invalid Entry!");
        			continue;
        		} else {
        			validEntry = true; 
        		}
        	} catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number >0.");
            }
        	
    	}
        LibraryController.deleteBookById(id);
    }
    private static void listBooks(){
    	
        List<Book> bookList = LibraryController.listBooks(); 
        System.out.println("------------------------------------------------------------------------------------------"); //90 dashes
        System.out.println("| Id   | Title                          | Author                         | ISBN          |");
        System.out.println("------------------------------------------------------------------------------------------");
        for (Book thisbook: bookList){
            if(thisbook != null) {
	            System.out.println("| " + Utils.fitString(thisbook.getID() + "", 4) +
	                    " | " + Utils.fitString(thisbook.getTitle(), 30) +
	                    " | " + Utils.fitString(thisbook.getAuthor(), 30) +
	                    " | " + Utils.fitString(thisbook.getISBN(), 13) + " |");
            }
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }
    private static void genLibReport(){
        List<Loan> loanList = LibraryController.generateCheckoutBookList(null);
        System.out.println("------------------------------------------------------------------------------------------"); //90 dashes
        System.out.println("| Book Id | Title                           | Loaned to                    | Due back by |");
        System.out.println("------------------------------------------------------------------------------------------");
        for (Loan loan: loanList){
            Copy c = LibraryController.getCopyById(loan.getCopyID());
            Book thisBook = LibraryController.getBookById(c.getBookID());
            User thisUser = UserController.getUserById(loan.getUserID());
            System.out.println("| " + Utils.fitString(loan.getCopyID() + "", 7) +
                    " | " + Utils.fitString(thisBook.getTitle(), 31) +
                    " | " + Utils.fitString( thisUser.getFirstName() + " " + thisUser.getLastName(), 28) +
                    " | " + Utils.fitString(loan.getDateDue().toString(), 11) + " |");
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }
    private static void checkAvailability(Scanner sc){
    	boolean validEntry = false;
    	Integer id = null;
    	String input = null;
    	
    	while(!validEntry) {
    		System.out.println("Enter the id of the book copy you'd like to check.");
        	input = sc.next();
        	try {
        		id = Integer.parseInt(input);
        		if(id == null || id <0) {
        			System.out.println("Invalid Entry!");
        			continue;
        		} else {
        			validEntry = true; 
        		}
        	} catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number >0.");
            }
        	
    	}
        
        Copy copy = LibraryController.getCopyById(id.intValue());
        Loan loan = LibraryController.checkIfCopyHasLoan(id.intValue());
        Book book = LibraryController.getBookById(copy.getBookID());
        
        if (book == null) {
        	System.out.println("Book does not exist.");
        } else if (loan== null){
            System.out.println("Book is available.");
        } else {
            System.out.println("This book was checked out on " + loan.getDateOut().toString());
            System.out.println("It will be returned after " + loan.getDateDue().toString());
        }
    }
    private static void checkoutBook(Scanner sc){
    	boolean validEntry = false; 
    	String input = null; 
    	Integer id = null; 
    	
    	while(!validEntry) {
    		System.out.println("Enter the id of the book you'd like to borrow.");
        	input = sc.next();
        	try {
        		id = Integer.parseInt(input);
        		if(id == null || id <0) {
        			System.out.println("Invalid Entry!");
        			continue;
        		} else {
        			validEntry = true; 
        		}
        	} catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number >0.");
            }
        	
    	}
    	if(LibraryController.getCopyById(id) != null) {  	
    		LibraryController.checkoutBook(id, UserController.getCurrentUser());
    	} else {
    		System.out.println("Book Copy not Found!");
    	}
    }
    private static void depositBook(Scanner sc){
    	
    	boolean validEntry = false; 
    	String input = null; 
    	Integer id = null; 
    	while(!validEntry) {
    		System.out.println("Enter the book copy ID to deposit:");
        	input = sc.next();
        	try {
        		id = Integer.parseInt(input);
        		if(id == null || id <0) {
        			System.out.println("Invalid Entry!");
        			continue;
        		} else {
        			validEntry = true; 
        		}
        	} catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number >0.");
            }
        	
    	}
    	if(LibraryController.getCopyById(id) == null) {
    		System.out.println("Copy not found!"); 
    	} else {
    		LibraryController.depositBook(id, UserController.getCurrentUser());
    	}
    }
    private static void genUserReport(Scanner sc){
        List<Loan> loanList = LibraryController.generateCheckoutBookList(UserController.getCurrentUser().getID());
        System.out.println("------------------------------------------------------------------------------------------"); //90 dashes
        System.out.println("| Book Id | Title                           | Author                       | Due back by |");
        System.out.println("------------------------------------------------------------------------------------------");
        if( loanList != null && !loanList.isEmpty()) {
	        for (Loan loan: loanList){
	            Copy c = LibraryController.getCopyById(loan.getCopyID());
	            Book thisBook = LibraryController.getBookById(c.getBookID());
	            System.out.println("| " + Utils.fitString(loan.getCopyID() + "", 7) +
	                    " | " + Utils.fitString(thisBook.getTitle(), 31) +
	                    " | " + Utils.fitString( thisBook.getAuthor(), 28) +
	                    " | " + Utils.fitString(loan.getDateDue().toString(), 11) + " |");
	        }
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

    private static void addCopy(Scanner sc) {
    	boolean validEntry = false; 
    	String input = null; 
    	Integer id = null; 
    	while(!validEntry) {
    		System.out.println("Enter the BookID which is is a copy of: ");
        	input = sc.next();
        	try {
        		id = Integer.parseInt(input);
        		if(id == null || id <0) {
        			System.out.println("Invalid Entry!");
        			continue;
        		} else {
        			validEntry = true; 
        		}
        	} catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number >0.");
            }
        	validEntry = true; 
    	}
    	if(!LibraryController.bookExists(id)) {
    		System.out.println("Book Record Not Found!");
    	} else {
    		LibraryController.createBookCopy(id);
    	}
    	
    }
    private static void removeCopy(Scanner sc) {
    	
    	boolean validEntry = false; 
    	String input = null; 
    	Integer id = null; 
    	while(!validEntry) {
    		System.out.println("Enter the Copy ID to delete: ");
        	input = sc.next();
        	try {
        		id = Integer.parseInt(input);
        		if(id == null || id <0) {
        			System.out.println("Invalid Entry!");
        			continue;
        		} else {
        			validEntry = true; 
        		}
        	} catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number >0.");
            }
        	validEntry = true; 
    	}
    	
   		LibraryController.deleteBookCopyById(id.intValue());
    }
}
