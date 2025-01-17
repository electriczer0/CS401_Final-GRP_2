package lib.model;

import java.util.Date;



/**
 * A class that describes social media interactions. Users can post, comment on each other's posts, and like and
 * share each other's posts (whether they are root comments or child comments).
 */
public class Interaction implements Has_ID, Has_Copy<Interaction> {
	
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



	protected Interaction() {

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

        if(id != inter.getID()) return false;
        if(group_Id != inter.getGroupId()) return false;
        if(target_Id != inter.getTargetId()) return false;
        if(user_Id != inter.getUserId()) return false; 
        if(!content.equals(inter.getContent())) return false; 
        if(!type.equals(inter.getType())) return false;
        if(!timestamp.equals(inter.getTimestamp())) return false;

        return true;
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
    
    public static Interaction create(int interactionID, int userID, int groupID, int targetID, Interaction_Type type, String content, Date timestamp) {
    	/**
    	 * Object factory for Interaction class
    	 * @param interactionID primary key for class. Can be -1 for null
    	 * @param userID foreign key to users table. user who created interaction
    	 * @param groupID foreign key to groups table. should not be null, can use global group 
    	 * @param targetID foreign key to Interaction table representing nested interactions 
    	 * @param type Interaction_Type enum representing the type of interaction
    	 * @param content the content of the social message
    	 * @param timestamp Date type object representing the time of creation. 
    	 * @return Interaction object. 
    	 */
    	
    	Interaction intOut = new Interaction();
    	intOut.setID(interactionID);
    	intOut.setUserId(userID);
    	intOut.setGroupId(groupID);
    	intOut.setTargetId(targetID);
    	intOut.setType(type);
    	intOut.setContent(content);
    	intOut.setTimestamp(timestamp);
    	return intOut;
    	
    }
    
    public static Interaction create() {
    	/**
    	 * Object Factory for Interaction class. returning empty Interaction instance
    	 */
    	return new Interaction();
    }
    
    public static Interaction copy(Interaction interaction) {
    	/**
    	 * Create a deep copy of interaction
    	 */
    	return Interaction.create(interaction.getID(), interaction.getUserId(), interaction.getGroupId(), interaction.getTargetId(),
    			interaction.getType(), interaction.getContent(), interaction.getTimestamp() );
    }
    
    public Interaction copy() {
    	/**
    	 * Return a deep copy of this Interaction instance
    	 */
    	return Interaction.copy(this);
    }

}
