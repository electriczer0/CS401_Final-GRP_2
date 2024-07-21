package lib.model;

import java.util.Date;
import java.util.List;

/**
 * A group of interactions. A user can create a group and post interactions to it; other users can join or leave
 * that group.
 */
public class Group extends Grouping implements Has_Copy<Group> {

	protected Group() {	}
    
	/**
	 * Object Factory for Group class. 
	 * @param groupID int primary key
	 * @param ownerID int foreign key to users table representing group owner
	 * @param name String name of the group
	 * @param description String description of group
	 * @param timestamp Date timestamp of group's creation 
	 */
    public static Group create(int groupID, int ownerID, String name, String description, Date timestamp) {
    	
    	Group groupOut = Group.create();
    	groupOut.setID(groupID);
    	groupOut.setOwnerId(ownerID);
    	groupOut.setName(name);
    	groupOut.setDescription(description);
    	groupOut.setTimestamp(timestamp);
    	groupOut.setType("Group");
    	return groupOut;
    }
    
    /**
	 * Object Factory for Group class returning empty group objects
	 */
    public static Group create() {
    	Group group = new Group();
    	group.setType("Group");
    	group.setID(-1);
    	return group;
    }
    
    /**
	 * Create a deep copy of group
	 */
    public static Group copy(Group group) {
    	return Group.create(group.getID(), group.getOwnerId(), group.getName(), group.getDescription(), group.getTimestamp());
    }
    
    /**
	 * Return a deep copy of this Group instance
	 */
    public Group copy() {return Group.copy(this);}

}
