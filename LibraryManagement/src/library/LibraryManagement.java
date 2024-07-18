package library;

import java.util.ArrayList;


class LibraryManagement{
	LibraryManagement(){}
	 
}

class book {
	private String author, title, ISBN;
	book(String title, String author, String ISBN){
		this.title = title;
		this.author = author;
		this.ISBN = ISBN;
		System.out.println(title + "\t" + author + "\t" + ISBN);
	}
	String getTitle() {return title;}
	String getAuthor() {return author;}
	String getISBN() {return ISBN;}
}

class BookManagement extends LibraryManagement{
	
	ArrayList<book> books = new ArrayList<>();
	
	protected void addBook(book book) {
		books.add(book);
	}
	
	protected void removeBook(book book) {
		books.remove(book);
	}
	
	protected void searchBook(ArrayList<book> books, String title) {
		
		for(book book:books) {
			if (book.getTitle() == title) {
				System.out.println("Book is found");
				System.out.println(book.getTitle() + "\t" + book.getAuthor() + "\t" + book.getISBN());
			}
		}
	}
	
	protected void listAllBook(String title, String author, String ISBN){
		
	}
}

class BorrowAndReturn extends LibraryManagement{
	private String ISBN;
	private int id;
	protected void borrowBook(String ISBN, int id) {}
	protected void returnBook(String ISBN, int id) {}
}

class Members extends LibraryManagement{
	private int id;
	protected void addMember(int id) {}
	protected void removeMemeber(int id) {}
	protected void listAllMember(){} 
}

class Report extends BorrowAndReturn{
	private String dueDate;
	private int id;
	protected void borrowedBooks(String dueDate, int id) {}
	protected void OverDueBooks(String dueDate) {}
}

