package library;

import java.text.SimpleDateFormat;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import java.util.Scanner;

public class Librarian {
	private String pseudonym;
    private String password;
    private ArrayList<Customer> customers;
	private ArrayList<Loan> loans;
    private static final String filePath = "/home/cytech/CYBook/data/LibraryData.json";

    /**
     * Constructor for the Librarian class.
     *
     * @param pseudonym The pseudonym of the librarian.
     * @param password  The password of the librarian.
     */
    public Librarian(String pseudonym, String password) {
        this.pseudonym = pseudonym;
        this.password = password;
        this.customers = new ArrayList<Customer>();
        if (authentificate()) {
            fetchCustomers();
        }
    }

    private boolean authentificate() {
    	boolean isAuthenticated = false;
		try {
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			JSONObject jsonObject = new JSONObject(content);
			JSONArray librarians = jsonObject.getJSONArray("librarians");
			for (int i = 0; i < librarians.length(); i++) {
				JSONObject librarian = librarians.getJSONObject(i);
				if (librarian.getString("pseudonym").equals(this.pseudonym) && librarian.getString("password").equals(this.password)) {
					isAuthenticated = true;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isAuthenticated;
    }

    public void fetchCustomers() {
    	try {
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			JSONObject jsonObject = new JSONObject(content);
			JSONArray customers = jsonObject.getJSONArray("customers");
			for (int i = 0; i < customers.length(); i++) {
				JSONObject customerObj = customers.getJSONObject(i);
				int id = customerObj.getInt("idNumber");
				String firstName = customerObj.getString("firstName");
				String lastName = customerObj.getString("lastName");
				String birthDateStr = customerObj.getString("birthDate");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date birthDate = sdf.parse(birthDateStr);

				Customer customer = new Customer(firstName, lastName, birthDate);
				this.customers.add(customer);
			}
		} catch (Exception e) {
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

	public String printCustomers(){
		return "pseudonyme = "+pseudonym+" customers list:"+customers;
	}
	public String printLoans(){
		return "pseudonyme = "+pseudonym+" loans list:"+loans;
	}



	/**
	 * This method call the BNF API to search a list of book according to filters
	 *
	 * @param listCreator Searched authors (can be empty)
	 * @param yearStart Minimum year publication (-1 means no filter)
	 * @param yearEnd Maximum year publication (-1 means no filter)
	 * @param listType Searched document types (can be empty)
	 * @param listUniverse Searched document universes (can be empty)
	 * @param searchTitle Enter text to search in titles (can be empty)
	 * @param startResearch Where to start the call of the API so we can manage pages
	 * @return List of Book object corresponding to filters
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws EmptyResearchException
	 * @throws BookNotInDataBaseException
	 */
	public ArrayList<Book> searchBooks(ArrayList<String> listCreator, int yearStart, int yearEnd, ArrayList<Universe> listUniverse, String searchTitle, int startResearch) throws URISyntaxException, IOException, InterruptedException, EmptyResearchException{
		ArrayList<Book> searchedBooks = new ArrayList<Book>();

		// CREATING THE REQUEST TO THE API
		String uri = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=";
		uri += URLEncoder.encode("bib.doctype any \"A\"", StandardCharsets.UTF_8);
		if( !(listCreator.isEmpty()) || !(yearStart == -1) || !(yearEnd == -1) || !(listUniverse.isEmpty()) || !(searchTitle == "")) {
			uri += "+and+";
		}
			if(!(listCreator.isEmpty())) {
				String authors = "";
				for(String creator: listCreator) {
					authors += creator+" ";
				}
				authors = authors.trim();
				uri += URLEncoder.encode("bib.author any \""+authors+"\"", StandardCharsets.UTF_8);
				if( !(yearStart == -1) || !(yearEnd == -1) || !(listUniverse.isEmpty()) || !(searchTitle == "")) {
					uri += "+and+";
				}
			}
			if(!(yearStart == -1) || !(yearEnd == -1)) {
				if(yearStart == yearEnd) {
					uri += URLEncoder.encode("bib.publicationdate= \""+yearStart+"\"", StandardCharsets.UTF_8);
					if(!(listUniverse.isEmpty()) || !(searchTitle == "")) {
						uri += "+and+";
					}
				}else {
					if (yearEnd > -1) {
						uri += URLEncoder.encode("bib.publicationdate<= \""+yearEnd+"\"", StandardCharsets.UTF_8);
						if( !(yearStart == -1) || !(listUniverse.isEmpty()) || !(searchTitle == "")) {
							uri += "+and+";
						}
					}
					if (yearStart > -1) {
						uri += URLEncoder.encode("bib.publicationdate>= \""+yearStart+"\"", StandardCharsets.UTF_8);
						if( !(listUniverse.isEmpty()) || !(searchTitle == "")) {
							uri += "+and+";
						}
					}
				}
			}
			if(!(listUniverse.isEmpty())) {
				String universes = "";
				for(Universe universe: listUniverse) {
					universes+=universe+" ";
				}
				universes = universes.trim();
				uri += URLEncoder.encode("bib.doctype all \""+universes+"\"", StandardCharsets.UTF_8);
				if(!(searchTitle == "")) {
					uri += "+and+";
				}
			}
			if(!(searchTitle == "")) {
				uri += URLEncoder.encode("bib.title any \""+searchTitle+"\"", StandardCharsets.UTF_8);
			}
		uri+= "&startRecord="+startResearch+"&maximumRecords=20&recordSchema=dublincore";
		HttpRequest getRequest = HttpRequest.newBuilder()
				.uri(new URI(uri))
				.GET()
				.build();
		HttpClient httpclient = HttpClient.newHttpClient();
		HttpResponse<String> getResponse = httpclient.send(getRequest, BodyHandlers.ofString());

		// COLLECTING BOOK INFORMATIONS
		try {
			JSONArray record = XML.toJSONObject(getResponse.body()).
					getJSONObject("srw:searchRetrieveResponse").
					getJSONObject("srw:records").
					getJSONArray("srw:record");
			JSONObject obj;
			JSONObject data;
			for (int i=0;i<record.length();i++) {
				 obj = record.getJSONObject(i);
				 data = obj.getJSONObject("srw:recordData").getJSONObject("oai_dc:dc");

				 String ark = obj.getString("srw:recordIdentifier");
				 String publisher = data.optString("dc:publisher", "");
				 String format;
				 if(data.has("dc:format")) {
					format = data.optString("dc:format", "");
				 }else {
					format = "Not specified";
				 }
				 int year;
				 String dateString = data.optString("dc:date", "");
				 if (dateString.contains("-")) {
		                year = Integer.parseInt(dateString.split("-")[0]);
		            } else {
		                year = data.optInt("dc:date", 0);
		            }
				 Object titleObj = data.get("dc:title");
		         String title = "";
		         if (titleObj instanceof JSONArray) {
		        	 JSONArray titles = (JSONArray) titleObj;
		             for (int j=0;j<titles.length();j++) {
		            	 title += titles.getString(j);
		             }
		         } else {
		             title = data.getString("dc:title");
		         }
		         String creator = "Not specified";
		         if(data.has("dc:creator")) {
			         Object CreatorObj = data.get("dc:creator");
			         creator = "";
			         if (CreatorObj instanceof JSONArray) {
			        	 JSONArray creators = (JSONArray) CreatorObj;
			             for (int j=0;j<creators.length();j++) {
			            	 creator += creators.getString(j)+" ";
			             }
			         } else {
			             creator = data.getString("dc:creator");
			         }
		         }
		         searchedBooks.add(new Book(title,creator,publisher,year,ark,format));
			}
			return searchedBooks;
		}catch(JSONException je) {
			throw new EmptyResearchException();
		}
	}

	private static void addToDatabaseLoan(Loan loan, int customerId) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			JSONObject root = new JSONObject(content);
			JSONArray loans = root.getJSONArray("loans");
			JSONArray customers = root.getJSONArray("customers");

			JSONObject loanDetails = new JSONObject();
			loanDetails.put("isbn", loan.getIsbn());
			loanDetails.put("customerId", customerId);
			loanDetails.put("dateLoan", new SimpleDateFormat("yyyy-MM-dd").format(loan.getDateLoan()));
			loanDetails.put("plannedDateBack", new SimpleDateFormat("yyyy-MM-dd").format(loan.getPlannedDateBack()));
			loanDetails.put("effectiveDateBack", loan.getEffectiveDateBack() != null ? new SimpleDateFormat("yyyy-MM-dd").format(loan.getEffectiveDateBack()) : JSONObject.NULL);
			loanDetails.put("late", loan.getLate());
			loanDetails.put("returned", loan.getReturned());
			loanDetails.put("loanId", loan.getId());

			loans.put(loanDetails);

			for (int i = 0; i < customers.length(); i++) {
				JSONObject customerDetails = customers.getJSONObject(i);
				if (customerDetails.getInt("idNumber") == customerId) {
					JSONArray customerLoans = customerDetails.getJSONArray("loans");
					customerLoans.put(loanDetails);
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

	private static void addToDatabaseCustomer(Customer customer) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			JSONObject root = new JSONObject(content);
			JSONArray customers = root.getJSONArray("customers");

			boolean customerExists = false;
			for (int i = 0; i < customers.length(); i++) {
				JSONObject existingCustomer = customers.getJSONObject(i);
				if (existingCustomer.getString("firstName").equals(customer.getFirstName()) &&
						existingCustomer.getString("lastName").equals(customer.getLastName()) &&
						existingCustomer.getString("birthDate").equals(new SimpleDateFormat("yyyy-MM-dd").format(customer.getBirthDate()))) {
					customerExists = true;
					break;
				}
			}
			if (!customerExists) {
				JSONObject customerDetails = new JSONObject();
				customerDetails.put("idNumber", customer.getIdNumber());
				customerDetails.put("firstName", customer.getFirstName());
				customerDetails.put("lastName", customer.getLastName());
				customerDetails.put("birthDate", new SimpleDateFormat("yyyy-MM-dd").format(customer.getBirthDate()));

				JSONArray customerLoans = new JSONArray();
				for (Loan loan : customer.getLoans()) {
					JSONObject loanDetails = new JSONObject();
					customerLoans.put(loanDetails);
				}
				customerDetails.put("loans", customerLoans);

				customers.put(customerDetails);

				try (FileWriter file = new FileWriter(filePath)) {
					file.write(root.toString(4));
					file.flush();
				}
			} else {
				System.out.println("Customer already exists and was not added.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateDatabaseOnReturn(Loan loan) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			JSONObject root = new JSONObject(content);
			JSONArray loans = root.getJSONArray("loans");

			for (int i = 0; i < loans.length(); i++) {
				JSONObject jsonLoan = loans.getJSONObject(i);
				if (jsonLoan.getLong("isbn") == loan.getIsbn()) {
					jsonLoan.put("effectiveDateBack", new SimpleDateFormat("yyyy-MM-dd").format(loan.getEffectiveDateBack()));
					jsonLoan.put("late", loan.getLate());
					jsonLoan.put("returned", loan.getReturned());
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


	public void markBack(Loan loan) {
		loan.setEffectiveDateBack(new Date());
		loan.setLate(loan.calculateLate());
		loan.setReturned(true);
		updateDatabaseOnReturn(loan);
	}

	public static void main(String[] args) throws Exception {
		Librarian rayen = new Librarian("rayen", ":)");
		ArrayList<String> listString = new ArrayList<>();
		int year1 = -1;
		int year2 = -1;
		ArrayList<Universe> listUniverse = new ArrayList<>();
		String searchTitle = "One piece";
		int startResearch = 1;

		ArrayList<Book> listBook = rayen.searchBooks(listString, year1, year2,listUniverse, searchTitle, startResearch);
		for(Book book: listBook) {
			System.out.println(book+ "\n");
		}
	}
}
