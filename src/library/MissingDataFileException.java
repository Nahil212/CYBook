package library;

public class MissingDataFileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7054098675674921293L;
	
	public MissingDataFileException() {
		super("The file containing loans and users data is not found");
	}
}
