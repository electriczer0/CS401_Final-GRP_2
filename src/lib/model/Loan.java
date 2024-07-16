package lib.model;


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
    
 // Overriding equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the objects are the same
        if (o == null || getClass() != o.getClass()) return false;  // Check if the classes are the same

        Loan loan = (Loan) o;

        if (Loan_Id != loan.getID()) return false;  // Compare the ids
        if (Copy_Id != loan.getCopyID()) return false;
        if (User_Id != loan.getUserID()) return false;
        if (!Active.equals(loan.isActive())) return false;
        if (!Date_Due.equals(loan.getDateDue())) return false;
        
        return Date_Out.equals(loan.getDateOut());  // Compare the authors
    }

    // Overriding hashCode method
    @Override
    public int hashCode() {
        int result = Loan_Id;
        result = 31 * result + Copy_Id;
        result = 31 * result + User_Id;
        result = 31 * result + Active.hashCode();
        result = 31 * result + Date_Due.hashCode();
        result = 31 * result + Date_Out.hashCode();
      
        return result;
    }

}
