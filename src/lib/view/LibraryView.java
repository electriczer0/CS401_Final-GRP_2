package lib.view;

import lib.controller.LibraryController;
import lib.controller.UserController;
import lib.model.User;

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
        if (UserController.getCurrentUser().getType() == "Librarian"){
            actions.add(LibraryAction.ADD_USER);
            actions.add(LibraryAction.REMOVE_USER);
            actions.add(LibraryAction.LIST_USERS);
            actions.add(LibraryAction.ADD_BOOK);
            actions.add(LibraryAction.REMOVE_BOOK);
            actions.add(LibraryAction.GENERATE_LIBRARY_CHECKOUT_REPORT);
        }
        if (UserController.getCurrentUser().getType() == "Patron"){
            actions.add(LibraryAction.CHECKOUT_BOOK);
            actions.add(LibraryAction.DEPOSIT_BOOK);
        }
        while (!exiting){
            System.out.println("Please select one of the actions available, or 'exit' to exit.");
            for (int i = 0; i < actions.size(); i++){
                System.out.println((i + 1) + ". " + printAction(actions.get(i)));
            }
            String input = sc.next();
            if (input != "exit") {
                LibraryAction action = actions.get(Integer.parseInt(input) - 1); //Sanitize
                dispatchAction(action, sc);
            } else {
                exiting = true;
            }
        }
        return 0;

    }

    private static String printAction(LibraryAction action){
        switch (action){
            case ADD_USER -> {return "Add a new user to the list of library patrons.";}
            case REMOVE_USER -> {return "Remove a user from the list of library patrons.";}
            case LIST_USERS -> {return "List all users.";}
            case ADD_BOOK -> {return "Add a book to the collection.";}
            case REMOVE_BOOK -> {return "Remove a book from the collection.";}
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
            case LIST_USERS -> {listUsers(sc); break;}
            case ADD_BOOK -> {addNewBook(sc); break;}
            case REMOVE_BOOK -> {removeBook(sc); break;}
            case LIST_BOOKS -> {listBooks(sc); break;}
            case GENERATE_LIBRARY_CHECKOUT_REPORT -> {genLibReport(sc); break;}
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
        return;
    }

    private static void removeUser(Scanner sc){
        System.out.println("What is the id of the user you want to remove?");
        String id = sc.next();
        LibraryController.deleteUserById(id);
        return;
    }
    private static void listUsers(Scanner sc){
        List<User> userList = LibraryController.listUsers();

    }
    private static void addNewBook(Scanner sc){
        System.out.println("What is the new book's title?");
        String title = sc.next();
        System.out.println("Who is the author?");
        String author = sc.next();
        System.out.println("What's the ISBN?");
        String isbn = sc.next();
        LibraryController.addNewBook(title, author, isbn);
        return;
    }
    private static void removeBook(Scanner sc){
        return;
    }
    private static void listBooks(Scanner sc){
        return;
    }
    private static void genLibReport(Scanner sc){
        return;
    }
    private static void checkAvailability(Scanner sc){
        return;
    }
    private static void checkoutBook(Scanner sc){
        return;
    }
    private static void depositBook(Scanner sc){
        return;
    }
    private static void genUserReport(Scanner sc){
        return;
    }

}
