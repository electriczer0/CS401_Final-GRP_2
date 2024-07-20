package lib.model;

import java.util.List;

/**
 * A user social profile.
 */
public class User_Profile implements Has_ID {
    private Integer Profile_Id;
    //The user this profile belongs to
    private Integer User_Id;
    private String username;
    private List<String> favoriteBookIds;
    private String readingHabits;
    private String literaryPreferences;
    private List<String> recentBookIds;

    public User_Profile() {
    }

    @Override
    public int getID() {
        return Profile_Id;
    }

    @Override
    public void setID(int Profile_Id) {
        this.Profile_Id = Profile_Id;
    }

    public int getUserId() {
        return User_Id;
    }
    public void setUserId(int user_Id) {
        this.User_Id = user_Id;
    }


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getFavoriteBookIds() {
        return favoriteBookIds;
    }
    public void setFavoriteBookIds(List<String> favoriteBookIds) {
        this.favoriteBookIds = favoriteBookIds;
    }

    public String getReadingHabits() {
        return readingHabits;
    }

    public void setReadingHabits(String readingHabits) {
        this.readingHabits = readingHabits;
    }

    public String getLiteraryPreferences() {
        return literaryPreferences;
    }

    public void setLiteraryPreferences(String literaryPreferences) {
        this.literaryPreferences = literaryPreferences;
    }

    public List<String> getRecentBookIds() {
        return recentBookIds;
    }
    public void setRecentBookIds(List<String> recentBookIds) {
        this.recentBookIds = recentBookIds;
    }

 // Overriding equals method

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false;  // Check if the classes are the same

        User_Profile profile = (User_Profile) o;

        if (Profile_Id != profile.getID()) return false;  // Compare the ids
        if (username != profile.getUsername()) return false;
        if (readingHabits != profile.getReadingHabits()) return false;
        if (literaryPreferences != profile.getLiteraryPreferences()) return false;
        if (!favoriteBookIds.equals(profile.getFavoriteBookIds())) return false;
        if (!recentBookIds.equals(profile.getRecentBookIds())) return false;

        return true;
    }

    // Overriding hashCode method
    @Override
    public int hashCode() {
        int result = Profile_Id;
        result = 31 * result + username.hashCode();

        return result;
    }

    public static User_Profile create(int id, int user_Id, String username) {

		User_Profile profile = new User_Profile();
		profile.setID(id);
        profile.setUserId(user_Id);
		profile.setUsername(username);

		return profile;
	}

    public static User_Profile copy(User_Profile profile) {

    	User_Profile newUser = User_Profile.create(profile.getID(), profile.getUserId(), profile.getUsername());
        newUser.setFavoriteBookIds(profile.getFavoriteBookIds());
        newUser.setReadingHabits(profile.getReadingHabits());
        newUser.setLiteraryPreferences(profile.getLiteraryPreferences());
        newUser.setRecentBookIds(profile.getRecentBookIds());
        return newUser;
    }

    public User_Profile copy() {
    	return User_Profile.copy(this);
    }
}

