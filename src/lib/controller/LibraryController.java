package lib.controller;

public class LibraryController {
    public static void addNewBook(String title, String author, String isbn){
        return;
    }
}
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

import java.util.*;

class LibraryController{
	
	private static ArrayList<ArrayList<String>> books = new ArrayList<>();
	
	LibraryController(){}
	
	public static void addbook(Scanner input){
		
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
	
	public static void removeBook(Scanner input) {
		
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
	
	public static boolean searchBook(Scanner input){
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
	
	public static void listAllBook() {
		for(int i = 0; i < books.size(); i++) {
			System.out.println(books.get(i));
		}
	}
}
