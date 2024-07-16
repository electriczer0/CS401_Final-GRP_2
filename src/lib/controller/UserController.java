package lib.controller;

import lib.model.User;

import java.util.Scanner;

public class UserController {
    /**
     * This controls the current user across all views. userController.currentUser defines permissions.
     * Any view can import the UserController to switch the current user.
     */
    private static User currentUser = null;

    public static void initUser(Scanner sc){
        while (currentUser == null) {
            System.out.println("Who is using this system? Enter an id, or type 'list' to list all users.");
            String input = sc.next();
            if (input == "exit") {
                return;
            }
            if (input == "list") {
                //Library.listAllusers();
            } else {
                //currentUser = Library.getUserById(input);
            }
        }
    }

    public static User getCurrentUser(){
        return currentUser;
    }
}
