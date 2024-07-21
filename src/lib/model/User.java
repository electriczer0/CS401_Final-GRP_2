package lib.model;

public class User implements Has_ID, Has_Copy<User> {
    private int UserID;
    private String Name_First;
    private String Name_Last;
    private String Type;

    protected User() {
    }

    @Override
    public int getID() {
        return UserID;
    }

    @Override
    public void setID(int User_Id) {
        this.UserID = User_Id;
    }

    public String getFirstName() {
        return Name_First;
    }

    public void setFirstName(String Name_First) {
        this.Name_First = Name_First;
    }

    public String getLastName() {
        return Name_Last;
    }

    public void setLastName(String Name_Last) {
        this.Name_Last = Name_Last;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }
    
 // Overriding equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false;  // Check if the classes are the same

        User user = (User) o;

        if (UserID != user.getID()) return false;  // Compare the ids
        if (!Name_First.equals(user.getFirstName())) return false;
        if (!Name_Last.equals(user.getLastName())) return false;
        
        
        return Type.equals(user.getType());  // Compare the types
    }

    // Overriding hashCode method
    @Override
    public int hashCode() {
        int result = UserID;
        result = 31 * result + Name_First.hashCode();
        result = 31 * result + Name_Last.hashCode();
        result = 31 * result + Type.hashCode();
        
        return result;
    }
    
    @Override
    public String toString() {
    	StringBuilder output = new StringBuilder();
		output.append("User ID:\t")
			.append(this.UserID)
			.append("\nFirst Name:\t")
			.append(this.Name_First)
			.append("\nLast Name:\t")
			.append(this.Name_Last)
			.append("\nType:\t")
			.append(this.Type)
			.append("\n");
		return output.toString();
    	
    }
    
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
    
    public static User create() {
    	/**
    	 * User Factory for generating blank User objects
    	 */
    	return new User();
    }
    
    public static User copy(User user) {
    	/**
    	 * Create a deep copy of user
    	 * @param user the User object to be copied
    	 * @return a User object which is a deep copy of user param. 
    	 * 
    	 */
    	return User.create(user.getID(), user.getFirstName(), user.getLastName(), user.getType());
    }
    
    public User copy() {
    	/**
    	 * Create a deep copy of this User instance
    	 */
    	return User.copy(this);
    }
}