package lib.model;

public class UserAddressFactory {
	/**
	 * Static class for instantiating User_Address objects
	 */
	
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
	

}
