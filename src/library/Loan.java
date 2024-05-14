package library;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Loan {
    private int id;
    private long isbn;
    private Date dateLoan;
    private Date plannedDateBack;
    private Date effectiveDateBack;
    private boolean late;
    private boolean returned;
    private static final String filePath = "../../data/LibraryData.json";

    public Loan(long isbn) {
        this.isbn = isbn;
        this.dateLoan = new Date();
        this.plannedDateBack = calculateScheduledReturnDate();
        this.effectiveDateBack = null;
        this.late = false;
        this.returned = false;
        addToDatabase();
    }

    private void addToDatabase() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject root = new JSONObject(content);
            JSONArray loans = root.getJSONArray("loans");

            JSONObject loanDetails = new JSONObject();
            loanDetails.put("isbn", this.isbn);
            loanDetails.put("dateLoan", new SimpleDateFormat("yyyy-MM-dd").format(this.dateLoan));
            loanDetails.put("plannedDateBack", new SimpleDateFormat("yyyy-MM-dd").format(this.plannedDateBack));
            loanDetails.put("effectiveDateBack", this.effectiveDateBack != null ? new SimpleDateFormat("yyyy-MM-dd").format(this.effectiveDateBack) : JSONObject.NULL);
            loanDetails.put("late", this.late);
            loanDetails.put("returned", this.returned);

            loans.put(loanDetails);

            try (FileWriter file = new FileWriter(filePath)) {
                file.write(root.toString(4));
                file.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Date calculateScheduledReturnDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateLoan);
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        return calendar.getTime();
    }

    public void markBack() {
        this.effectiveDateBack = new Date();
        this.late = calculateLate();
        this.returned = true;
        updateDatabaseOnReturn();
    }

    private void updateDatabaseOnReturn() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject root = new JSONObject(content);
            JSONArray loans = root.getJSONArray("loans");

            for (int i = 0; i < loans.length(); i++) {
                JSONObject loan = loans.getJSONObject(i);
                if (loan.getLong("isbn") == this.isbn) { // Assuming ISBN uniquely identifies a loan
                    loan.put("effectiveDateBack", new SimpleDateFormat("yyyy-MM-dd").format(this.effectiveDateBack));
                    loan.put("late", this.late);
                    loan.put("returned", this.returned);
                    break;
                }
            }

            try (FileWriter file = new FileWriter(filePath)) {
                file.write(root.toString(4));
                file.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean calculateLate() {
        return this.effectiveDateBack != null && this.effectiveDateBack.after(this.plannedDateBack);
    }
}
