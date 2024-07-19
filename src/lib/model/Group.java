package lib.model;

import java.util.Date;
import java.util.List;

/**
 * A group of interactions. A user can create a group and post interactions to it; other users can join or leave
 * that group.
 */
public class Group implements Has_ID, Has_Interactions {
	protected Integer id = -1; //-1 will be the null value for this field

	//The user that owns this group, typically the person who made it
	protected Integer owner_Id;

	//A list of associated interactions in this group.
	protected List<Interaction> interactions; 

	protected String name;
	protected String description;
	protected Date timestamp;



	public Group() {
		
	}
		
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Group ID:\t")
			.append(this.id)
			.append("\nOwner ID:\t")
			.append(this.owner_Id)
			.append("\nInteraction Count:\t")
			.append(this.interactions.size())
			.append("\nName:\t")
			.append(this.name)
			.append("\nDescription:\t")
			.append(this.description)
			.append("\n");
		return output.toString();
		
	}
	
	
	
	public int getID() {
		return this.id;
	}
	
	public Integer getOwnerId() {
		return this.owner_Id;
	}
	public String getName() {
		return this.name;
	}
	public String getDescription() {
		return this.description;
	}
	public List<Interaction> getInteractions(){
		return this.interactions;
	}
	public Date getTimestamp() {
		return this.timestamp;
	}

	@Override
	public void setID(int id){
		this.id = id;
	}
	public void setOwnerId(int id) { this.owner_Id = id; }
	public void setName(String name) { this.name = name; }
	public void setDescription(String desc) { this.description = desc; }
	public void setInteractions(List<Interaction> interactions) { this.interactions = interactions; } 

	
	// Overriding equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false;  // Check if the classes are the same

        Group gr = (Group) o;

        if (id != gr.getID()) {
			return false;  // Compare the ids
		} else {
			return true;
		}
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
