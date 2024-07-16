package lib.model;

public class BookFactory {
	
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

}
