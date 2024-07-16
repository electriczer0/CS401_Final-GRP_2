package lib.model;

public class CopyFactory {
	
	public static Copy create(int Copy_Id, int Book_Id) {
		/**
		 * Factory for instantiating instances of Copy class representing individual copies of a book
		 * @Copy_Id int primay key, must be unique, can be -1 representing null value 
		 * @Book_Id int foreign key to Book table, must exist in that table
		 */
		
		Copy copy = new Copy();
		copy.setID(Copy_Id);
		copy.setBookID(Book_Id);
		return copy; 
	}

}
