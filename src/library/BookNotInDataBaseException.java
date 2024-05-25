package library;

/**
 * An exception thrown when a book is not found in the BNF (Bibliothèque nationale de France) database.
 */
public class BookNotInDataBaseException extends Exception {

    /**
     * A unique identifier for serializing and deserializing the exception object.
     */
    private static final long serialVersionUID = 5889523759541478331L;

    /**
     * Constructs a new BookNotInDataBaseException with the default error message.
     */
    public BookNotInDataBaseException() {
        super("This book does not exist in the BNF database");
    }
}