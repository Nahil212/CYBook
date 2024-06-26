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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a librarian who manages customers and loans in a library.
 */
public class Librarian {
	/**
	 * The pseudonym of the librarian.
	 */
	private String pseudonym;

	/**
	 * The password of the librarian.
	 */
	private String password;

	/**
	 * The list of customers managed by the librarian.
	 */
	private ArrayList<Customer> customers;

	/**
	 * The list of loans managed by the librarian.
	 */
	private ArrayList<Loan> loans;

	/**
	 * The file path for the library data.
	 */
	protected static final String filePath = "data/LibraryData.json";

	/**
	 * The JSON object representing the library data.
	 */
	private JSONObject jsonObject;

	/**
	 * Constructor for the Librarian class.
	 *
	 * @param pseudonym The pseudonym of the librarian.
	 * @param password  The password of the librarian.
	 */
	public Librarian(String pseudonym, String password) {
		this.pseudonym = pseudonym;
		this.password = password;
		this.customers = new ArrayList<>();
		this.loans = new ArrayList<>();
		try {
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			this.jsonObject = new JSONObject(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.fetchCustomers();
		this.fetchLoans();
	}

	/**
	 * Fetches customers and their loans from the JSON object and populates the respective lists.
	 */
	protected void fetchCustomers() {
		try {
			JSONArray customersArray = this.jsonObject.getJSONArray("customers");
			for (int i = 0; i < customersArray.length(); i++) {
				JSONObject customerObj = customersArray.getJSONObject(i);
				int id = customerObj.getInt("idNumber");
				String firstName = customerObj.getString("firstName");
				String lastName = customerObj.getString("lastName");
				String birthDateStr = customerObj.getString("birthDate");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date birthDate = sdf.parse(birthDateStr);

				Customer customer = new Customer(id, firstName, lastName, birthDate);
				this.customers.add(customer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fetches loans from the JSON object and populates the loans list.
	 */
	protected void fetchLoans() {
		this.loans.clear();
		try {
			JSONArray loansArray = this.jsonObject.getJSONArray("loans");
			for (int i = 0; i < loansArray.length(); i++) {
				JSONObject loanObj = loansArray.getJSONObject(i);
				int loanId = loanObj.getInt("loanId");
				String identifier = loanObj.getString("identifier");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date dateLoan = sdf.parse(loanObj.getString("dateLoan"));
				Date plannedDateBack = sdf.parse(loanObj.getString("plannedDateBack"));
				Date effectiveDateBack = loanObj.isNull("effectiveDateBack") ? null : sdf.parse(loanObj.getString("effectiveDateBack"));
				boolean returned = loanObj.getBoolean("returned");
				boolean late = loanObj.getBoolean("late");
				int customerId = loanObj.getInt("customerId");

				Loan loan = new Loan(loanId, identifier, dateLoan, plannedDateBack, effectiveDateBack, returned, late, customerId);
				this.loans.add(loan);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the pseudonym of the librarian.
	 *
	 * @return pseudonym of the librarian
	 */
	public String getPseudonym() {
		return pseudonym;
	}

	/**
	 * Sets the pseudonym of the librarian.
	 *
	 * @param pseudonym the new pseudonym to set
	 */
	public void setPseudonym(String pseudonym) {
		this.pseudonym = pseudonym;
	}

	/**
	 * Returns the password of the librarian.
	 *
	 * @return password of the librarian
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password of the librarian.
	 *
	 * @param password the new password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the list of customers.
	 *
	 * @return the list of customers
	 */
	public ArrayList<Customer> getCustomers() {
		return customers;
	}

	/**
	 * Sets the list of customers.
	 *
	 * @param customers the new list of customers to set
	 */
	public void setCustomers(ArrayList<Customer> customers) {
		this.customers = customers;
	}

	/**
	 * Returns the list of loans.
	 *
	 * @return the list of loans
	 */
	public ArrayList<Loan> getLoans() {
		return loans;
	}

	/**
	 * Generates a formatted string containing details of all customers.
	 *
	 * @return formatted string with customer details
	 */
	public String printCustomers() {
		StringBuilder sb = new StringBuilder();
		sb.append("Customers list:\n");
		for (Customer customer : this.getCustomers()) {
			sb.append("First Name: ").append(customer.getFirstName()).append("\n");
			sb.append("Last Name: ").append(customer.getLastName()).append("\n");
			sb.append("Birth Date: ").append(customer.getBirthDate()).append("\n");
			sb.append("Customer ID: ").append(customer.getIdNumber()).append("\n");
		}
		return sb.toString();
	}

	/**
	 * Calls the BNF API to search for books according to filters.
	 *
	 * @param creator Searched author (can be empty)
	 * @param yearStart Minimum year of publication (-1 means no filter)
	 * @param yearEnd Maximum year of publication (-1 means no filter)
	 * @param universe Searched document universe (can be empty)
	 * @param searchTitle Text to search in titles (can be empty)
	 * @param startResearch Where to start the API call to manage pages
	 * @return List of Book objects corresponding to filters
	 * @throws EmptyResearchException if the research results are empty
	 */
	public ArrayList<Book> searchBooks(String creator, int yearStart, int yearEnd, Universe universe, String searchTitle, int startResearch) throws EmptyResearchException {
		ArrayList<Book> searchedBooks = new ArrayList<Book>();

		// CREATING THE REQUEST TO THE API
		String uri = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=";
		uri += URLEncoder.encode("bib.doctype any \"A\"", StandardCharsets.UTF_8);
		if (!creator.equals("") || yearStart != -1 || yearEnd != -1 || universe != Universe.NONE || !searchTitle.equals("")) {
			uri += "+and+";
		}
		if (!creator.equals("")) {
			uri += URLEncoder.encode("bib.author any \"" + creator + "\"", StandardCharsets.UTF_8);
			if (yearStart != -1 || yearEnd != -1 || universe != Universe.NONE || !searchTitle.equals("")) {
				uri += "+and+";
			}
		}
		if (yearStart != -1 || yearEnd != -1) {
			if (yearStart == yearEnd) {
				uri += URLEncoder.encode("bib.publicationdate= \"" + yearStart + "\"", StandardCharsets.UTF_8);
				if (universe != Universe.NONE || !searchTitle.equals("")) {
					uri += "+and+";
				}
			} else {
				if (yearEnd > -1) {
					uri += URLEncoder.encode("bib.publicationdate<= \"" + yearEnd + "\"", StandardCharsets.UTF_8);
					if (yearStart != -1 || universe != Universe.NONE || !searchTitle.equals("")) {
						uri += "+and+";
					}
				}
				if (yearStart > -1) {
					uri += URLEncoder.encode("bib.publicationdate>= \"" + yearStart + "\"", StandardCharsets.UTF_8);
					if (universe != Universe.NONE || !searchTitle.equals("")) {
						uri += "+and+";
					}
				}
			}
		}
		if (universe != Universe.NONE) {
			String universes = "" + universe;
			uri += URLEncoder.encode("bib.set all \"" + universes + "\"", StandardCharsets.UTF_8);
			if (!searchTitle.equals("")) {
				uri += "+and+";
			}
		}
		if (!searchTitle.equals("")) {
			uri += URLEncoder.encode("bib.title any \"" + searchTitle + "\"", StandardCharsets.UTF_8);
		}

		// COLLECTING BOOK INFORMATION
		try {
			uri += "&startRecord=" + startResearch + "&maximumRecords=20&recordSchema=dublincore";
			HttpRequest getRequest = HttpRequest.newBuilder()
					.uri(new URI(uri))
					.GET()
					.build();
			HttpClient httpclient = HttpClient.newHttpClient();
			HttpResponse<String> getResponse = httpclient.send(getRequest, BodyHandlers.ofString());
			JSONArray record = XML.toJSONObject(getResponse.body())
					.getJSONObject("srw:searchRetrieveResponse")
					.getJSONObject("srw:records")
					.getJSONArray("srw:record");
			JSONObject obj;
			for (int i = 0; i < record.length(); i++) {
				obj = record.getJSONObject(i);
				searchedBooks.add(createBookFromJSON(obj));
			}
			return searchedBooks;
		} catch (JSONException | InterruptedException | IOException | URISyntaxException e) {
			throw new EmptyResearchException();
		}
	}

	/**
	 * Adds a loan to the database for the given customer.
	 *
	 * @param loan       the loan to add to the database
	 * @param customerId the ID of the customer associated with the loan
	 */
	public void addToDatabaseLoan(Loan loan, int customerId) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			JSONObject root = new JSONObject(content);
			JSONArray loans = root.getJSONArray("loans");
			JSONArray customers = root.getJSONArray("customers");

			int maxIdLoan = root.getInt("maxiIdLoan");
			maxIdLoan += 1;
			loan.setId(maxIdLoan);
			root.put("maxiIdLoan", maxIdLoan);

			JSONObject loanDetails = new JSONObject();
			loanDetails.put("identifier", loan.getIdentifier());
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
			this.loans.add(loan);
			for (Customer customer : this.getCustomers()) {
				if (customer.getIdNumber() == customerId) {
					customer.getLoans().add(loan);
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

	/**
	 * Adds a customer to the database if not already exists.
	 *
	 * @param customer the customer to add to the database
	 */
	protected void addToDatabaseCustomer(Customer customer) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			JSONObject root = new JSONObject(content);
			JSONArray customers = root.getJSONArray("customers");

			int maxIdCustomer = root.getInt("maxiIdCustomer");
			maxIdCustomer += 1;
			customer.setIdNumber(maxIdCustomer);
			root.put("maxiIdCustomer", maxIdCustomer);

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
				this.customers.add(customer);

				customerDetails.put("loans", new JSONArray());

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

	/**
	 * Updates the database when a loan is returned.
	 *
	 * @param loan the loan to update in the database
	 */
	private void updateDatabaseOnReturn(Loan loan) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			JSONObject root = new JSONObject(content);
			JSONArray loans = root.getJSONArray("loans");

			for (int i = 0; i < loans.length(); i++) {
				JSONObject jsonLoan = loans.getJSONObject(i);
				if (jsonLoan.getString("identifier").equals(loan.getIdentifier())) {
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
			for (Loan l : this.getLoans()) {
				if (l.getId() == loan.getId()) {
					l.setReturned(true);
				}
			}
			for (Customer c : this.getCustomers()) {
				if (c.getIdNumber() == loan.getCustomerId()) {
					for (Loan l : c.getLoans()) {
						if (l.getId() == loan.getId()) {
							l.setReturned(true);
							l.setEffectiveDateBack(new Date());
							if (l.getPlannedDateBack().before(new Date())) {
								l.setLate(true);
							} else {
								l.setLate(false);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Marks a loan as returned, updates its effective return date, and checks if it is late.
	 *
	 * @param loan the loan to mark as returned
	 */
	public void markBack(Loan loan) {
		for (Loan l : loans) {
			if (l.getId() == loan.getId()) {
				l.setEffectiveDateBack(new Date());
				l.setReturned(true);
				l.setLate(l.calculateLate());
				updateDatabaseOnReturn(l);
				return;
			}
		}
	}

	/**
	 * Searches for a book in the BNF catalog using the provided ISBN.
	 *
	 * @param isbn The ISBN (International Standard Book Number) of the book to search for.
	 * @return A Book object representing the book found in the BNF catalog.
	 * @throws BookNotInDataBaseException If the book with the provided ISBN is not found in the BNF catalog.
	 */
	public Book searchBookFromISBN(long isbn) throws BookNotInDataBaseException {
		String uri = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=" + URLEncoder.encode("bib.isbn= \"" + isbn + "\"", StandardCharsets.UTF_8) + "&recordSchema=dublincore";
		try {
			HttpRequest getRequest = HttpRequest.newBuilder()
					.uri(new URI(uri))
					.GET()
					.build();
			HttpClient httpclient = HttpClient.newHttpClient();
			HttpResponse<String> getResponse = httpclient.send(getRequest, BodyHandlers.ofString());
			JSONObject obj = XML.toJSONObject(getResponse.body())
					.getJSONObject("srw:searchRetrieveResponse")
					.getJSONObject("srw:records")
					.getJSONObject("srw:record");
			return createBookFromJSON(obj);
		} catch (JSONException | InterruptedException | IOException | URISyntaxException e) {
			throw new BookNotInDataBaseException();
		}
	}

	/**
	 * Searches for a book in the BNF catalog using the provided ISSN.
	 *
	 * @param issn The ISSN (International Standard Serial Number) of the book to search for.
	 * @return A Book object representing the book found in the BNF catalog.
	 * @throws BookNotInDataBaseException If the book with the provided ISSN is not found in the BNF catalog.
	 */
	public Book searchBookFromISSN(long issn) throws BookNotInDataBaseException {
		String uri = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=" + URLEncoder.encode("bib.issn= \"" + issn + "\"", StandardCharsets.UTF_8) + "&recordSchema=dublincore";
		try {
			HttpRequest getRequest = HttpRequest.newBuilder()
					.uri(new URI(uri))
					.GET()
					.build();
			HttpClient httpclient = HttpClient.newHttpClient();
			HttpResponse<String> getResponse = httpclient.send(getRequest, BodyHandlers.ofString());
			JSONObject obj = XML.toJSONObject(getResponse.body())
					.getJSONObject("srw:searchRetrieveResponse")
					.getJSONObject("srw:records")
					.getJSONObject("srw:record");
			return createBookFromJSON(obj);
		} catch (URISyntaxException | IOException | InterruptedException | JSONException je) {
			throw new BookNotInDataBaseException();
		}
	}

	/**
	 * Searches for a book in the BNF catalog using the provided identifier (ARK).
	 *
	 * @param ark The ARK (Archival Resource Key) identifier of the book to search for.
	 * @return A Book object representing the book found in the BNF catalog.
	 * @throws BookNotInDataBaseException If the book with the provided identifier is not found in the BNF catalog.
	 */
	public Book searchBookFromIdentifier(String ark) throws BookNotInDataBaseException {
		String uri = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=" + URLEncoder.encode("bib.persistentid= \"" + ark + "\"", StandardCharsets.UTF_8) + "&recordSchema=dublincore";
		try {
			HttpRequest getRequest = HttpRequest.newBuilder()
					.uri(new URI(uri))
					.GET()
					.build();
			HttpClient httpclient = HttpClient.newHttpClient();
			HttpResponse<String> getResponse = httpclient.send(getRequest, BodyHandlers.ofString());
			JSONObject obj = XML.toJSONObject(getResponse.body())
					.getJSONObject("srw:searchRetrieveResponse")
					.getJSONObject("srw:records")
					.getJSONObject("srw:record");
			return createBookFromJSON(obj);
		} catch (URISyntaxException | IOException | InterruptedException | JSONException je) {
			throw new BookNotInDataBaseException();
		}
	}

	/**
	 * Creates a Book object from the provided JSON data obtained from the BNF catalog API response.
	 *
	 * @param obj The JSONObject containing the book data.
	 * @return A Book object representing the book data.
	 */
	private Book createBookFromJSON(JSONObject obj) {
		JSONObject data = obj.getJSONObject("srw:recordData").getJSONObject("oai_dc:dc");
		String ark = obj.getString("srw:recordIdentifier");
		String publisher = data.optString("dc:publisher", "");
		String format;

		if (data.has("dc:format")) {
			format = data.optString("dc:format", "");
		} else {
			format = "Not specified";
		}
		int year;
		String dateString = data.optString("dc:date", "");
		if (dateString.contains("-")) {
			try {
				year = Integer.parseInt(dateString.split("-")[0] + dateString.split("-")[1]) / 2;
			} catch (NumberFormatException e) {
				StringBuilder sb1 = new StringBuilder(dateString.split("-")[0]);
				StringBuilder sb2 = new StringBuilder(dateString.split("-")[0]);
				for (int i = 0; i < sb1.length(); i++) {
					if (sb1.charAt(i) == '.') {
						sb1.setCharAt(i, '0');
					}
					if (sb2.charAt(i) == '.') {
						sb2.setCharAt(i, '0');
					}
				}
				int y1 = Integer.parseInt(sb1.toString());
				int y2 = Integer.parseInt(sb2.toString());
				if (y1 > y2) {
					year = y1;
				} else {
					year = (y1 + y2) / 2;
				}
			}
		} else {
			year = data.optInt("dc:date", 0);
		}
		Object titleObj = data.get("dc:title");
		String title = "";
		if (titleObj instanceof JSONArray) {
			JSONArray titles = (JSONArray) titleObj;
			for (int j = 0; j < titles.length(); j++) {
				title += titles.getString(j);
			}
		} else {
			title = data.getString("dc:title");
		}
		String creator = "Not specified";
		if (data.has("dc:creator")) {
			Object CreatorObj = data.get("dc:creator");
			creator = "";
			if (CreatorObj instanceof JSONArray) {
				JSONArray creators = (JSONArray) CreatorObj;
				for (int j = 0; j < creators.length(); j++) {
					creator += creators.getString(j) + " ";
				}
			} else {
				creator = data.getString("dc:creator");
			}
		}
		return (new Book(title, creator, publisher, year, ark, format));
	}

	/**
	 * Checks if an identifier has been borrowed more than 10 times without being returned.
	 *
	 * @param identifier the identifier to check
	 * @return true if the identifier is overborrowed, false otherwise
	 */
	public boolean isIdentifierOverBorrowed(String identifier) {
		try {
			int count = 0;
			JSONArray loansArray = this.jsonObject.getJSONArray("loans");
			for (int i = 0; i < loansArray.length(); i++) {
				JSONObject loanObj = loansArray.getJSONObject(i);
				if (loanObj.getString("identifier").equals(identifier) && !loanObj.getBoolean("returned")) {
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

	/**
	 * Finds and returns the most borrowed books in the past 30 days.
	 * @throws MissingDataFileException If the data file is missing.
	 * @throws BookNotInDataBaseException If a book is not found in the database.
	 * @throws URISyntaxException If there is a URI syntax error.
	 * @throws IOException If an I/O error occurs.
	 * @throws InterruptedException If the operation is interrupted.
	 */
	@SuppressWarnings("finally")
	public ArrayList<Book> MostFamousLoan() throws MissingDataFileException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date();
		ArrayList<Book> mostFamousBooksWithCount = new ArrayList<>();
		try {
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			JSONObject root = new JSONObject(content);
			JSONArray loansJson = root.getJSONArray("loans");

			HashMap<String, Integer> loanCount = new HashMap<>();

			for (int i = 0; i < loansJson.length(); i++) {
				JSONObject loanJson = loansJson.getJSONObject(i);
				String dateLoanStr = loanJson.getString("dateLoan");

				try {
					Date dateLoan = sdf.parse(dateLoanStr);
					long diff = currentDate.getTime() - dateLoan.getTime();
					long diffDays = diff / (24 * 60 * 60 * 1000);

					if (diffDays <= 30) {
						String identifier = loanJson.getString("identifier");
						loanCount.put(identifier, loanCount.getOrDefault(identifier, 0) + 1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			List<Map.Entry<String, Integer>> sortedLoanCount = loanCount.entrySet()
					.stream()
					.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
					.limit(20)
					.collect(Collectors.toList());

			for (Map.Entry<String, Integer> entry : sortedLoanCount) {
				String ark = entry.getKey();
				Book currentBook = this.searchBookFromIdentifier(ark);
				mostFamousBooksWithCount.add(currentBook);
			}
		} catch (JSONException | IOException je) {
			throw new MissingDataFileException();
		} catch (BookNotInDataBaseException bE) {
			bE.printStackTrace();
		} finally {
			return mostFamousBooksWithCount;
		}
	}

	/**
	 * Updates a customer's information in the database.
	 *
	 * @param customerId    The ID of the customer to update.
	 * @param newFirstName  The new first name of the customer.
	 * @param newLastName   The new last name of the customer.
	 * @param newBirthDate  The new birth date of the customer.
	 * @return true if the customer was updated, false otherwise.
	 */
	public boolean updateCustomer(int customerId, String newFirstName, String newLastName, String newBirthDate) {
		boolean updated = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			JSONObject root = new JSONObject(content);
			JSONArray customers = root.getJSONArray("customers");

			for (int i = 0; i < customers.length(); i++) {
				JSONObject customer = customers.getJSONObject(i);
				if (customer.getInt("idNumber") == customerId) {
					customer.put("firstName", newFirstName);
					customer.put("lastName", newLastName);
					customer.put("birthDate", newBirthDate);
					updated = true;
					break;
				}
			}
			for (Customer customer : this.getCustomers()) {
				if (customer.getIdNumber() == customerId) {
					customer.setFirstName(newFirstName);
					customer.setLastName(newLastName);
					customer.setBirthDate(sdf.parse(newBirthDate));
					break;
				}
			}
			try (FileWriter file = new FileWriter(filePath)) {
				file.write(root.toString(4));
				file.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updated;
	}

	/**
	 * Generates a formatted string containing details of all loans.
	 *
	 * @return formatted string with loan details
	 */
	public String printLoans() {
		StringBuilder sb = new StringBuilder();
		sb.append("Loans list:\n");
		for (Loan loan : loans) {
			sb.append("Loan ID: ").append(loan.getId()).append("\n");
			sb.append("Identifier: ").append(loan.getIdentifier()).append("\n");
			sb.append("Date Loan: ").append(loan.getDateLoan()).append("\n");
			sb.append("Planned Date Back: ").append(loan.getPlannedDateBack()).append("\n");
			sb.append("Effective Date Back: ").append(loan.getEffectiveDateBack()).append("\n");
			sb.append("Late: ").append(loan.getLate()).append("\n");
			sb.append("Returned: ").append(loan.getReturned()).append("\n");
			sb.append("Customer ID: ").append(loan.getCustomerId()).append("\n");
			sb.append("\n");
		}
		return sb.toString();
	}
}
