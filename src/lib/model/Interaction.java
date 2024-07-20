package lib.model;

import java.util.Date;



/**
 * A class that describes social media interactions. Users can post, comment on each other's posts, and like and
 * share each other's posts (whether they are root comments or child comments).
 */
public class Interaction implements Has_ID {
	
	public enum Interaction_Type {
		ORIGINAL_CONTENT,
		COMMENT_ON_CONTENT,
		LIKE_CONTENT,
		SHARE_ON_CONTENT
	}
	
	private int id = -1; //-1 will be the null value for this field

	//The user that owns this interaction, typically the person who made it
	private int user_Id;

	//The target of this interaction. This will be null if it is original content.
	private int target_Id;
	
	//Group owner of interaction. All interactions will have a group owner
	//Defaults to "global" group ID
	private int group_Id;

	private Interaction_Type type;

	//The content of a post. Will be null if the Interaction is a Like or a Share.
	private String content;

	private Date timestamp;



	public Interaction() {

	}
		
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Interaction ID:\t")
			.append(this.id)
			.append("\nUser ID:\t")
			.append(this.user_Id)
			.append("\nTarget ID:\t")
			.append(this.target_Id)
			.append("\nType:\t")
			.append(this.type)
			.append("\nContent:\t")
			.append(this.content)
			.append("\n");
		return output.toString();
		
	}
	
	public int getID() {
		return this.id;
	}
	
	public int getUserId() {
		return this.user_Id;
	}
	public int getTargetId() {
		return this.target_Id;
	}
	public int getGroupId() {
		return this.group_Id;
	}
	public Interaction_Type getType() {
		return this.type;
	}
	public String getContent() {
		return this.content;
	}
	public Date getTimestamp() {
		return this.timestamp;
	}

	//These setters shouldn't be used in regular logic - an Interaction should be treated as immutable.
	@Override
	public void setID(int id){
		this.id = id;
	}
	public void setUserId(int id){
		this.user_Id = id;
	}
	public void setTargetId(int id){
		this.target_Id = id;
	}
	public void setType(Interaction_Type type){
		this.type = type;
	}
	public void setContent(String content){
		this.content = content;
	}
	public void setTimestamp(Date timestamp){
		this.timestamp = timestamp;
	}
	public void setGroupId(int id) {
		this.group_Id = id; 
	}

	
	// Overriding equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false;  // Check if the classes are the same

        Interaction inter = (Interaction) o;

        if (id != inter.getID()) {
			return false;  // Compare the ids
		} else {
			return true;
		}
		//We should enforce id uniqueness in the DB.
		/*
        if (!target_Id.equals(inter.getTargetId())) return false;  // Compare the ISBNs
        if (!type.equals(inter.getType())) return false;  // Compare the titles
        return Title.equals(inter.getTitle());  // Compare the authors
		 */

    }

    // Overriding hashCode method
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + user_Id;
        result = 31 * result + target_Id;
        result = 31 * result + group_Id; 
        result = 31 * result + content.hashCode();
        return result;
    }

}
