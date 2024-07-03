package library.model;
import java.util.Date;
import java.util.Vector;

import library.db.*;
import library.model.*;


public class Book {
	private int Book_Id;
	private String Author;
	private String ISBN;
	private String Title;
	private Vector<Copy> Copies;
	private Vector<Loan> Loans;
	
	public static class Copy{
		private int Copy_Id;
		private int Book_Id; 
		private Book Book_Ref;
		private Loan Loan_Ref = null;
		
		private Copy() {
			
		}
		
		public Copy(Book book) {
			// TODO implement
			this.Book_Ref = book;
			this.Book_Id = book.getID();
			//get new copyID from database
			
		}
		public Copy(Book book, int Copy_Id) {
			// TODO finish implementation
			this.Book_Ref = book;
			this.Book_Id = book.getID();
			this.Copy_Id = Copy_Id; 
			//push new copy to DB
			
		}
		public Copy(int Book_Id, int Copy_Id) {
			// TODO finish implementation
			this.Book_Id = Book_Id;
			this.Copy_Id = Copy_Id;
			//find book from Book_Id and link to Book_Ref
			//push new Copy to DB
		}
		
	}
	
	public static class Loan{
		private int Loan_Id;
		private int Copy_Id;
		private int User_Id;
		private Date Date_Out;
		private Date Date_Due;
		private Boolean Active = true; 
		private Copy Copy_Ref; 
		private User User_Ref; 
		private Book Book_Ref;
		
		private Loan() {
			
		}
		public Loan(int Copy_Id, int User_Id, Date Date_Out, Date Date_Due) {
			this.Copy_Id = Copy_Id;
			this.User_Id = User_Id; 
			this.Date_Out = Date_Out;
			this.Date_Due = Date_Due; 
			//push to DB and get new Loan_Id
			//note: we may need different constructors to handle loading records vs creating new records
			//link to copy, user, and book
			// TODO finish
			
			
		}
		public Loan(int Loan_Id) {
			//TODO finish
			//load loan record from DB based on Loan_Id
			
		}
		public Loan(int Loan_Id, int Copy_Id, int User_Id, Date Date_Out, Date Date_Due) {
			//TODO finish
		}
		public void Renew() {
			//TODO implement
		}
		public void Renew(Date Renew_Date) {
			//TODO implement
		}
		public void Return() {
			//TODO implement
		}
		public void Return(Date Return_Date) {
			
		}
	}
	
	public Book() {
		
	}
	public Book(int Book_Id, String Author, String ISBN, String title) {
		
	}
	
	@Override
	public String toString() {
		
	}
	
	public void addCopy(int Copy_Id) {
		
	}
	
	public int addCopy() {
		//creates a new copy, pulling a Copy_Id from database which is returned
		
		
	}
	
	public void remCopy(int Copy_Id) {
		//removes copy by Copy_Id
	}
	
	public int Count() {
		//returns count of copies
	}
	
	private int loadCopies() {
		//loads copies from db returning count
	}
	public Vector<Copy> getCopies(){
		//returns a list of all copies
	}
	
	public int getID() {
		return this.Book_Id;
	}
	
	public String getAuthor() {
		return this.Author;
	}
	public String getISBN() {
		return this.ISBN;
	}
	public String getTitle() {
		return this.Title;
	}
	public Vector<Loan> getLoans(){
		return this.Loans;
	}
	public void setAuthor(String Author) {
		this.Author = Author;
	}
	public void setISBN(String ISBN) {
		this.ISBN = ISBN;
	}
	public void setTitle(String Title) {
		this.Title = Title;
	}
	public void addLoan(int Copy_Id, int User_Id, Date Date_Out, Date Date_Due) {
		
	}
	public void returnLoan(int Loan_Id, Date Return_Date) {
		
	}
	public void renewLoan(int Loan_Id, Date Renew_Date) {
		
	}
	private void loadLoans() {
		//  TODO write loadLoans() method
		//loads loan records related to Book from DB
	}
	
	

}
