package library.model;

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
}