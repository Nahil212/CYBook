package library;

public class BookNotInDataBaseException extends Exception {

	private static final long serialVersionUID = 5889523759541478331L;

	public BookNotInDataBaseException() {
		super("Error: this book does not exist in the BNF database");
	}
}
