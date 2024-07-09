package library.model;

public class Copy implements Has_ID {
    private Integer Copy_Id;
    private Integer Book_Id;

    public Copy() {
    }

    public Copy(Integer Copy_Id, Integer Book_Id) {
        this.Copy_Id = Copy_Id;
        this.Book_Id = Book_Id;
    }

    @Override
    public int getID() {
        return Copy_Id;
    }

    @Override
    public void setID(int Copy_Id) {
        this.Copy_Id = Copy_Id;
    }

    public Integer getBookID() {
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
}