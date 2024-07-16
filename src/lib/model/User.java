package lib.model;

public class User implements Has_ID {
    private int UserID;
    private String Name_First;
    private String Name_Last;
    private String Type;

    public User() {
    }

    public User(Integer UserID, String NameFirst, String NameLast, String Type) {
        this.UserID = UserID;
        this.Name_First = NameFirst;
        this.Name_Last = NameLast;
        this.Type = Type;
    }

    @Override
    public int getID() {
        return UserID;
    }

    @Override
    public void setID(int User_Id) {
        this.UserID = User_Id;
    }

    public String getFirstName() {
        return Name_First;
    }

    public void setFirstName(String Name_First) {
        this.Name_First = Name_First;
    }

    public String getLastName() {
        return Name_Last;
    }

    public void setLastName(String Name_Last) {
        this.Name_Last = Name_Last;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }
    
 // Overriding equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false;  // Check if the classes are the same

        User user = (User) o;

        if (UserID != user.getID()) return false;  // Compare the ids
        if (!Name_First.equals(user.getFirstName())) return false;
        if (!Name_Last.equals(user.getLastName())) return false;
        
        
        return Type.equals(user.getType());  // Compare the authors
    }

    // Overriding hashCode method
    @Override
    public int hashCode() {
        int result = UserID;
        result = 31 * result + Name_First.hashCode();
        result = 31 * result + Name_Last.hashCode();
        result = 31 * result + Type.hashCode();
        
        return result;
    }
}