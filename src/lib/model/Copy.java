package lib.model;

public class Copy implements Has_ID, Has_Copy {
    private Integer Copy_Id;
    private Integer Book_Id;

    protected Copy() {
    }

  

    @Override
    public int getID() {
        return Copy_Id;
    }

    @Override
    public void setID(int Copy_Id) {
        this.Copy_Id = Copy_Id;
    }

    public int getBookID() {
        return Book_Id;
    }

    public void setBookID(int Book_Id) {
        this.Book_Id = Book_Id;
    }
    
    public String toString() {
    	StringBuilder output = new StringBuilder();
		output.append("Copy ID:\t")
			.append(this.getID())
			.append("\nBook ID:\t")
			.append(this.getBookID())
			.append("\n");
		return output.toString();
    }
    
 // Overriding equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false;  // Check if the classes are the same

        Copy copy = (Copy) o;

        if (Copy_Id != copy.getID()) return false;  // Compare the ids
        
        return Book_Id == copy.getBookID();  // Compare the authors
    }

    // Overriding hashCode method
    @Override
    public int hashCode() {
        int result = Copy_Id;
        result = 31 * result + Book_Id;
        return result;
    }
    
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
    
    public static Copy create() {
    	/**
    	 * Factory for creating empty copy objects
    	 */
    	return new Copy();
    }
    
    public static Copy copy(Copy copy) {
    	/**
    	 * return a deep copy of copy
    	 */
    	return Copy.create(copy.getID(), copy.getBookID());
    }
    public Copy copy() {
    	/**
    	 * return a deep copy of this Copy instance
    	 */
    	return Copy.copy(this);
    }
}