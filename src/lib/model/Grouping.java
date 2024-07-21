package lib.model;


import java.util.Date;
import java.util.List;

public abstract class Grouping implements Has_ID {

	protected int id; //-1 will be the null value for this field

	//The user that owns this group, typically the person who made it
	protected int owner_Id;
	protected String name;
	protected String description;
	protected Date timestamp;
	
	//defines the object type distinguishing subtypes
	protected String type; 

		
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Group ID:\t")
			.append(this.id)
			.append("\nOwner ID:\t")
			.append(this.owner_Id)
			.append("\nName:\t")
			.append(this.name)
			.append("\nDescription:\t")
			.append(this.description)
			.append("\n");
		return output.toString();
		
	}
	
	
	
	public int getID() {return this.id;}
	
	public int getOwnerId() {return this.owner_Id;}
	public String getName() {return this.name;}
	public String getDescription() {return this.description;}
	public Date getTimestamp() {return this.timestamp;}
	public String getType() { return this.type;}

	@Override
	public void setID(int id){this.id = id;}
	public void setOwnerId(int id) { this.owner_Id = id; }
	public void setName(String name) { this.name = name; }
	public void setDescription(String desc) { this.description = desc; }
	public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
	public void setType(String type) {this.type = type;}

	
	// Overriding equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false;  // Check if the classes are the same

        Group gr = (Group) o;

        if (id != gr.getID()) {return false;}  // Compare the ids
        if (owner_Id != gr.getOwnerId()) { return false; }
        if(!description.equals(gr.getDescription())) { return false; }
        if(!name.equals(gr.getName())) { return false;}
        if(!timestamp.equals(gr.getTimestamp())) { return false; }
        if(!type.equals(this.getType())) {return false; }
        
        return true;
		//We should enforce id uniqueness in the DB.

    }

    // Overriding hashCode method
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }
    
      
    


}
