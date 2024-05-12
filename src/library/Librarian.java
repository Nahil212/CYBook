package library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class Librarian {
	private String pseudonym;
    private String password;
    private ArrayList<Customer> customers;

    public Librarian(String pseudonym, String password) {
        this.pseudonym = pseudonym;
        this.password = password;
        this.customers = new ArrayList<Customer>();

        if (authentificate()) {
            fetchCustomers();
        }
    }

    private boolean authentificate() {
    	// A MODIFIER !!!!!
        String url = "jdbc:mysql://localhost:3306/Library";
        String user = "username";
        String dbPassword = "password";
        boolean isAuthenticated = false;

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Librarian WHERE pseudonym = ? AND pwd = ?")) {
            pstmt.setString(1, this.pseudonym);
            pstmt.setString(2, this.password);

            try (ResultSet rs = pstmt.executeQuery()) {
                isAuthenticated = rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isAuthenticated;
    }

    private void fetchCustomers() {
    	// A MODIFIER
        String url = "jdbc:mysql://localhost:3306/Library";
        String user = "username";
        String dbPassword = "password";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Customer");
            ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                java.sql.Date birthDate = rs.getDate("birthDate");
                Customer customer = new Customer(id, firstName, lastName, new java.util.Date(birthDate.getTime()));
                this.customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
    }
	
	/**
	 * This method call the BNF API to search a book according to its ISBN and create a Book object containing related informations
	 *
	 * @param isbn International Standard Book Number
	 * @return Book object corresponding to the ISBN
	 * @throws URISyntaxException 
	 * @throws InterruptedException 
	 * @throws BookNotInDataBaseException 
	 */
	public Book searchBookFromIsbn(long isbn) throws Exception {
		String isbnQuery = "bib.isbn all \""+isbn+"\"";
		String uri = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=" + URLEncoder.encode(isbnQuery, StandardCharsets.UTF_8) + "&recordSchema=dublincore";
		HttpRequest getRequest = HttpRequest.newBuilder()
				.uri(new URI(uri))
				.GET()
				.build();
		HttpClient httpclient = HttpClient.newHttpClient();
		HttpResponse<String> getResponse = httpclient.send(getRequest, BodyHandlers.ofString());	
		JSONObject obj = XML.toJSONObject(getResponse.body());
		
		try {
			JSONObject data = obj.getJSONObject("srw:searchRetrieveResponse").
					getJSONObject("srw:records").
					getJSONObject("srw:record").
					getJSONObject("srw:recordData").
					getJSONObject("oai_dc:dc");
			Book book = new Book(data.getString("dc:title").replaceAll("/.*", ""), data.getString("dc:creator").replaceAll("\\(.*", ""), data.getString("dc:publisher"), data.getInt("dc:date"), isbn, data.getString("dc:format"));
			return book;
		}catch(JSONException jE) {
			throw new BookNotInDataBaseException();
		}
	}
	
	public ArrayList<Book> searchBook(ArrayList<String> listCreator, int yearStart, int yearEnd, ArrayList<DocType> listType, ArrayList<Universe> listUniverse, String searchTitle, int startResearch){
		ArrayList<Book> searchedBooks = new ArrayList<Book>();
		String uri = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=";
		
		if(!(listCreator == null)) {
			String authors = "";
			for(String creator: listCreator) {
				authors += creator+" ";
			}
			uri += URLEncoder.encode("bib.author any \""+authors+"\"", StandardCharsets.UTF_8)+ "and ";
		}
		if(!(yearStart == -1) || !(yearEnd == -1)) {
			if(yearStart == yearEnd) {
				uri += URLEncoder.encode("bib.publicationdate = \""+yearStart+"\"", StandardCharsets.UTF_8)+ " and ";
			}else if(yearStart == -1) {
				uri += URLEncoder.encode("bib.publicationdate <= \""+yearEnd+"\"", StandardCharsets.UTF_8)+ " and ";
			}else {
				uri += URLEncoder.encode("bib.publicationdate >= \""+yearStart+"\"", StandardCharsets.UTF_8)+ " and ";
			}
		}
		if(!(listType == null)) {
			String types ="";
			for(DocType type: listType) {
				types+= type+" ";
			}
			uri += URLEncoder.encode("bib.doctype any \""+types+"\"", StandardCharsets.UTF_8)+ "and ";
		}
		if(!(listUniverse == null)) {
			String universes = "";
			for(Universe universe: listUniverse) {
				universes+=universe+" ";
			}
			uri += URLEncoder.encode("bib.doctype all \""+universes+"\"", StandardCharsets.UTF_8)+ "and ";
		}
		if(!(searchTitle == null)) {
			uri += URLEncoder.encode("bib.author any \""+searchTitle+"\"", StandardCharsets.UTF_8);
		}
		uri+= " &startRecord="+startResearch+"&maximumRecords=20&recorsSchema=dublincore";
		System.out.println(uri);
		return searchedBooks;
	}
	
	public static void main(String[] args) throws Exception {
		Librarian rayen = new Librarian("rayen", ":)");
	}

}