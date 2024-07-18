package lib.model;

import java.util.Date;
import java.util.List;

/**
 * A meeting. Similar to group, has interactions, and users can join/leave meetings and post new interactions,
 * but a meeting both has an owning Group and has a specific timestamp and location.
 */
public class Meeting extends Group implements Has_ID, Has_Interactions {
	//The associated group for this meeting.
	private Integer group_Id;

	//When the meeting is supposed to be, and where
	private Date meetingTimestamp;
	private String meetingLocation;



	public Meeting() {
		super();
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

	public Integer getGroupId() {
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

        Meeting gr = (Meeting) o;

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
        result = 31 * result + meetingTimestamp.hashCode();
        result = 31 * result + meetingLocation.hashCode();
        return result;
    }

}
