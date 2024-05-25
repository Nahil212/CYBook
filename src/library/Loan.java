package library;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a loan of a book with its identifier, loan date, planned return date,
 * effective return date, late status, return status, and customer ID.
 */
public class Loan {
    /**
     * Static AtomicInteger to keep track of the next loan ID.
     */
    private static AtomicInteger nextId = new AtomicInteger(2);

    /**
     * The ID of the loan.
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
     * The planned date for returning the book.
     */
    private Date plannedDateBack;

    /**
     * The actual date when the book was returned.
     */
    private Date effectiveDateBack;

    /**
     * Indicates if the book was returned late.
     */
    private boolean late;

    /**
     * Indicates if the book has been returned.
     */
    private boolean returned;

    /**
     * The ID of the customer who borrowed the book.
     */
    private int customerId;

    /**
     * Constructs a new Loan object with the specified book identifier.
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
     * @param id                the ID of the loan
     * @param identifier        the identifier of the book being loaned
     * @param dateLoan          the date when the loan was made
     * @param plannedDateBack   the planned date for returning the book
     * @param effectiveDateBack the actual date when the book was returned
     * @param returned          indicates if the book has been returned
     * @param late              indicates if the book was returned late
     * @param customerId        the ID of the customer who borrowed the book
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
     * Calculates the scheduled return date, which is 14 days from the loan date.
     *
     * @return the scheduled return date
     */
    private Date calculateScheduledReturnDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateLoan);
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        return calendar.getTime();
    }

    /**
     * Checks if the book was returned late.
     *
     * @return true if the book was returned late, false otherwise
     */
    protected boolean calculateLate() {
        return this.effectiveDateBack != null && this.effectiveDateBack.after(this.plannedDateBack);
    }

    /**
     * Returns the ID of the loan.
     *
     * @return the ID of the loan
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the loan.
     *
     * @param id the new ID of the loan
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
     * Returns the planned date for returning the book.
     *
     * @return the planned date for returning the book
     */
    public Date getPlannedDateBack() {
        return plannedDateBack;
    }

    /**
     * Sets the planned date for returning the book.
     *
     * @param plannedDateBack the new planned date for returning the book
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
     * Returns the late status of the loan.
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
     * Sets the late status of the loan.
     *
     * @param late the new late status of the loan
     */
    public void setLate(boolean late) {
        this.late = late;
    }

    /**
     * Returns the return status of the loan.
     *
     * @return true if the book has been returned, false otherwise
     */
    public boolean getReturned() {
        return returned;
    }

    /**
     * Sets the return status of the loan.
     *
     * @param returned the new return status of the loan
     */
    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    /**
     * Returns the ID of the customer who borrowed the book.
     *
     * @return the ID of the customer who borrowed the book
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets the ID of the customer who borrowed the book.
     *
     * @param customerId the new ID of the customer who borrowed the book
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
