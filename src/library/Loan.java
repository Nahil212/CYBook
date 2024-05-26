package library;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a loan of a book, with details about the loan and return dates, and the status of the loan.
 */
public class Loan {
    /**
     * The next available ID for a loan.
     */
    protected static AtomicInteger nextId = new AtomicInteger(2);

    /**
     * The unique ID of the loan.
     */
    private int id;

    /**
     * The identifier of the book being loaned.
     */
    private String identifier;

    /**
     * The date when the loan was made.
     */
    private Date dateLoan;

    /**
     * The planned date for returning the loaned book.
     */
    private Date plannedDateBack;

    /**
     * The actual date when the book was returned.
     */
    private Date effectiveDateBack;

    /**
     * Indicates if the loan is late.
     */
    private boolean late;

    /**
     * Indicates if the book has been returned.
     */
    private boolean returned;

    /**
     * The ID of the customer who made the loan.
     */
    private int customerId;

    /**
     * Constructs a new Loan object with the specified identifier.
     *
     * @param identifier the identifier of the book being loaned
     */
    public Loan(String identifier) {
        this.identifier = identifier;
        this.dateLoan = new Date();
        this.plannedDateBack = calculateScheduledReturnDate();
        this.effectiveDateBack = null;
        this.late = false;
        this.returned = false;
        this.id = nextId.getAndIncrement();
    }

    /**
     * Constructs a new Loan object with the specified details.
     *
     * @param id                the unique ID of the loan
     * @param identifier        the identifier of the book being loaned
     * @param dateLoan          the date when the loan was made
     * @param plannedDateBack   the planned date for returning the loaned book
     * @param effectiveDateBack the actual date when the book was returned
     * @param returned          indicates if the book has been returned
     * @param late              indicates if the loan is late
     */
    public Loan(int id, String identifier, Date dateLoan, Date plannedDateBack, Date effectiveDateBack, boolean returned, boolean late) {
        this.id = id;
        this.identifier = identifier;
        this.dateLoan = dateLoan;
        this.plannedDateBack = plannedDateBack;
        this.effectiveDateBack = effectiveDateBack;
        this.late = late;
        this.returned = returned;
    }

    /**
     * Constructs a new Loan object with the specified details including the customer ID.
     *
     * @param id                the unique ID of the loan
     * @param identifier        the identifier of the book being loaned
     * @param dateLoan          the date when the loan was made
     * @param plannedDateBack   the planned date for returning the loaned book
     * @param effectiveDateBack the actual date when the book was returned
     * @param returned          indicates if the book has been returned
     * @param late              indicates if the loan is late
     * @param customerId        the ID of the customer who made the loan
     */
    public Loan(int id, String identifier, Date dateLoan, Date plannedDateBack, Date effectiveDateBack, boolean returned, boolean late, int customerId) {
        this.id = id;
        this.identifier = identifier;
        this.dateLoan = dateLoan;
        this.plannedDateBack = plannedDateBack;
        this.effectiveDateBack = effectiveDateBack;
        this.late = late;
        this.returned = returned;
        this.customerId = customerId;
    }

    /**
     * Initializes the next ID to be used for loans.
     *
     * @param maxIdLoan the maximum ID currently in use
     */
    public static void initializeNextId(int maxIdLoan) {
        nextId.set(maxIdLoan + 1);
    }

    /**
     * Calculates the scheduled return date, which is 14 days after the loan date.
     *
     * @return the scheduled return date
     */
    protected Date calculateScheduledReturnDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateLoan);
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        return calendar.getTime();
    }

    /**
     * Calculates if the loan is late based on the planned return date and the actual return date.
     *
     * @return true if the loan is late, false otherwise
     */
    protected boolean calculateLate() {
        return this.effectiveDateBack != null && this.effectiveDateBack.after(this.plannedDateBack);
    }

    /**
     * Returns the unique ID of the loan.
     *
     * @return the unique ID of the loan
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of the loan.
     *
     * @param id the new unique ID of the loan
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the identifier of the book being loaned.
     *
     * @return the identifier of the book being loaned
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier of the book being loaned.
     *
     * @param identifier the new identifier of the book being loaned
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the date when the loan was made.
     *
     * @return the date when the loan was made
     */
    public Date getDateLoan() {
        return dateLoan;
    }

    /**
     * Sets the date when the loan was made.
     *
     * @param dateLoan the new date when the loan was made
     */
    public void setDateLoan(Date dateLoan) {
        this.dateLoan = dateLoan;
    }

    /**
     * Returns the planned date for returning the loaned book.
     *
     * @return the planned date for returning the loaned book
     */
    public Date getPlannedDateBack() {
        return plannedDateBack;
    }

    /**
     * Sets the planned date for returning the loaned book.
     *
     * @param plannedDateBack the new planned date for returning the loaned book
     */
    public void setPlannedDateBack(Date plannedDateBack) {
        this.plannedDateBack = plannedDateBack;
    }

    /**
     * Returns the actual date when the book was returned.
     *
     * @return the actual date when the book was returned
     */
    public Date getEffectiveDateBack() {
        return effectiveDateBack;
    }

    /**
     * Sets the actual date when the book was returned.
     *
     * @param effectiveDateBack the new actual date when the book was returned
     */
    public void setEffectiveDateBack(Date effectiveDateBack) {
        this.effectiveDateBack = effectiveDateBack;
    }

    /**
     * Returns whether the loan is late.
     *
     * @return true if the loan is late, false otherwise
     */
    public boolean getLate() {
        if (!(this.getReturned()) && this.getPlannedDateBack().before(new Date())) {
            this.setLate(true);
        }
        return late;
    }

    /**
     * Sets whether the loan is late.
     *
     * @param late true if the loan is late, false otherwise
     */
    public void setLate(boolean late) {
        this.late = late;
    }

    /**
     * Returns whether the book has been returned.
     *
     * @return true if the book has been returned, false otherwise
     */
    public boolean getReturned() {
        return returned;
    }

    /**
     * Sets whether the book has been returned.
     *
     * @param returned true if the book has been returned, false otherwise
     */
    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    /**
     * Returns the ID of the customer who made the loan.
     *
     * @return the ID of the customer who made the loan
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets the ID of the customer who made the loan.
     *
     * @param customerId the new ID of the customer who made the loan
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
