package library;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LibraryApplication extends Application{
	private int searchStart = 1;
	private ArrayList<Book> searchedBooks = new ArrayList<Book>();
	private BorderPane borderPane;
	private Librarian librarian;

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("CY-Book");
		Image logoshessh = new Image("file:img/book.png");
		stage.getIcons().add(logoshessh);
		BorderPane borderPane = new BorderPane();
		Scene homePage = new Scene(borderPane);
		String content = new String(Files.readAllBytes(Paths.get(Librarian.filePath)));
	        JSONObject jsonObject = new JSONObject(content);
	        JSONArray librarians = jsonObject.getJSONArray("librarians");
	        Librarian librarian = new Librarian("","");
		
		// SIGN IN PAGE
		VBox signInVBox = new VBox();
		signInVBox.setAlignment(Pos.CENTER);
		signInVBox.setSpacing(5);
		Scene signInPage = new Scene(signInVBox);
		
		Label pseudonym = new Label("Pseudonym");
		Label password = new Label("Password");
		TextField pseudoField = new TextField();
		pseudoField.setMaxWidth(200);
		PasswordField passwordField = new PasswordField();
		passwordField.setMaxWidth(200);
		Button signInButton = new Button("Sign in");
		Label incorrect = new Label("Incorrect informations");
		signInButton.setOnAction(signIn->{
			boolean isAuthentificated = false;
			for (int i = 0; i < librarians.length(); i++) {
                JSONObject lib = librarians.getJSONObject(i);
                if(lib.getString("pseudonym").equals(pseudoField.getText()) && lib.getString("password").equals(passwordField.getText())) {
                	isAuthentificated = true;
                	break;
                }
            }
			if (isAuthentificated) {
				stage.setScene(homePage);
			}else {
				incorrect.setVisible(true);
			}
		});
				
		signInVBox.getChildren().addAll(pseudonym,pseudoField,password,passwordField,signInButton,incorrect);
		incorrect.setVisible(false);
		
		// HOME PAGE
		
		// Top
		VBox filter = new VBox();
		filter.setAlignment(Pos.CENTER);
		filter.setSpacing(10);
		filter.setPrefHeight(125);
		
		Label idSearch = new Label("Search by identifier");
		HBox idFields = new HBox();
		idFields.setAlignment(Pos.TOP_CENTER);
		TextField isbn = new TextField();
		isbn.setPromptText("ISBN");
		Button searchISBN = new Button("Search");
		searchISBN.setOnAction(sI->{
			searchedBooks.clear();
			try {
				Book book = librarian.searchBookFromISBN(Long.parseLong(isbn.getText()));
				searchedBooks.add(book);
			}catch(BookNotInDataBaseException e) {
				// to complete
			}
		});
		TextField issn = new TextField();
		Button searchISSN = new Button("Search");
		searchISSN.setOnAction(sI->{
			searchedBooks.clear();
			try {
				Book book = librarian.searchBookFromISSN(Long.parseLong(isbn.getText()));
				searchedBooks.add(book);
			}catch(BookNotInDataBaseException e) {
				// to complete
			}
		});
		issn.setPromptText("ISSN");
		TextField ark = new TextField();
		Button searchARK = new Button("Search");
		ark.setPromptText("ARK");
		idFields.getChildren().addAll(isbn,searchISBN,issn,searchISSN,ark,searchARK);
		
		Label filterSearch = new Label("Search by filters");
		HBox filterFields = new HBox();
		filterFields.setAlignment(Pos.TOP_CENTER);
		TextField titleField = new TextField();
		titleField.setPromptText("Title");
		TextField authorField = new TextField();
		authorField.setPromptText("Author");
		TextField yearStartField = new TextField();
		yearStartField.setPromptText("Minimal year");
		TextField yearEndField = new TextField();
		yearEndField.setPromptText("Maximal year");
		ChoiceBox<Universe> universeBox = new ChoiceBox<>();
		universeBox.getItems().addAll(Universe.values());
		universeBox.setValue(Universe.NONE);
		ImageView imgSearchFilter = new ImageView(new Image("file:img/search.png"));
		imgSearchFilter.setFitWidth(15); 
		imgSearchFilter.setFitHeight(15);
		Button searchButtonFilter = new Button();
		searchButtonFilter.setGraphic(imgSearchFilter);
		filterFields.getChildren().addAll(titleField,authorField,yearStartField,yearEndField,universeBox,searchButtonFilter);
		filter.getChildren().addAll(idSearch,idFields,filterSearch,filterFields);
		
		
		// Left
		VBox icons = new VBox();
		icons.setAlignment(Pos.TOP_CENTER);
		icons.setPrefWidth(150);
		icons.setSpacing(10);
		ImageView imgSearch = new ImageView(new Image("file:img/search.png"));
		ImageView imgBook = new ImageView(new Image("file:img/book.png"));
		ImageView imgUser = new ImageView(new Image("file:img/user.png"));
		imgSearch.setFitWidth(75); 
		imgSearch.setFitHeight(75);
		imgBook.setFitWidth(75); 
		imgBook.setFitHeight(75);
		imgUser.setFitWidth(75); 
		imgUser.setFitHeight(75);
		Button searchButton = new Button();
		Button loanButton = new Button();
		Button userButton = new Button();
		searchButton.setGraphic(imgSearch);
		loanButton.setGraphic(imgBook);
		userButton.setGraphic(imgUser);
		icons.getChildren().addAll(searchButton,loanButton,userButton);
		
		
		// Middle
		ScrollPane scrollBook = new ScrollPane();
		scrollBook.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		VBox bookVBox = new VBox();
		bookVBox.setSpacing(10);
		scrollBook.setContent(bookVBox);
		
		
		// Bottom
		HBox changePage = new HBox();
		Button precedent = new Button("<<");
		Button next = new Button(">>");
		precedent.setVisible(false);
		next.setVisible(false);
		changePage.getChildren().addAll(precedent,next);
		changePage.setAlignment(Pos.CENTER);
		changePage.setPrefHeight(50);
		changePage.setSpacing(10);
		
		borderPane.setLeft(icons);
		borderPane.setTop(filter);
		borderPane.setCenter(scrollBook);
		borderPane.setBottom(changePage);
		
		// IMPLEMENTATION
		searchISBN.setOnAction(sI->{
			this.searchStart=1;
			searchedBooks.clear();
			try {
				Book book = librarian.searchBookFromISBN(Long.parseLong(isbn.getText().toString()));
				searchedBooks.add(book);
				this.visibleButton(precedent,next);
				displayBooksInVBox(bookVBox);
			}catch(BookNotInDataBaseException e) {
				displayNoResult(bookVBox);
			}
		});
		searchISSN.setOnAction(sI->{
			this.searchStart=1;
			searchedBooks.clear();
			try {
				Book book = librarian.searchBookFromISSN(Long.parseLong(issn.getText().toString()));
				searchedBooks.add(book);
				this.visibleButton(precedent,next);
				displayBooksInVBox(bookVBox);
			}catch(BookNotInDataBaseException e) {
				displayNoResult(bookVBox);
			}
		});
		searchARK.setOnAction(sI->{
			this.searchStart=1;
			searchedBooks.clear();
			try {
				Book book = librarian.searchBookFromIdentifier(ark.getText().toString());
				searchedBooks.add(book);
				this.visibleButton(precedent,next);
				displayBooksInVBox(bookVBox);
			}catch(BookNotInDataBaseException e) {
				displayNoResult(bookVBox);
			}
		});
		searchButtonFilter.setOnAction(e->{
			this.searchStart=1;
			String title = titleField.getText().toString();
			String author = authorField.getText().toString();
			int yearStart = -1;
			int yearEnd = -1;
			if(!yearStartField.getText().equals("")) {
				yearStart = Integer.parseInt(yearStartField.getText());
			}
			if(!yearEndField.getText().equals("")) {
				yearEnd = Integer.parseInt(yearEndField.getText());
			}
			Universe univ = universeBox.getSelectionModel().getSelectedItem();
			try {
				this.searchedBooks = librarian.searchBooks(author, yearStart, yearEnd, univ, title, this.searchStart);
				this.visibleButton(precedent,next);
				this.displayBooksInVBox(bookVBox);
			}catch(EmptyResearchException eRE) {
				this.displayNoResult(bookVBox);
			}
		});
		
		next.setOnAction(e->{
			this.searchStart+=20;
			String title = titleField.getText().toString();
			String author = authorField.getText().toString();
			int yearStart = -1;
			int yearEnd = -1;
			if(!yearStartField.getText().equals("")) {
				yearStart = Integer.parseInt(yearStartField.getText());
			}
			if(!yearEndField.getText().equals("")) {
				yearEnd = Integer.parseInt(yearEndField.getText());
			}
			Universe univ = universeBox.getSelectionModel().getSelectedItem();
			try {
				this.searchedBooks = librarian.searchBooks(author, yearStart, yearEnd, univ, title, this.searchStart);
				this.visibleButton(precedent,next);
				this.displayBooksInVBox(bookVBox);
			}catch(EmptyResearchException eRE) {
				this.displayNoResult(bookVBox);
			}
		});
		
		precedent.setOnAction(e->{
			this.searchStart-=20;
			String title = titleField.getText().toString();
			String author = authorField.getText().toString();
			int yearStart = -1;
			int yearEnd = -1;
			if(!yearStartField.getText().equals("")) {
				yearStart = Integer.parseInt(yearStartField.getText());
			}
			if(!yearEndField.getText().equals("")) {
				yearEnd = Integer.parseInt(yearEndField.getText());
			}
			Universe univ = universeBox.getSelectionModel().getSelectedItem();
			try {
				this.searchedBooks = librarian.searchBooks(author, yearStart, yearEnd, univ, title, this.searchStart);
				this.visibleButton(precedent,next);
				this.displayBooksInVBox(bookVBox);
			}catch(EmptyResearchException eRE) {
				this.displayNoResult(bookVBox);
			}
		});
		
		stage.setScene(homePage);
		stage.show();
	}
	
	private VBox createBookDisplayer(Book book) {
		VBox bookInfo = new VBox();
		Label title = new Label(book.getTitle());
		Label author = new Label(book.getCreator());
		Label year = new Label(book.getYear()+"");
		Label publisher = new Label(book.getPublisher());
		Label format = new Label(book.getFormat());
		bookInfo.getChildren().addAll(title,author,year,publisher,format);
		return bookInfo;
	}
	
	private void displayBooksInVBox(VBox bookVbox) {
		bookVbox.getChildren().clear();
		for(Book book: this.searchedBooks) {
			bookVbox.getChildren().add(createBookDisplayer(book));
		}
	}
	
	private void displayNoResult(VBox bookVbox) {
		bookVbox.getChildren().clear();
		bookVbox.getChildren().add(new Label("No result"));
	}
	
	private void visibleButton(Button precedent, Button next) {
		if (this.searchedBooks.size()<20) {
			next.setVisible(false);;
		}else {
			next.setVisible(true);;
		}
		if (this.searchStart>1) {
			precedent.setVisible(true);
		}else {
			precedent.setVisible(false);
		}
	}

	// Méthode pour charger les utilisateurs à partir du fichier JSON
	private List<Customer> loadCustomers() {
		List<Customer> customers = new ArrayList<>();
		try {
			String content = new String(Files.readAllBytes(Paths.get(Librarian.filePath)));
			JSONObject root = new JSONObject(content);
			JSONArray customersArray = root.getJSONArray("customers");

			for (int i = 0; i < customersArray.length(); i++) {
				JSONObject customerObj = customersArray.getJSONObject(i);
				int id = customerObj.getInt("idNumber");
				String firstName = customerObj.getString("firstName");
				String lastName = customerObj.getString("lastName");
				String birthDateStr = customerObj.getString("birthDate");

				Customer customer = new Customer(id, firstName, lastName, new SimpleDateFormat("yyyy-MM-dd").parse(birthDateStr));
				customers.add(customer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customers;
	}

	// Méthode pour afficher les utilisateurs sous forme de boutons
	private void displayUsers(List<Customer> customers) {
		VBox userListVBox = new VBox();
		userListVBox.setAlignment(Pos.CENTER);
		userListVBox.setSpacing(10);
		for (Customer customer : customers) {
			Button customerButton = new Button(customer.getFirstName() + " " + customer.getLastName());
			customerButton.setOnAction(e -> showCustomerOptions(customer));
			userListVBox.getChildren().add(customerButton);
		}
		ScrollPane scrollPane = new ScrollPane(userListVBox);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		borderPane.setCenter(scrollPane);
	}

	// Méthode pour afficher les options de modification du client
	private void showCustomerOptions(Customer customer) {
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(borderPane.getScene().getWindow());

		VBox dialogVBox = new VBox();
		dialogVBox.setAlignment(Pos.CENTER);
		dialogVBox.setSpacing(10);

		Label messageLabel = new Label("Do you want to modify the customer?");
		Button modifyButton = new Button("Modify");
		Button showLoansButton = new Button("Show Loans");

		modifyButton.setOnAction(e -> {
			dialog.close();
			showCustomerEditDialog(customer);
		});
		showLoansButton.setOnAction(e -> {
			dialog.close();
			showCustomerLoans(customer);
		});

		dialogVBox.getChildren().addAll(messageLabel, modifyButton, showLoansButton);

		Scene dialogScene = new Scene(dialogVBox, 300, 200);
		dialog.setScene(dialogScene);
		dialog.show();
	}

	// Méthode pour afficher la boîte de dialogue de modification du client
	private void showCustomerEditDialog(Customer customer) {
		Stage editDialog = new Stage();
		editDialog.initModality(Modality.APPLICATION_MODAL);
		editDialog.initOwner(borderPane.getScene().getWindow());

		VBox editVBox = new VBox();
		editVBox.setAlignment(Pos.CENTER);
		editVBox.setSpacing(10);

		TextField idField = new TextField(String.valueOf(customer.getIdNumber()));
		idField.setEditable(false);
		TextField firstNameField = new TextField(customer.getFirstName());
		TextField lastNameField = new TextField(customer.getLastName());
		TextField birthDateField = new TextField(new SimpleDateFormat("yyyy-MM-dd").format(customer.getBirthDate()));
		Button updateButton = new Button("Update");

		updateButton.setOnAction(e -> {
			boolean success = librarian.updateCustomer(
					customer.getIdNumber(),
					firstNameField.getText(),
					lastNameField.getText(),
					birthDateField.getText()
			);
			if (success) {
				editDialog.close();
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Success");
				alert.setHeaderText(null);
				alert.setContentText("Customer updated successfully.");
				alert.showAndWait();
				displayUsers(loadCustomers());
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Failed to update customer.");
				alert.showAndWait();
			}
		});

		editVBox.getChildren().addAll(new Label("ID"), idField, new Label("First Name"), firstNameField, new Label("Last Name"), lastNameField, new Label("Birth Date (yyyy-MM-dd)"), birthDateField, updateButton);

		Scene editDialogScene = new Scene(editVBox, 300, 400);
		editDialog.setScene(editDialogScene);
		editDialog.show();
	}

	// Méthode pour afficher les emprunts d'un client depuis le fichier JSON
	private void showCustomerLoans(Customer customer) {
		Stage loansDialog = new Stage();
		loansDialog.initModality(Modality.APPLICATION_MODAL);
		loansDialog.initOwner(borderPane.getScene().getWindow());

		VBox loansVBox = new VBox();
		loansVBox.setAlignment(Pos.CENTER);
		loansVBox.setSpacing(10);

		try {
			String content = new String(Files.readAllBytes(Paths.get(Librarian.filePath)));
			JSONObject root = new JSONObject(content);
			JSONArray customersArray = root.getJSONArray("customers");

			for (int i = 0; i < customersArray.length(); i++) {
				JSONObject customerObj = customersArray.getJSONObject(i);
				if (customerObj.getInt("idNumber") == customer.getIdNumber()) {
					JSONArray loansArray = customerObj.getJSONArray("loans");
					for (int j = 0; j < loansArray.length(); j++) {
						JSONObject loanObj = loansArray.getJSONObject(j);
						Label loanLabel = new Label("Loan ID: " + loanObj.getInt("loanId")
								+ ", Identifier: " + loanObj.getString("identifier")
								+ ", Date Loan: " + loanObj.getString("dateLoan")
								+ ", Planned Date Back: " + loanObj.getString("plannedDateBack")
								+ ", Effective Date Back: " + (loanObj.isNull("effectiveDateBack") ? "N/A" : loanObj.getString("effectiveDateBack")));
						loansVBox.getChildren().add(loanLabel);
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Scene loansDialogScene = new Scene(loansVBox, 400, 300);
		loansDialog.setScene(loansDialogScene);
		loansDialog.show();
	}

	// Méthode pour afficher le formulaire d'ajout de nouveau client
	private void displayAddCustomerForm() {
		VBox addCustomerVBox = new VBox();
		addCustomerVBox.setAlignment(Pos.CENTER);
		addCustomerVBox.setSpacing(10);

		TextField firstNameField = new TextField();
		firstNameField.setPromptText("First Name");
		TextField lastNameField = new TextField();
		lastNameField.setPromptText("Last Name");
		TextField birthDateField = new TextField();
		birthDateField.setPromptText("Birth Date (yyyy-MM-dd)");
		Button addButton = new Button("Add Customer");

		addButton.setOnAction(e -> {
			Customer newCustomer = new Customer(
					firstNameField.getText(),
					lastNameField.getText(),
					parseDate(birthDateField.getText())
			);
			librarian.addToDatabaseCustomer(newCustomer);
			displayUsers(loadCustomers());
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Success");
			alert.setHeaderText(null);
			alert.setContentText("Customer added successfully.");
			alert.showAndWait();
		});

		addCustomerVBox.getChildren().addAll(new Label("Add New Customer"), new Label("First Name"), firstNameField, new Label("Last Name"), lastNameField, new Label("Birth Date (yyyy-MM-dd)"), birthDateField, addButton);
		borderPane.setTop(addCustomerVBox);
	}

	private Date parseDate(String dateStr) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
