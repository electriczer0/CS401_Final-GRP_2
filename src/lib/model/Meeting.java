package lib.model;

import java.util.Date;
import java.util.List;

/**
 * A meeting. Similar to group, has interactions, and users can join/leave meetings and post new interactions,
 * but a meeting both has an owning Group and has a specific timestamp and location.
 */
public class Meeting extends Group implements Has_ID, Has_Copy {
	
	/**
	 * Foreign key to the Group class which is the group associated with this meeting
	 * This is NOT a primary key. The primary key is id inherited from the Group class
	 */
	private int group_Id;

	//When the meeting is supposed to be, and where
	private Date meetingTimestamp;
	private String meetingLocation;


	//Type differentiates between class subtypes
	protected String type; 

	protected Meeting() {
		super();
	}
		
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("ID:\t")
			.append(this.id)
			.append("\nOwner ID:\t")
			.append(this.owner_Id)
			.append("\nName:\t")
			.append(this.name)
			.append("\nDescription:\t")
			.append(this.description)
			.append("\nRefGroupID:\t")
			.append(this.group_Id)
			.append("\nLocation:\t")
			.append(this.meetingLocation)
			.append("\nMeeting Time:\t")
			.append(this.meetingTimestamp)
			.append("\nType:\t")
			.append(this.getType())
			.append("\nTimestamp:\t")
			.append(this.getTimestamp())
			.append("\n");
		return output.toString();
		
	}

	public int getGroupId() {
		return this.group_Id;
	}
	public Date getMeetingTime() {
		return this.meetingTimestamp;
	}
	public String getMeetingLocation() {
		return this.meetingLocation;
	}

	public void setGroupId(int id) { this.group_Id = id; }
	public void setMeetingTimestamp(Date meetingTime) { this.meetingTimestamp = meetingTime; }
	public void setMeetingLocation(String location) { this.meetingLocation = location; }

	
	// Overriding equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false;  // Check if the classes are the same

        Meeting meeting = (Meeting) o;
        
        if(this.id != meeting.getID()) return false;
        if(this.owner_Id != meeting.getOwnerId()) return false; 
        if(this.description != null && !this.description.equals(meeting.getDescription())) return false;
        if(this.name != null && !this.name.equals(meeting.getName()))return false; 
        if(this.timestamp != null && !this.timestamp.equals(meeting.getTimestamp())) return false;
        if(this.meetingLocation != null && !this.meetingLocation.equals(meeting.getMeetingLocation())) return false;
        if(this.meetingTimestamp != null && !this.meetingTimestamp.equals(meeting.getMeetingTime())) return false;
        if(this.group_Id != meeting.getGroupId()) return false;
        if(this.type != null && !this.type.equals(meeting.getType())) return false; 
        return true; 

       

    }

    // Overriding hashCode method
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + meetingTimestamp.hashCode();
        result = 31 * result + meetingLocation.hashCode();
        return result;
    }
    
    /**
     * Object Factory for Meeting Record
     * @param recordID primary key can be -1 for null 
     * @param ownerID foreign key to Users table representing ownership of the Meeting. not null
     * @param groupID foreign key to Group table representing group ownership of the meeting -1 for null
     * @param name String meeting name
     * @param description String meeting description
     * @param timestamp String meeting creation timestamp
     * @param meetingTimestamp Date the datetime at which the meeting is to take place 
     * @return
     */
    public static Meeting create(int recordID, int ownerID, int groupID, String name,
    		String description, String location, Date meetingTimestamp, Date timestamp) {
    
    	Meeting meetingOut = new Meeting();
    	meetingOut.setID(recordID);
    	meetingOut.setOwnerId(ownerID);
    	meetingOut.setGroupId(groupID);
    	meetingOut.setName(name);
    	meetingOut.setDescription(description);
    	meetingOut.setMeetingLocation(location);
    	meetingOut.setTimestamp(timestamp);
    	meetingOut.setMeetingTimestamp(meetingTimestamp);
    	meetingOut.setType("Meeting");
    	return meetingOut;
    }
    
    /**
	 * Object Factory for instantiating blank Meeting Objects. 
	 */
    public static Meeting create() {
    	Meeting meeting = new Meeting();
    	meeting.setType("Meeting");
    	return meeting; 
    }
    
    /**
	 * Create a deep copy of meeting
	 */
    public static Meeting copy(Meeting meeting) {
    	
    	return Meeting.create(meeting.getID(), meeting.getOwnerId(), meeting.getGroupId(),
    			meeting.getName(), meeting.getDescription(), meeting.getMeetingLocation(), 
    			meeting.getMeetingTime(), meeting.getTimestamp());
    }
    
    /**
	 * Return a deep copy of this Meeting instance
	 */
    public Meeting copy() {
    	
    	return Meeting.copy(this);
    }

}
