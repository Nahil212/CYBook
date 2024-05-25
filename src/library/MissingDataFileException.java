package library;

/**
 * Exception thrown when the data file containing loans and users information is not found.
 */
public class MissingDataFileException extends Exception {

	/**
	 * Serialization ID for the exception class.
	 */
	private static final long serialVersionUID = 7054098675674921293L;

	/**
	 * Constructs a new MissingDataFileException with a default error message.
	 */
	public MissingDataFileException() {
		super("The file containing loans and users data is not found");
	}
}
