package lib.model;

import java.util.Date;
import java.util.List;

/**
 * A group of interactions. A user can create a group and post interactions to it; other users can join or leave
 * that group.
 */
public class Group implements Has_ID, Has_Copy {
	protected int id = -1; //-1 will be the null value for this field

	//The user that owns this group, typically the person who made it
	protected int owner_Id;

	protected String name;
	protected String description;
	protected Date timestamp;
	
	//defines the object type distinguishing subtypes
	protected String type = "Group"; 



	protected Group() {
		
	}
		
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Group ID:\t")
			.append(this.id)
			.append("\nOwner ID:\t")
			.append(this.owner_Id)
			.append("\nInteraction Count:\t")
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
	
	public int getOwnerId() {
		return this.owner_Id;
	}
	public String getName() {
		return this.name;
	}
	public String getDescription() {
		return this.description;
	}
	public Date getTimestamp() {
		return this.timestamp;
	}
	public String getType() { return this.type;}

	@Override
	public void setID(int id){
		this.id = id;
	}
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
    
    public static Group create(int groupID, int ownerID, String name, String description, Date timestamp) {
    	/**
    	 * Object Factory for Group class. 
    	 * @param groupID int primary key
    	 * @param ownerID int foreign key to users table representing group owner
    	 * @param name String name of the group
    	 * @param description String description of group
    	 * @param timestamp Date timestamp of group's creation 
    	 */
    	Group groupOut = new Group();
    	groupOut.setID(groupID);
    	groupOut.setOwnerId(ownerID);
    	groupOut.setName(name);
    	groupOut.setDescription(description);
    	groupOut.setTimestamp(timestamp);
    	return groupOut;
    	
    	
    }
    public static Group copy(Group group) {
    	/**
    	 * Create a deep copy of group
    	 */
    	return Group.create(group.getID(), group.getOwnerId(), group.getName(), group.getDescription(), group.getTimestamp());
    }
    
    public Group copy() {
    	/**
    	 * Return a deep copy of this Group instance
    	 */
    	return Group.copy(this);
    }

}
