package lib.controller;

import lib.db.User_Access;
import lib.model.User;

import java.sql.SQLException;
import java.util.Scanner;

public class UserController {
    /**
     * This controls the current user across all views. userController.currentUser defines permissions.
     * Any view can import the UserController to switch the current user.
     */
    private static User currentUser = null;

    public static User getCurrentUser(){
        return currentUser;
    }

	public static void setCurrentUserById(String id){
		//Lookup the user by their id, then set currentUser to that User.
		try {
			int uId = Integer.parseInt(id);
			currentUser = User_Access.getInstance().read(uId);
			currentUser.setType(currentUser.getType());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    /**
     * Gets a user by ID.
     * @param id
     */
    public static User getUserById(int id){
    	
    	User user = null;
    	try {
    		user = User_Access.getInstance().read(id);
    	} catch(SQLException e) {
    		e.printStackTrace();
    	}
    	return user;
    }
}

/*
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class UserController {
	private ArrayList<ArrayList<String>> members = new ArrayList<>();
	
	public void createNewUser(Scanner input){
		System.out.println("Enter member's first name: ");
		String firstName = input.next();
		System.out.println("Enter member's Last name: ");
		String lasttName = input.next();
		System.out.println("Enter your Library member ID");
		String id = input.next();
		String type = "Registered";
		System.out.println("Congulation, You're the new member of our Library!");
		
		int i = 0;
		members.add(new ArrayList<String>());
		members.get(i).addAll(Arrays.asList(firstName, lasttName, id, type));
		i++;
		
	 }
	
	public void removeMemeber(Scanner input) {
		System.out.println("Enter member's first name: ");
		String firstName = input.next();
		System.out.println("Enter member's Last name: ");
		String lasttName = input.next();
		System.out.println("Enter your Library member ID");
		String id = input.next();
		String type = "Registered";
		
		for(int i = 0; i < members.size(); i++) {
			if (members.get(i).containsAll(Arrays.asList(firstName, lasttName, id, type))) {
				members.get(i).removeAll(Arrays.asList(firstName, lasttName, id, type));
			}
		}
		
	}
	
	public void updateMemberInfo(Scanner input) {
		System.out.println("Enter member's current first name: ");
		String firstName = input.next();
		System.out.println("Enter member's current Last name: ");
		String lasttName = input.next();
		System.out.println("Enter your Library member ID");
		String id = input.next();
		String type = "Registered";
		for(int i = 0; i < members.size(); i++) {
			if (members.get(i).containsAll(Arrays.asList(firstName, lasttName, id, type))) {
				members.get(i).removeAll(Arrays.asList(firstName, lasttName, id, type));
				
				System.out.println("Enter your new first name: ");
				String newFirstName = input.next();
				System.out.println("Enter your new Last name: ");
				String newLasttName = input.next();
				System.out.println("Enter your Library member ID now in use: ");
				String newId = input.next();
				members.get(i).addAll(Arrays.asList(newFirstName, newLasttName, newId, type));
			}
		}
	}
	
	public void listAllusers(){
		for(int i = 0; i < members.size(); i++) {
			System.out.println(members.get(i));
		}
	} 
	
	
	public void borrowBook(Scanner input) {
		System.out.println("Enter borrowing book's ISBN");
		String ISBN = input.next();
		System.out.println("Enter your Library member ID");
		int id = input.nextInt();
		
		System.out.println("Book " + ISBN +  " is brrowed by member " + id);
	}
	
	public void returnBook(Scanner input) {
		
		System.out.println("Enter returning book's ISBN");
		String ISBN = input.next();
		System.out.println("Enter your Library member ID");
		int id = input.nextInt();
		
		System.out.println("Book " + ISBN +  " is returned by member " + id);
	}
}

*/