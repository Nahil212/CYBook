package library;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Customer {
    private int idNumber;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private ArrayList<Loan> loans;
    private static final String filePath = "C:/Users/keizo/Downloads/LibraryData.json";

    public Customer(int idNumber, String firstName, String lastName, Date birthDate) {
        this.idNumber = idNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.loans = new ArrayList<Loan>();
        addToDatabase();
    }

    private void addToDatabase() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject root = new JSONObject(content);
            JSONArray customers = root.getJSONArray("customers");

            JSONObject customerDetails = new JSONObject();
            customerDetails.put("idNumber", this.idNumber);
            customerDetails.put("firstName", this.firstName);
            customerDetails.put("lastName", this.lastName);
            customerDetails.put("birthDate", new SimpleDateFormat("yyyy-MM-dd").format(this.birthDate));

            JSONArray customerLoans = new JSONArray();
            for (Loan loan : this.loans) {
                JSONObject loanDetails = new JSONObject();
                customerLoans.put(loanDetails);
            }
            customerDetails.put("loans", customerLoans);

            customers.put(customerDetails);

            try (FileWriter file = new FileWriter(filePath)) {
                file.write(root.toString(4));
                file.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        return "Customer{" +
                "idNumber=" + idNumber +
                ", first name='" + firstName + '\'' +
                ", last name='" + lastName + '\'' +
                ", Date of birth='" + birthDate + '\'' +
                ", number of loan='" + this.loans.size() +
                '}';
    }
}
