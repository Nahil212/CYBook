package library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    	// A MODIFIER !!!!!!!!
        String url = "jdbc:mysql://localhost:3306/Library";
        String user = "username";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO Loan (isbn, dateLoan, plannedDateBack, effectiveDateBack, late, returned) VALUES (?, ?, ?, ?, ?, ?)")) {
            pstmt.setLong(1, this.isbn);
            pstmt.setDate(2, new java.sql.Date(this.dateLoan.getTime()));
            pstmt.setDate(3, new java.sql.Date(this.plannedDateBack.getTime()));
            pstmt.setDate(4, this.effectiveDateBack != null ? new java.sql.Date(this.effectiveDateBack.getTime()) : null);
            pstmt.setBoolean(5, this.late);
            pstmt.setBoolean(6, this.returned);

            pstmt.executeUpdate();
        } catch (SQLException e) {
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

    private boolean calculateLate() {
        return (effectiveDateBack != null && effectiveDateBack.after(plannedDateBack));
    }

    private void updateDatabaseOnReturn() {
        String url = "jdbc:mysql://localhost:3306/Library";
        String user = "username";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE Loan SET effectiveDateBack = ?, late = ?, returned = ? WHERE id = ?")) {
            pstmt.setDate(1, new java.sql.Date(this.effectiveDateBack.getTime()));
            pstmt.setBoolean(2, this.late);
            pstmt.setBoolean(3, this.returned);
            pstmt.setInt(4, this.id);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getId()
    {
        return id;
    }

    public Date getDateLoan()
    {
        return dateLoan;
    }

    public Date getPlannedDateBack()
    {
        return plannedDateBack;
    }

    public Date getEffectiveDateBack()
    {
        return effectiveDateBack;
    }

    public boolean isLate()
    {
        return late;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setDateLoan(Date dateLoan)
    {
        this.dateLoan = dateLoan;
    }

    public void setPlannedDateBack(Date plannedDateBack)
    {
        this.plannedDateBack = plannedDateBack;
    }

    public void setEffectiveDateBack(Date effectiveDateBack)
    {
        this.effectiveDateBack = effectiveDateBack;
    }

    public void setLate(boolean late)
    {
        this.late = late;
    }

    @Override
    public String toString()
    {
        return "Loan{" +
                "id=" + id +
                ", isbn=" + isbn +
                ", dateLoan=" + dateLoan +
                ", plannedBackDate=" + plannedDateBack +
                ", effectiveBackDate=" + effectiveDateBack +
                ", late=" + late +
                ", returned=" + returned +
                '}';
    }
}