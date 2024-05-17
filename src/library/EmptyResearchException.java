package library;

public class EmptyResearchException extends Exception {

	private static final long serialVersionUID = 3720686815307834393L;
	public EmptyResearchException() {
		super("No book correspond to those filters in the BNF database");
	}

}
