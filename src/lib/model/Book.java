package lib.model;
import java.util.Date;

import lib.db.*;
import lib.model.*;


public class Book implements Has_ID, Has_Copy<Book>{
	private int Book_Id = -1; //-1 will be the null value for this field
	private String Author;
	private String ISBN;
	private String Title;
	
	
		
	protected Book() {
		
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
	
	// Overriding equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false;  // Check if the classes are the same

        Book book = (Book) o;

        if (Book_Id != book.getID()) return false;  // Compare the ids
        if (!ISBN.equals(book.getISBN())) return false;  // Compare the ISBNs
        if (!Author.equals(book.getAuthor())) return false;  // Compare the titles
        return Title.equals(book.getTitle());  // Compare the authors
    }

    // Overriding hashCode method
    @Override
    public int hashCode() {
        int result = Book_Id;
        result = 31 * result + Author.hashCode();
        result = 31 * result + ISBN.hashCode();
        result = 31 * result + Title.hashCode();
        return result;
    }
    
    public static Book create(int Book_Id, String Author, String ISBN, String Title) {
		/**
		 * Factory for instantiating Book objects
		 * @param Book_Id primary key must be unique, can be -1 representing null (unknown) value
		 * @param Author String representing author
		 * @param ISBN String representing ISBN 10 or 13 digits alpha-numeric. 
		 * @param Title String representing book title 
		 */
		
		Book book = new Book();
		book.setID(Book_Id);
		book.setAuthor(Author);
		book.setISBN(ISBN);
		book.setTitle(Title);
		return book;
	}
    
    public static Book create() {
    	return new Book(); 
    }
    
    public static Book copy(Book book) {
    	/**
    	 * Create a deep copy of book
    	 */
    	return Book.create(book.getID(), book.getAuthor(), book.getISBN(), book.getTitle());
    }
    
    public Book copy() {
    	/**
    	 * Return a deep copy of this Book instance
    	 */
    	return Book.copy(this);
    }

}
