package library;

/**
 * Custom exception to be thrown when a loan is not found.
 */
public class LoanNotFoundException extends Exception {

    /**
     * Unique identifier for serialization.
     */
    private static final long serialVersionUID = 1787548330231118500L;

    /**
     * Constructs a new LoanNotFoundException with a default error message.
     */
    public LoanNotFoundException() {
        super("Error: this customer has never made this loan");
    }
}
