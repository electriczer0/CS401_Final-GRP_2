package lib.controller;

import lib.db.Copy_Access;
import lib.db.User_Access;
import lib.model.Book;
import lib.model.Copy;
import lib.model.Loan;
import lib.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LibraryController {
    public static void addNewBook(String title, String author, String isbn){
        return;
    }

    /**
     * Creates a new user in the DB. The Library is an implicit construct - all users in the DB are
     * considered library users.
     * @param firstName
     * @param lastName
     * @param type
     */
    public static void createNewUser(String firstName, String lastName, String type){
        //Create a new user and save to db. This user is a member of the library.
        try {
            User_Access accessor = User_Access.getInstance();
            User k = User.create(-1, firstName, lastName, type);
            accessor.insert(k);
        } catch (SQLException e){
            return;
        }
    }

    /**
     * Deletes a user by their Id. This should both remove a user and also clean up anything related to
     * that user (i.e. auto returns all books that user checked out).
     * @param id
     */
    public static void deleteUserById(String id){
        //Delete a user from the library by their id.
    }

    /**
     * Returns a list of all Library users.
     * @return
     */
    public static List<User> listUsers(){
        //Delete a user from the library by their id.
        try {
            User_Access accessor = User_Access.getInstance();
            Map<Integer, User> map = accessor.readAll(0, 1000);

            Collection<User> col = map.values();
            return new ArrayList<User>(col);
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Deletes a copy of a book from the library. Should also delete any outstanding loan for that book.
     * @param id
     */
    public static void deleteBookById(String id){
        return;
    }

    /**
     * Returns a list of all copies of books in the library. Note that this should return *Copies*, not books.
     * @return
     */
    public static List<Copy> listCopies(){
        //Returns a list of all book copies.
        return new ArrayList<>();
    }

    /**
     * Returns all outstanding checked out books by the user_id. If user_id is null, returns all
     * checked out books. Used to generate library reports for loaned books as well as user reports
     * of their own borrowed books.
     * @param userId
     * @return
     */
    public static List<Loan> generateCheckoutBookList(Integer userId){
        return new ArrayList<Loan>();
    }

    /**
     * Looks up a book by id. A useful helper method.
     * @param id
     * @return
     */
    public static Book getBookById(int id){
        return Book.create();
    }

    /**
     * Looks up a copy by id. A useful helper method.
     * @param id
     * @return
     */
    public static Copy getCopyById(int id){
        return Copy.create();
    }

    /**
     * Checks to see if a copy of a book is available, i.e. there are no current Loans for that book.
     * If there is one, returns a Loan so that the user can be made aware of when the book will be returned.
     * Returning null means that the book is available.
     * @param copyId
     * @return
     */
    public static Loan checkIfBookHasLoan(String copyId){
        return null;
    }

    /**
     * Checks a copy of a book out to the specified user. Creates a loan for that copy and marks the date.
     * Let's say that all book loans are for two weeks.
     * @param copyId
     */
    public static void checkoutBook(String copyId, User user){

    }

    /**
     * Deposits a book back in the library. Should mark the corresponding loan as inactive, if an active
     * loan exists for that copy,
     * for this user.
     * @param copyId
     */
    public static void depositBook(String copyId, User user){

    }
}
/*
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

import java.util.*;

class LibraryController{
	
	private ArrayList<ArrayList<String>> books = new ArrayList<>();
	
	LibraryController(){}
	
	public void addbook(Scanner input){
		
		System.out.println("Enter new book's title: ");
		String title = input.next();
		System.out.println("Enter new book's author");
		String author = input.next();
		System.out.println("Enter new book's ISBN");
		String ISBN = input.next();
	
		int i = 0;
		books.add(new ArrayList<String>());
		books.get(i).addAll(Arrays.asList(title, author, ISBN));
		i++;
		
	}
	
	public void removeBook(Scanner input) {
		
		System.out.println("Enter deprecated book's title: ");
		String title = input.next();
		System.out.println("Enter deprecated book's author");
		String author = input.next();
		System.out.println("Enter deprecated book's ISBN");
		String ISBN = input.next();
		
		for(int i = 0; i < books.size(); i++) {
			if (books.get(i).containsAll(Arrays.asList(title, author, ISBN))) {
				books.get(i).removeAll(Arrays.asList(title, author, ISBN));
			}
		}

	}
	
	public boolean searchBook(Scanner input){
		System.out.println("Enter your book's title: ");
		String title = input.next();
		System.out.println("Enter your new book's author");
		String author = input.next();
		System.out.println("Enter your book's ISBN");
		String ISBN = input.next();
		
		for(int i = 0; i < books.size(); i++) {
			if (books.get(i).containsAll(Arrays.asList(title, author, ISBN))) {
				return true;
			}
		}
		return false;
	}
	
	public void listAllBook() {
		for(int i = 0; i < books.size(); i++) {
			System.out.println(books.get(i));
		}
	}
}
*/