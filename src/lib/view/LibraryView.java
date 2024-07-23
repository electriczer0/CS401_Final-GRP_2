package lib.view;

import lib.controller.LibraryController;
import lib.controller.UserController;
import lib.model.Book;
import lib.model.Copy;
import lib.model.Loan;
import lib.model.User;
import lib.utilities.Utils;

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
    CHECK_BOOK_AVAILABLE,
    CHECKOUT_BOOK,
    DEPOSIT_BOOK,
    GENERATE_USER_CHECKOUT_REPORT
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
        actions.add(LibraryAction.CHECK_BOOK_AVAILABLE);
        actions.add(LibraryAction.GENERATE_USER_CHECKOUT_REPORT);
        if (UserController.getCurrentUser().getType().equals("Librarian")){
            actions.add(LibraryAction.ADD_USER);
            actions.add(LibraryAction.REMOVE_USER);
            actions.add(LibraryAction.LIST_USERS);
            actions.add(LibraryAction.ADD_BOOK);
            actions.add(LibraryAction.REMOVE_BOOK);
            actions.add(LibraryAction.GENERATE_LIBRARY_CHECKOUT_REPORT);
        }
        if (UserController.getCurrentUser().getType().equals("Patron")){
            actions.add(LibraryAction.CHECKOUT_BOOK);
            actions.add(LibraryAction.DEPOSIT_BOOK);
        }
        while (!exiting){
            System.out.println("Please select one of the actions available, or 'exit' to exit.");
            System.out.println("You'll need a book id to check out or deposit a book.");
            System.out.println("Type 'social' to switch to the Social Media platform.");
            for (int i = 0; i < actions.size(); i++){
                System.out.println((i + 1) + ". " + printAction(actions.get(i)));
            }
            String input = sc.next();
            if (input.equals("social")){
                return 1;
            } else {
                if (!input.equals("exit")) {
                    LibraryAction action = actions.get(Integer.parseInt(input) - 1); //Sanitize
                    dispatchAction(action, sc);
                } else {
                    exiting = true;
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
            case ADD_BOOK -> {return "Add a copy of a book to the collection.";}
            case REMOVE_BOOK -> {return "Remove a copy of a book from the collection.";}
            case LIST_BOOKS -> {return "List all books in the collection.";}
            case GENERATE_LIBRARY_CHECKOUT_REPORT -> {return "Print a list of all book loans.";}
            case CHECK_BOOK_AVAILABLE -> {return "Check a book for availability.";}
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
            case GENERATE_LIBRARY_CHECKOUT_REPORT -> {genLibReport(); break;}
            case CHECK_BOOK_AVAILABLE -> {checkAvailability(sc); break;}
            case CHECKOUT_BOOK -> {checkoutBook(sc); break;}
            case DEPOSIT_BOOK -> {depositBook(sc); break;}
            case GENERATE_USER_CHECKOUT_REPORT -> {genUserReport(sc); break;}
            default -> {return;}
        }
    }

    private static void addNewUser(Scanner sc){
        System.out.println("What is the new user's first name?");
        String fn = sc.next();
        System.out.println("What is the new user's last name?");
        String ln = sc.next();
        System.out.println("Type out this user's role. Ex. 'Patron' or 'Librarian'.");
        String type = sc.next();
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
        System.out.println("What is the new book's title?");
        String title = sc.next();
        System.out.println("Who is the author?");
        String author = sc.next();
        System.out.println("What's the ISBN?");
        String isbn = sc.next();
        LibraryController.addNewBook(title, author, isbn);
    }
    private static void removeBook(Scanner sc){
        System.out.println("What is the book's id?");
        String id = sc.next();
        LibraryController.deleteBookById(id);
    }
    private static void listBooks(){
    	//TODO reconsider logic. This will print X copies of each book where X is the number of copies we have. 
    	// Instead, perhaps we should call to Book.readAll() directly. or if we want to ensure that there is >0 copies of the book
    	// we could load the copy table, and then run a filter for unique BookIDs
        List<Copy> bookList = LibraryController.listCopies();
        System.out.println("------------------------------------------------------------------------------------------"); //90 dashes
        System.out.println("| Id   | Title                          | Author                         | ISBN          |");
        System.out.println("------------------------------------------------------------------------------------------");
        for (Copy copy: bookList){
            //For every copy of the book, we look up the original book.
            Book thisbook = LibraryController.getBookById(copy.getID());
            if(thisbook != null) {
	            System.out.println("| " + Utils.fitString(copy.getID() + "", 4) +
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
    	//TODO need to work on logic here. what if entered book id doesn't exist? 
        System.out.println("Enter the id of the book copy you'd like to check.");
        String id = sc.next();
        
        Copy copy = LibraryController.getCopyById(Integer.parseInt(id));
        Loan loan = LibraryController.checkIfCopyHasLoan(id);
        Book book = LibraryController.getBookById(copy.getBookID());
        
        if (book == null) {
        	System.out.println("Book does not exist.");
        } else if (loan == null){
            System.out.println("Book is available.");
        } else {
            System.out.println("This book was checked out on " + loan.getDateOut().toString());
            System.out.println("It will be returned after " + loan.getDateDue().toString());
        }
    }
    private static void checkoutBook(Scanner sc){
        System.out.println("Enter the id of the book you'd like to borrow.");
        String id = sc.next();
        LibraryController.checkoutBook(id, UserController.getCurrentUser());
    }
    private static void depositBook(Scanner sc){
        System.out.println("Enter the id of the book you'd like to deposit.");
        String id = sc.next();
        LibraryController.depositBook(id, UserController.getCurrentUser());
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

}
/*
-----------------------------------------------------------------------------------------------------------------------------------------------------------
Here is my code. -- Jian Xiong Lu

public class LibraryView {

	
	private static void removeUser(Scanner sc){
		System.out.println("Enter member's first name: ");
		String firstName = sc.next();
		System.out.println("Enter member's Last name: ");
		String lastName = sc.next();
		System.out.println("Enter your Library member ID");
		String id = sc.next();
		String type = "Deprecated";
		UserController.removeMemeber(firstName,lastName,id,type);
    }
	private static void listUsers(){
        UserController.listAllusers();
    }
    //....
    private static void removeBook(Scanner sc){
    	System.out.println("Enter deprecated book's title: ");
		String title = sc.next();
		System.out.println("Enter deprecated book's author");
		String author = sc.next();
		System.out.println("Enter deprecated book's ISBN");
		String ISBN = sc.next();
        LibraryController.removeBook(title, author, ISBN);
    }
    
    private static void listBooks(){
    	LibraryController.listAllAvaliableBook();
    }
    
    private static void genLibReport(Scanner sc){
    	LibraryController.Report();
    }
    private static void checkAvailability(Scanner sc){
    	System.out.println("Enter your book's title: ");
		String title = sc.next();
		System.out.println("Enter your new book's author");
		String author = sc.next();
		System.out.println("Enter your book's ISBN");
		String ISBN = sc.next();
		LibraryController.searchBook(title, author, ISBN);
    }
    private static void checkoutBook(Scanner sc){
    	System.out.println("Enter your Library member ID");
		String id = sc.next();
		System.out.println("Enter borrowing book's ISBN");
		String ISBN = sc.next();
		System.out.println("Enter borrowing book's due date(yyyy-mm-dd)");
		String dueDate = sc.next();
		LibraryController.borrowBook(id, ISBN, dueDate);
		
    }
    public void depositBook(Scanner sc){
        System.out.println("Enter returning book's ISBN");
		String ISBN = sc.next();
		System.out.println("Enter your Library member ID");
		String id = sc.next();
		System.out.println("Enter your book's actual returned date(yyyy-mm-dd)");
		String actualReturnedDate = sc.next();
		LibraryController.returnBook(id, ISBN, actualReturnedDate);
    }
    
    public void trackBooks(Scanner sc) {
		System.out.println("Enter tracking book's ISBN");
		String ISBN = sc.next();
		LibraryController.trackBooks(ISBN);
		}
    
    
    public static void genUserReport(){
        return;
    }
    
    public static void createNewUser(Scanner input){
		System.out.println("Enter member's first name: ");
		String firstName = input.next();
		System.out.println("Enter member's Last name: ");
		String lastName = input.next();
		System.out.println("Enter your Library member ID");
		String id = input.next();
		String type = "Registered";
		System.out.println("Congulation, You're the new member of our Library!");
		UserController.createNewUser(firstName,lastName, id,type);
    }
    
    public void updateMemberInfo(Scanner input) {
		System.out.println("Enter member's current first name: ");
		String firstName = input.next();
		System.out.println("Enter member's current Last name: ");
		String lastName = input.next();
		System.out.println("Enter your Library member ID");
		String id = input.next();
		String type = "Registered";
		UserController.updateMemberInfo(firstName,lastName, id,type);}
}
*/
