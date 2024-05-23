package library;

import org.json.JSONArray;
import org.json.JSONObject;

public class Book {
	private String title;
	private String creator;
	private String publisher;
	private int year;
	private String identifier;
	private String format;
	
	public Book(String title, String creator, String publisher, int year, String identifier,String format) {
		this.title = title;
		this.creator = creator;
		this.publisher = publisher;
		this.year = year;
		this.identifier = identifier;
		this.format = format;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	
	public String toString() {
		String s = "";
		s += "Title: "+this.getTitle()+
				"\nAuthor: "+this.getCreator()+
				"\nPublisher: "+this.getPublisher()+
				"\nYear: "+this.getYear()+
				"\nIdentifier: "+this.getIdentifier()+
				"\nFormat: "+this.getFormat();
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
