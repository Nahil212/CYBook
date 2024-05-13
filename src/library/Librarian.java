package library;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Librarian {
	private String pseudonym;
	private String password;
	private ArrayList<Customer> customers;
	private static final String filePath = "C:/Users/keizo/Downloads/LibraryData.json";

	public Librarian(String pseudonym, String password) {
		this.pseudonym = pseudonym;
		this.password = password;
		this.customers = new ArrayList<Customer>();

		if (authenticate()) {
			fetchCustomers();
		}
	}

	private boolean authenticate() {
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

	private void fetchCustomers() {
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

				Customer customer = new Customer(id, firstName, lastName, birthDate);
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
}
