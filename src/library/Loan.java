package library;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Loan {
    private static AtomicInteger nextId = new AtomicInteger(2);

    private int id;
    private String identifier;
    private Date dateLoan;
    private Date plannedDateBack;
    private Date effectiveDateBack;
    private boolean late;
    private boolean returned;

    private int customerId;
    public Loan(String identifier) {
        this.identifier = identifier;
        this.dateLoan = new Date();
        this.plannedDateBack = calculateScheduledReturnDate();
        this.effectiveDateBack = null;
        this.late = false;
        this.returned = false;
        this.id = nextId.getAndIncrement();
    }

    public Loan(int id, String identifier, Date dateLoan, Date plannedDateBack, Date effectiveDateBack, boolean returned, boolean late) {
        this.id = id;
        this.identifier = identifier;
        this.dateLoan = dateLoan;
        this.plannedDateBack = plannedDateBack;
        this.effectiveDateBack = effectiveDateBack;
        this.late = late;
        this.returned = returned;
    }

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



    public static void initializeNextId(int maxIdLoan) {
        nextId.set(maxIdLoan + 1);
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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
    	if(!(this.getReturned()) && this.getPlannedDateBack().before(new Date()) ){
    		this.setLate(true);
    	}
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

    public int getCustomerId(){
        return customerId;
    }

    public void setCustomerId(int customerId){
        this.customerId= customerId;
    }

  
}
