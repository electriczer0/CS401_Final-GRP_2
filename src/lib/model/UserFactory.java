package lib.model;

public class UserFactory {
	
	public static User create(int User_Id, String FirstName, String LastName, String Type) {
		/**
		 * Primary factory for instantiating User objects
		 * @param User_Id primary key to User table, must be unique, can be -1 for null values (unknown)
		 * @param FirstName String representing first name
		 * @param LastName String representing last name
		 * @param Type String representing user type, can be "Patron" or "Librarian" 
		 */
		
		User user = new User();
		user.setID(User_Id);
		user.setFirstName(FirstName);
		user.setLastName(LastName);
		user.setType(Type);
		return user; 
	}

}
