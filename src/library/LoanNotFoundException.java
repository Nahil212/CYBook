package library;

public class LoanNotFoundException extends Exception{

	private static final long serialVersionUID = 1787548330231118500L;

	public LoanNotFoundException(){
        super("Error: this customer has never made this loan");
    }
}
