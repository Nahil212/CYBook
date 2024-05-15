package library;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Loan {
    private static final AtomicInteger nextId = new AtomicInteger(2);
    private int id;
    private long isbn;
    private Date dateLoan;
    private Date plannedDateBack;
    private Date effectiveDateBack;
    private boolean late;
    private boolean returned;

    public Loan(long isbn) {
        this.isbn = isbn;
        this.dateLoan = new Date();
        this.plannedDateBack = calculateScheduledReturnDate();
        this.effectiveDateBack = null;
        this.late = false;
        this.returned = false;
        this.id = nextId.getAndIncrement();
    }

    private Date calculateScheduledReturnDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateLoan);
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        return calendar.getTime();
    }

    protected boolean calculateLate() {
        return this.effectiveDateBack != null && this.effectiveDateBack.after(this.plannedDateBack);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    public Date getDateLoan() {
        return dateLoan;
    }

    public void setDateLoan(Date dateLoan) {
        this.dateLoan = dateLoan;
    }

    public Date getPlannedDateBack() {
        return plannedDateBack;
    }

    public void setPlannedDateBack(Date plannedDateBack) {
        this.plannedDateBack = plannedDateBack;
    }

    public Date getEffectiveDateBack() {
        return effectiveDateBack;
    }

    public void setEffectiveDateBack(Date effectiveDateBack) {
        this.effectiveDateBack = effectiveDateBack;
    }

    public boolean getLate() {
        return late;
    }

    public void setLate(boolean late) {
        this.late = late;
    }

    public boolean getReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
