package library;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a book with its title, creator, publisher, year, identifier, and format.
 */
public class Book {
    /**
     * The title of the book.
     */
    private String title;
    
    /**
     * The creator (author, editor, etc.) of the book.
     */
    private String creator;
    
    /**
     * The publisher of the book.
     */
    private String publisher;
    
    /**
     * The year the book was published.
     */
    private int year;
    
    /**
     * A unique identifier for the book.
     */
    private String identifier;
    
    /**
     * The format of the book.
     */
    private String format;

    /**
     * Constructs a new Book object with the specified title, creator, publisher, year, identifier, and format.
     *
     * @param title      the title of the book
     * @param creator    the creator of the book
     * @param publisher  the publisher of the book
     * @param year       the year the book was published
     * @param identifier a unique identifier for the book
     * @param format     the format of the book
     */
    public Book(String title, String creator, String publisher, int year, String identifier, String format) {
        this.title = title;
        this.creator = creator;
        this.publisher = publisher;
        this.year = year;
        this.identifier = identifier;
        this.format = format;
    }

    /**
     * Returns the title of the book.
     *
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title the new title of the book
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the creator of the book.
     *
     * @return the creator of the book
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Sets the creator of the book.
     *
     * @param creator the new creator of the book
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Returns the publisher of the book.
     *
     * @return the publisher of the book
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher of the book.
     *
     * @param publisher the new publisher of the book
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Returns the year the book was published.
     *
     * @return the year the book was published
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the year the book was published.
     *
     * @param year the new year the book was published
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Returns the unique identifier of the book.
     *
     * @return the unique identifier of the book
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the unique identifier of the book.
     *
     * @param identifier the new unique identifier of the book
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the format of the book.
     *
     * @return the format of the book
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the format of the book.
     *
     * @param format the new format of the book
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Returns a string representation of the book, including its title, creator, publisher, year, identifier, and format.
     *
     * @return a string representation of the book
     */
    public String toString() {
        String s = "";
        s += "Title: " + this.getTitle() +
                "\nAuthor: " + this.getCreator() +
                "\nPublisher: " + this.getPublisher() +
                "\nYear: " + this.getYear() +
                "\nIdentifier: " + this.getIdentifier() +
                "\nFormat: " + this.getFormat();
        return s;
    }

    /**
     * Checks if a book has been borrowed more than 10 times without being returned.
     *
     * @param identifier the identifier to check
     * @return true if the identifier is overborrowed, false otherwise
     */
    public boolean isOverBorrowed() {
        try {
            int count = 0;
            JSONObject jsonObject = new JSONObject();
            JSONArray loansArray = jsonObject.getJSONArray("loans");
            for (int i = 0; i < loansArray.length(); i++) {
                JSONObject loanObj = loansArray.getJSONObject(i);
                if (loanObj.getString("identifier").equals(this.getIdentifier()) && !loanObj.getBoolean("returned")) {
                    count++;
                    if (count == 10) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
