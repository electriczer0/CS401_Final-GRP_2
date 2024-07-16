package lib.model;
import java.util.Date;


public class LoanFactory {
	public static Loan create(int loanID, int copyID, int userID, Date dateOut, Date dateDue, boolean isActive) {
		Loan loanOut = new Loan(); 
		loanOut.setID(loanID);
		loanOut.setCopyID(copyID);
		loanOut.setUserID(userID);
		loanOut.setDateOut(dateOut);
		loanOut.setDateDue(dateDue);
		loanOut.setActive(isActive);
		
		return loanOut;
		
	}

}
