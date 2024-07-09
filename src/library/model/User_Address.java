package library.model;

public class User_Address implements Has_ID {
    private Integer Address_Id;
    private Integer User_Id;
    private String Street1;
    private String Street2;
    private String City;
    private String Zip;
    private String State;

    public User_Address() {
    }

    @Override
    public int getID() {
        return Address_Id;
    }

    @Override
    public void setID(int Address_Id) {
        this.Address_Id = Address_Id;
    }

    public Integer getUserID() {
        return User_Id;
    }

    public void setUserID(Integer User_Id) {
        this.User_Id = User_Id;
    }

    public String getStreet1() {
        return Street1;
    }

    public void setStreet1(String Street1) {
        this.Street1 = Street1;
    }

    public String getStreet2() {
        return Street2;
    }

    public void setStreet2(String Street2) {
        this.Street2 = Street2;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String City) {
        this.City = City;
    }

    public String getZip() {
        return Zip;
    }

    public void setZip(String Zip) {
        this.Zip = Zip;
    }

    public String getState() {
        return State;
    }

    public void setState(String State) {
        this.State = State;
    }
}

