package library.model;


import java.util.Date;

public class Loan implements Has_ID {
	private Integer Loan_Id;
    private Integer Copy_Id;
    private Integer User_Id;
    private Date Date_Out;
    private Date Date_Due;
    private Boolean Active;

    public Loan() {
    }

    
    @Override
    public int getID() {
        return Loan_Id;
    }

    @Override
    public void setID(int Loan_Id) {
        this.Loan_Id = Loan_Id;
    }

    public Integer getCopyID() {
        return Copy_Id;
    }

    public void setCopyID(int Copy_Id) {
        this.Copy_Id = Copy_Id;
    }

    public int getUserID() {
        return User_Id;
    }

    public void setUserID(int User_Id) {
        this.User_Id = User_Id;
    }

    public Date getDateOut() {
        return Date_Out;
    }

    public void setDateOut(Date Date_Out) {
        this.Date_Out = Date_Out;
    }

    public Date getDateDue() {
        return Date_Due;
    }

    public void setDateDue(Date Date_Due) {
        this.Date_Due = Date_Due;
    }

    public Boolean isActive() {
        return Active;
    }

    public void setActive(Boolean Active) {
        this.Active = Active;
    }

}
