package library;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Customer {
    private static AtomicInteger nextId = new AtomicInteger(2);

    private int idNumber;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private ArrayList<Loan> loans;

    public Customer(String firstName, String lastName, Date birthDate) {
        this.idNumber = nextId.getAndIncrement();
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.loans = new ArrayList<>();
    }

    public Customer(int idNumber, String firstName, String lastName, Date birthDate) {
        this.idNumber = idNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.loans = new ArrayList<>();
    }

    public static void initializeNextId(int maxIdCustomer) {
        nextId.set(maxIdCustomer + 1);
    }

    public int getIdNumber()
    {
        return idNumber;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public Date getBirthDate()
    {
        return birthDate;
    }

    public ArrayList<Loan> getLoans(){
        return this.loans;
    }

    public void setIdNumber(int idNumber)
    {
        this.idNumber = idNumber;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public void setBirthDate(Date birthDate)
    {
        this.birthDate = birthDate;
    }

    public void setLoans(ArrayList<Loan> new_list){
        this.loans = new_list;
    }

    public boolean addLoan(Loan newLoan){
        if(this.getLoans().contains(newLoan)){
            return false;
        }else{
            this.loans.add(newLoan);
            return true;
        }
    }

    public void removeLoan(Loan newLoan) throws LoanNotFoundException{
        if(this.getLoans().contains(newLoan)){
            this.loans.remove(newLoan);
        }else{
            throw new LoanNotFoundException();
        }
    }

    @Override
    public String toString()
    {
        return this.getIdNumber()+") "+this.getFirstName()+" "+this.getLastName();
    }
}
