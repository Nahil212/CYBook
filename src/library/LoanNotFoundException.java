package library;

public class LoanNotFoundException extends Exception{

    public LoanNotFoundException(){
        super("Error: this customer has never made this loan");
    }
}
