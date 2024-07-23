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
