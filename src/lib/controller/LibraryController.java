package lib.controller;

public class LibraryController {
    public static void addNewBook(String title, String author, String isbn){
        return;
    }

    public static void createNewUser(String firstName, String lastName, String type){
        //Create a new user and save to db. This user is a member of the library.
    }

    public static void deleteUserById(String id){
        //Delete a user from the library by their id.
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