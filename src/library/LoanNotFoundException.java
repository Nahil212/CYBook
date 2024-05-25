package library;

/**
 * Exception thrown when a specific loan is not found for a customer.
 */
public class LoanNotFoundException extends Exception {

    /**
     * Serialization ID for the exception class.
     */
    private static final long serialVersionUID = 1787548330231118500L;

    /**
     * Constructs a new LoanNotFoundException with a default error message.
     */
    public LoanNotFoundException() {
        super("Error: this customer has never made this loan");
    }
}
