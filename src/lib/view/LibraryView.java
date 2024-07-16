package lib.view;

/**
 * This static class manages the user-facing library interactions CLI.
 * A user who is a librarian can add and remove books from the library collection, add and remove users
 * from the library, and generate reports.
 * A user who is a patron can check books out, return books, and see what books they've borrowed and when they're due.
 */
public class LibraryView {


    /**
     * Initial intake. Returning a '1' indicates the user wants to switch modes, and returning a '0' indicates
     * that the user wants to quit.
     */
    public static int basePrompt(){

        System.out.println("Welcome to the Library Manager.");


    }

}
