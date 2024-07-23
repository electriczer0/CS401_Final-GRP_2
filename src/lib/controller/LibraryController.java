package lib.controller;

import lib.db.Book_Access;
import lib.db.Copy_Access;
import lib.db.Loan_Access;
import lib.db.User_Access;
import lib.model.Book;
import lib.model.Copy;
import lib.model.Loan;
import lib.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The controller for library functions. Services the LibraryView.
 */
public class LibraryController {
    public static void addNewBook(String title, String author, String isbn){
        Book_Access bookTable = Book_Access.getInstance();
        Book book = Book.create(-1, author, isbn, title);
        try {
        	bookTable.insert(book);
        } catch (SQLException e) {
        	e.printStackTrace();
        }
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
     * Deletes a user by their Id. Related records, including loans will automatically be deleted by the DB
     * @param id
     */
    public static void deleteUserById(String id){
    	
    	 try {
             int idNum = Integer.parseInt(id);
             User_Access.getInstance().delete(idNum);
         } catch (NumberFormatException e) {
             System.out.println("Invalid string format: " + id);
         } catch (SQLException e) {
    		e.printStackTrace();
         }
    }


    /**
     * Returns a list of all Library users.
     * @return
     */
    public static List<User> listUsers(){
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
    	
    	
    	try {
            int idNum = Integer.parseInt(id);
            Copy_Access.getInstance().delete(idNum);
        } catch (NumberFormatException e) {
            System.out.println("Invalid string format: " + id);
        } catch (SQLException e) {
   		e.printStackTrace();
        }
    
    }

    /**
     * Returns a list of all copies of books in the library. Note that this should return *Copies*, not books.
     * @return
     */
    public static List<Copy> listCopies(){
        //Returns a list of all book copies.
    	List <Copy> returnList = null;
    	try {
    		returnList = new ArrayList<>(Copy_Access.getInstance().readAll(0, 1000).values());
    		
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return returnList;
    }

    /**
     * Returns all outstanding checked out books by the user_id. If user_id is null, returns all
     * checked out books. Used to generate library reports for loaned books as well as user reports
     * of their own borrowed books.
     * @param userId
     * @return
     */
    public static List<Loan> generateCheckoutBookList(Integer userId){
    	
    	List<Loan> returnList = null; 
    	try {
	    	if (userId == null) {
	    		returnList = new ArrayList<>(Loan_Access.getInstance().readAllActive().values());
	    	} else {
	    		returnList = new ArrayList<>(User_Access.getInstance().getActiveLoans((int) userId).values());
	    	}
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	
        return returnList;
    }

    /**
     * Looks up a book by id. A useful helper method.
     * @param id
     * @return
     */
    public static Book getBookById(int id){
    	Book book = null;
    	try {
    		book = Book_Access.getInstance().read(id);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return book; 
    }

    /**
     * Looks up a copy by id. A useful helper method.
     * @param id
     * @return
     */
    public static Copy getCopyById(int id){
    	Copy copy = null; 
    	try {
    		copy = Copy_Access.getInstance().read(id);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return copy; 
    
    }

    /**
     * Checks to see if a copy of a book is available, i.e. there are no current Loans for that book.
     * If there is one, returns a Loan so that the user can be made aware of when the book will be returned.
     * Returning null means that the book is available.
     * @param copyId
     * @return
     */
    public static Loan checkIfBookHasLoan(String copyId){
        Loan loan = null;
        
    	try {
    		int idNum = Integer.parseInt(copyId);
    		loan = Copy_Access.getInstance().getActiveLoan(idNum);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return loan;
    }

    /**
     * Checks a copy of a book out to the specified user. Creates a loan for that copy and marks the date.
     * Let's say that all book loans are for two weeks.
     * @param copyId
     */
    public static void checkoutBook(String copyId, User user){
    	
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.DAY_OF_YEAR, 14);
    	Date dueDate = calendar.getTime();
    	
    	if(checkIfBookHasLoan(copyId) != null) {
    		System.out.println("Error: The book is already checked out!");
    		return; 
    	}
    	
    	try {
    		int cId = Integer.parseInt(copyId);
    		Loan_Access.getInstance().insert(Loan.create(-1, cId, user.getID(), new Date(), dueDate, true));
    	} catch(SQLException e) {
    		e.printStackTrace();
    	}
    	System.out.println("Book Checked Out. Due date: " + dueDate);

    }

    /**
     * Deposits a book back in the library. Should mark the corresponding loan as inactive, if an active
     * loan exists for that copy,
     * for this user.
     * @param copyId
     */
    public static void depositBook(String copyId, User user){
    	Loan loan = null; 
    	try {
    		int cId = Integer.parseInt(copyId);
    		loan = Copy_Access.getInstance().getActiveLoan(cId);
    		if(loan != null) {
    			loan.setActive(false);
    			Loan_Access.getInstance().update(loan);
    		}
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
}
