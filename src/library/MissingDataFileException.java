package library;

/**
 * Custom exception to be thrown when the data file containing loans and user data is missing.
 */
public class MissingDataFileException extends Exception {

	/**
	 * Unique identifier for serialization.
	 */
	private static final long serialVersionUID = 7054098675674921293L;

	/**
	 * Constructs a new MissingDataFileException with a default error message.
	 */
	public MissingDataFileException() {
		super("The file containing loans and users data is not found");
	}
}
