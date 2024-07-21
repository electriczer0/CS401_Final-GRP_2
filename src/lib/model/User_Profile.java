package lib.model;

import java.util.List;

/**
 * A user social profile.
 */
public class User_Profile implements Has_ID, Has_Copy<User_Profile> {
    private int Profile_Id;
    //The user this profile belongs to
    private int User_Id;
    private String username;
    private String readingHabits;
    private String literaryPreferences;

    public User_Profile() {
    }

    @Override
    public int getID() { return Profile_Id;}

    @Override
    public void setID(int Profile_Id) {this.Profile_Id = Profile_Id;}

    public int getUserId() {return User_Id;}
    public void setUserId(int user_Id) { this.User_Id = user_Id;}


    public String getUsername() { return username;}
    public void setUsername(String username) { this.username = username;}


    public String getReadingHabits() {return readingHabits;}

    public void setReadingHabits(String readingHabits) {this.readingHabits = readingHabits;}

    public String getLiteraryPreferences() {return literaryPreferences;}

    public void setLiteraryPreferences(String literaryPreferences) {this.literaryPreferences = literaryPreferences; }

//TODO implement favorite book IDs
//TODO implement recent book IDs
 // Overriding equals method

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false;  // Check if the classes are the same

        User_Profile profile = (User_Profile) o;

        if (Profile_Id != profile.getID()) return false;  // Compare the ids
        if (username != profile.getUsername()) return false;
        if (!readingHabits.equals(getReadingHabits())) return false;
        if (!literaryPreferences.equals(profile.getLiteraryPreferences())) return false;

        return true;
    }

    // Overriding hashCode method
    @Override
    public int hashCode() {
        int result = Profile_Id;
        result = 31 * result + username.hashCode();

        return result;
    }

    public static User_Profile create(int id, int user_Id, String username, String litPrefs, String readingHabits) {

		User_Profile profile = User_Profile.create();
		profile.setID(id);
        profile.setUserId(user_Id);
		profile.setUsername(username);
		profile.setLiteraryPreferences(litPrefs);
		profile.setReadingHabits(readingHabits);

		return profile;
	}
    
    public static User_Profile create() {
    	return new User_Profile();
    }

    public static User_Profile copy(User_Profile profile) {

    	User_Profile newUser = User_Profile.create(profile.getID(), profile.getUserId(), profile.getUsername(),
    			profile.getLiteraryPreferences(), profile.getReadingHabits());
        return newUser;
    }

    public User_Profile copy() {return User_Profile.copy(this); }
}

