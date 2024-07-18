package lib.model;

public class User_Address implements Has_ID, Has_Copy {
    private Integer Address_Id;
    private Integer User_Id;
    private String Street1;
    private String Street2;
    private String City;
    private String Zip;
    private String State;

    public User_Address() {
    }

    @Override
    public int getID() {
        return Address_Id;
    }

    @Override
    public void setID(int Address_Id) {
        this.Address_Id = Address_Id;
    }

    public Integer getUserID() {
        return User_Id;
    }

    public void setUserID(int User_Id) {
        this.User_Id = User_Id;
    }

    public String getStreet1() {
        return Street1;
    }

    public void setStreet1(String Street1) {
        this.Street1 = Street1;
    }

    public String getStreet2() {
        return Street2;
    }

    public void setStreet2(String Street2) {
        this.Street2 = Street2;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String City) {
        this.City = City;
    }

    public String getZip() {
        return Zip;
    }

    public void setZip(String Zip) {
        this.Zip = Zip;
    }

    public String getState() {
        return State;
    }

    public void setState(String State) {
        this.State = State;
    }
    
 // Overriding equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false;  // Check if the classes are the same

        User_Address address = (User_Address) o;

        if (Address_Id != address.getID()) return false;  // Compare the ids
        if (User_Id != address.getUserID()) return false;
        if (!City.equals(address.getCity())) return false;
        if (!State.equals(address.getState())) return false;
        if (!Street1.equals(address.getStreet1())) return false;
        if (!Street2.equals(address.getStreet2())) return false;
        
        return Zip.equals(address.getZip());  // Compare the authors
    }

    // Overriding hashCode method
    @Override
    public int hashCode() {
        int result = Address_Id;
        result = 31 * result + User_Id;
        result = 31 * result + City.hashCode();
        result = 31 * result + State.hashCode();
        result = 31 * result + Street1.hashCode();
        result = 31 * result + Street2.hashCode();
        result = 31 * result + Zip.hashCode();
      
        return result;
    }
    
    public static User_Address create(int Address_Id, int User_Id, String Street1, String Street2, String City, String State, String Zip) {
		/**
		 * Primary method for instantiating User_Address Object
		 * @param Address_Id int primary key, must be unique can be -1 representing null
		 * @param User_Id int foreign key to user table must exist in that table
		 * @param Street1 String representation of street
		 * @param Street2 String representation of street 2
		 * @param City String representation of city
		 * @param State String representation 2 digit alphabetical state abbreviation
		 * @param Zip String representation of 5 digit zip code (alpha only) 
		 */
		
		User_Address address = new User_Address();
		address.setID(Address_Id);
		address.setUserID(User_Id);
		address.setStreet1(Street1);
		address.setStreet2(Street2);
		address.setCity(City);
		address.setState(State);
		address.setZip(Zip);
		
		return address;
	}
    
    public static User_Address copy(User_Address address) {
    	/**
    	 * Creates a deep copy of User_Address object
    	 * @param User_Address the address to be copied
    	 * @return a User_Address object which is a deep copy of address
    	 */
    	return User_Address.create(address.getID(), address.getUserID(), address.getStreet1(),
    			address.getStreet2(), address.getCity(), address.getState(), address.getZip());
    }
    
    public User_Address copy() {
    	return User_Address.copy(this);	
    }
}

