package lib.model;


import java.util.Date;

public class Loan implements Has_ID, Has_Copy {
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

    public int getCopyID() {
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
    
    @Override
    public String toString() {
    	StringBuilder output = new StringBuilder();
		output.append("Loan ID:\t")
			.append(this.Loan_Id)
			.append("\nCopy ID:\t")
			.append(this.Copy_Id)
			.append("\nUser ID:\t")
			.append(this.User_Id)
			.append("\nIs Active:\t")
			.append(this.Active)
			.append("\nDate Out:\t")
			.append(this.Date_Out)
			.append("\nDate Due:\t")
			.append(this.Date_Due)
			.append("\n");
		return output.toString();
    	
    }
    

	public static Loan create(int loanID, int copyID, int userID, Date dateOut, Date dateDue, boolean isActive) {
		/**
		 * Loan Factory for instantiating Loan objects
		 */
		Loan loanOut = new Loan(); 
		loanOut.setID(loanID);
		loanOut.setCopyID(copyID);
		loanOut.setUserID(userID);
		loanOut.setDateOut(dateOut);
		loanOut.setDateDue(dateDue);
		loanOut.setActive(isActive);
		
		return loanOut;
		
	}
	
	public static Loan copy(Loan loan) {
		/**
		 * Create a deep copy of the Loan instance
		 * @param loan a Loan object to be copied
		 * @return A Loan object which is a deep copy of the passed param. 
		 */
		return Loan.create(loan.getID(), loan.getCopyID(), loan.getUserID(), 
				new Date(loan.getDateOut().getTime()),
				new Date(loan.getDateDue().getTime()),
				loan.isActive()
				);
	}
	
	public Loan copy() {
		/**
		 * create a deep copy of this Loan instance
		 */
		return Loan.copy(this);
	}

}
