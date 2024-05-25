package library;

/**
 * An exception thrown when no book matches the specified filters in the BNF (Bibliothèque nationale de France) database.
 */
public class EmptyResearchException extends Exception {

    /**
     * A unique identifier for serializing and deserializing the exception object.
     */
    private static final long serialVersionUID = 3720686815307834393L;

    /**
     * Constructs a new EmptyResearchException with the default error message.
     */
    public EmptyResearchException() {
        super("No book correspond to those filters in the BNF database");
    }
}
