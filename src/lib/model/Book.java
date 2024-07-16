package lib.model;
import java.util.Date;

import lib.db.*;
import lib.model.*;


public class Book implements Has_ID{
	private int Book_Id = -1; //-1 will be the null value for this field
	private String Author;
	private String ISBN;
	private String Title;
	
	
		
	public Book() {
		
	}
	public Book(int Book_Id, String Author, String ISBN, String title) {
		
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Book ID:\t")
			.append(this.Book_Id)
			.append("\nTitle:\t")
			.append(this.Title)
			.append("\nAuthor:\t")
			.append(this.Author)
			.append("\nISBN:\t")
			.append(this.ISBN)
			.append("\n");
		return output.toString();
		
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
	
	public void setAuthor(String Author) {
		this.Author = Author;
	}
	public void setISBN(String ISBN) {
		this.ISBN = ISBN;
	}
	public void setTitle(String Title) {
		this.Title = Title;
	}
	public void setID(int Book_Id) {
		this.Book_Id = Book_Id;
	}
	
	

}
