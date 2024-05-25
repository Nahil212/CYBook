package library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a customer of a library system.
 */
public class Customer {
    /**
     * The next available unique identifier for a new customer.
     */
    private static AtomicInteger nextId = new AtomicInteger(2);
    
    /**
     * The unique identifier of the customer.
     */
    private int idNumber;
    
    /**
     * The first name of the customer.
     */
    private String firstName;
    
    /**
     * The last name of the customer.
     */
    private String lastName;
    
    /**
     * The birth date of the customer.
     */
    private Date birthDate;
    
    /**
     * The list of loans associated with the customer.
     */
    private ArrayList<Loan> loans;

    /**
     * Constructs a new Customer object with the specified first name, last name, and birth date.
     * The customer's ID number is automatically assigned.
     *
     * @param firstName the first name of the customer
     * @param lastName  the last name of the customer
     * @param birthDate the birth date of the customer
     * @throws JSONException    if there is an error parsing the JSON data
     * @throws IOException      if there is an error reading the file
     * @throws ParseException   if there is an error parsing the date
     */
    public Customer(String firstName, String lastName, Date birthDate) throws JSONException, IOException, ParseException {
        this.idNumber = nextId.getAndIncrement();
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.loans = this.selectLoans();
    }

    /**
     * Constructs a new Customer object with the specified ID number, first name, last name, and birth date.
     *
     * @param idNumber  the unique identifier of the customer
     * @param firstName the first name of the customer
     * @param lastName  the last name of the customer
     * @param birthDate the birth date of the customer
     * @throws JSONException    if there is an error parsing the JSON data
     * @throws IOException      if there is an error reading the file
     * @throws ParseException   if there is an error parsing the date
     */
    public Customer(int idNumber, String firstName, String lastName, Date birthDate) throws JSONException, IOException, ParseException {
        this.idNumber = idNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.loans = this.selectLoans();
    }
    
    /**
     * Retrieves the list of loans associated with the customer from the JSON data file.
     *
     * @return the list of loans associated with the customer
     * @throws IOException    if there is an error reading the file
     * @throws JSONException  if there is an error parsing the JSON data
     * @throws ParseException if there is an error parsing the date
     */
    private ArrayList<Loan> selectLoans() throws IOException, JSONException, ParseException {
    	ArrayList<Loan> list = new ArrayList<>();
    	JSONObject data = new JSONObject(new String(Files.readAllBytes(Paths.get(Librarian.filePath))));
    	JSONArray jsonLoans = data.getJSONArray("loans");
    	JSONObject loan;
    	for(int i=0;i<jsonLoans.length();i++) {
    		loan = jsonLoans.getJSONObject(i);
    		if((Integer) loan.get("customerId")==this.getIdNumber()) {
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    			Date effectiveDateBack = loan.isNull("effectiveDateBack") ? null : sdf.parse(loan.getString("effectiveDateBack"));
    			list.add(new Loan(loan.getInt("loanId"),
    					loan.getString("identifier"),
    					sdf.parse(loan.getString("dateLoan")),
    					sdf.parse(loan.getString("plannedDateBack")),
    					effectiveDateBack,
    					loan.getBoolean("returned"),
    					loan.getBoolean("late"),
    					this.getIdNumber()
    					));
    		}
    	}
    	return list;
    }

    /**
     * Initializes the next available unique identifier for a new customer.
     *
     * @param maxIdCustomer the maximum customer ID number currently in use
     */
    public static void initializeNextId(int maxIdCustomer) {
        nextId.set(maxIdCustomer + 1);
    }

    /**
     * Returns the unique identifier of the customer.
     *
     * @return the unique identifier of the customer
     */
    public int getIdNumber() {
        return idNumber;
    }

    /**
     * Returns the first name of the customer.
     *
     * @return the first name of the customer
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the customer.
     *
     * @return the last name of the customer
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the birth date of the customer.
     *
     * @return the birth date of the customer
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Returns the list of loans associated with the customer.
     *
     * @return the list of loans associated with the customer
     */
    public ArrayList<Loan> getLoans() {
        return this.loans;
    }

    /**
     * Sets the unique identifier of the customer.
     *
     * @param idNumber the new unique identifier of the customer
     */
    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    /**
     * Sets the first name of the customer.
     *
     * @param firstName the new first name of the customer
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the last name of the customer.
     *
     * @param lastName the new last name of the customer
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the birth date of the customer.
     *
     * @param birthDate the new birth date of the customer
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Sets the list of loans associated with the customer.
     *
     * @param new_list the new list of loans associated with the customer
     */
    public void setLoans(ArrayList<Loan> new_list) {
        this.loans = new_list;
    }

    /**
     * Adds a new loan to the customer's list of loans.
     *
     * @param newLoan the new loan to be added
     * @return true if the loan was added successfully, false otherwise
     */
    public boolean addLoan(Loan newLoan) {
        if (this.getLoans().contains(newLoan)) {
            return false;
        } else {
            this.loans.add(newLoan);
            return true;
        }
    }

    /**
     * Removes a loan from the customer's list of loans.
     *
     * @param newLoan the loan to be removed
     * @throws LoanNotFoundException if the loan is not found in the customer's list of loans
     */
    public void removeLoan(Loan newLoan) throws LoanNotFoundException {
        if (this.getLoans().contains(newLoan)) {
            this.loans.remove(newLoan);
        } else {
            throw new LoanNotFoundException();
        }
    }

    /**
     * Checks if the customer can borrow a new book based on the number of books currently borrowed.
     *
     * @return true if the customer can borrow a new book, false otherwise
     */
    public boolean canBorrow() {
        int nbBorrow = 0;
        for (Loan loan : this.getLoans()) {
            if (!(loan.getReturned())) {
                nbBorrow++;
            }
            if (nbBorrow >= 10) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a string representation of the customer, including their ID number, first name, and last name.
     *
     * @return a string representation of the customer
     */
    @Override
    public String toString() {
        return this.getIdNumber() + ") " + this.getFirstName() + " " + this.getLastName();
    }
    
	/**
	 * Generates a formatted string containing details of all loans.
	 *
	 * @return formatted string with loan details
	 */
	public String printLoans() {
		StringBuilder sb = new StringBuilder();
		sb.append("Loans list:\n");
		for (Loan loan : this.getLoans()) {
			sb.append("Loan ID: ").append(loan.getId()).append("\n");
			sb.append("Identifier: ").append(loan.getIdentifier()).append("\n");
			sb.append("Date Loan: ").append(loan.getDateLoan()).append("\n");
			sb.append("Planned Date Back: ").append(loan.getPlannedDateBack()).append("\n");
			sb.append("Effective Date Back: ").append(loan.getEffectiveDateBack()).append("\n");
			sb.append("Late: ").append(loan.getLate()).append("\n");
			sb.append("Returned: ").append(loan.getReturned()).append("\n");
			sb.append("CustomerId: ").append(loan.getCustomerId()).append("\n");
			sb.append("\n");
		}
		return sb.toString();
	}
}
