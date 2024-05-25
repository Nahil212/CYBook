package library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import javafx.stage.Modality;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Date;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

public class LibraryApplication extends Application{
    private int searchStart = 1;
    private int yearStart = -1;
    private int yearEnd = -1;
    private String title="";
    private String author ="";
    private Universe univ=Universe.NONE;
    private ArrayList<Book> searchedBooks = new ArrayList<Book>();
    private Stage loanCreation = new Stage();
    private Librarian librarian = new Librarian("","");
    private BorderPane borderPane;
    private BorderPane userPane;
    private BorderPane loanPane;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("CY-Book");
        Image logoshessh = new Image("file:img/book.png");
        stage.getIcons().add(logoshessh);
        borderPane = new BorderPane();
        userPane = new BorderPane();
        loanPane = new BorderPane();
        Scene homePage = new Scene(borderPane);
        Scene userPage = new Scene(userPane);
        Scene loanPage = new Scene(loanPane);
        String content = new String(Files.readAllBytes(Paths.get(Librarian.filePath)));
        JSONObject jsonObject = new JSONObject(content);
        JSONArray librarians = jsonObject.getJSONArray("librarians");

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
                    this.librarian.setPseudonym(lib.getString("pseudonym"));
                    this.librarian.setPassword(lib.getString("password"));
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
        filter.setStyle("-fx-padding: 20; -fx-border-style: solid inside; -fx-border-width: 2; -fx-border-insets: 5; -fx-border-radius: 5; -fx-border-color: gray;");

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
        Button popularSearch = new Button("Popular");
        filterFields.getChildren().addAll(popularSearch,titleField,authorField,yearStartField,yearEndField,universeBox,searchButtonFilter);
        filter.getChildren().addAll(idSearch,idFields,filterSearch,filterFields);

        
        // Left
        VBox icons = new VBox();
        icons.setAlignment(Pos.TOP_CENTER);
        icons.setPrefWidth(150);
        icons.setSpacing(10);
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e->{
        	stage.setScene(homePage);
        });
        Button loanButton = new Button("Loans");
        loanButton.setOnAction(e->{
        	this.displayloanCategories();
        	this.displayloans(this.librarian.getLoans());
        	stage.setScene(loanPage);
        });
        Button userButton = new Button("Users");
        icons.getChildren().addAll(searchButton,loanButton,userButton);
        userButton.setOnAction(e -> {
            displayUsers(librarian.getCustomers());
            displayAddCustomerForm();
            stage.setScene(userPage);
        });
        
        VBox icons2 = new VBox();
        icons2.setAlignment(Pos.TOP_CENTER);
        icons2.setPrefWidth(150);
        icons2.setSpacing(10);
        Button searchButton2 = new Button("Search");
        searchButton2.setOnAction(e->{
        	stage.setScene(homePage);
        });
        Button loanButton2 = new Button("Loans");
        loanButton2.setOnAction(e->{
        	this.displayloanCategories();
        	this.displayloans(this.librarian.getLoans());
        	stage.setScene(loanPage);
        });
        Button userButton2 = new Button("Users");
        icons2.getChildren().addAll(searchButton2,loanButton2,userButton2);
        userButton2.setOnAction(e -> {
            displayUsers(librarian.getCustomers());
            displayAddCustomerForm();
            stage.setScene(userPage);
        });
        
        VBox icons3 = new VBox();
        icons3.setAlignment(Pos.TOP_CENTER);
        icons3.setPrefWidth(150);
        icons3.setSpacing(10);
        Button searchButton3 = new Button("Search");
        searchButton3.setOnAction(e->{
        	stage.setScene(homePage);
        });
        Button loanButton3 = new Button("Loans");
        loanButton3.setOnAction(e->{
        	this.displayloanCategories();
        	this.displayloans(this.librarian.getLoans());
        	stage.setScene(loanPage);
        });
        Button userButton3 = new Button("Users");
        icons3.getChildren().addAll(searchButton3,loanButton3,userButton3);
        userButton3.setOnAction(e -> {
            List<Customer> customers = librarian.getCustomers();
            displayUsers(customers);
            displayAddCustomerForm();
            stage.setScene(userPage);
        });


        // Middle
        ScrollPane scrollBook = new ScrollPane();
        scrollBook.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        VBox bookVBox = new VBox();
        bookVBox.setStyle("-fx-padding: 10px;");
        bookVBox.setSpacing(10);
        bookVBox.setAlignment(Pos.TOP_CENTER);
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
        loanPane.setLeft(icons2);
        userPane.setLeft(icons3);
        borderPane.setTop(filter);
        borderPane.setCenter(scrollBook);
        borderPane.setBottom(changePage);

        // IMPLEMENTATION
        popularSearch.setOnAction(e->{
        	this.searchedBooks.clear();
        	try {
				this.searchedBooks = this.librarian.MostFamousLoan();
				this.visibleButton(precedent,next);
                this.displayBooksInVBox(bookVBox);
			} catch (MissingDataFileException e1) {
				this.displayError(bookVBox, "Error while extracting data");
			}
        });
        searchISBN.setOnAction(sI->{
            Stage newStage = new Stage();
            newStage.show();
            this.searchStart=1;
            searchedBooks.clear();
            try {
                Book book = librarian.searchBookFromISBN(Long.parseLong(isbn.getText().toString()));
                searchedBooks.add(book);
                this.visibleButton(precedent,next);
                displayBooksInVBox(bookVBox);
            }catch(BookNotInDataBaseException e) {
                displayError(bookVBox, "No result");
            }catch(NumberFormatException nEFE) {
                this.displayError(bookVBox, "Incorrect filter informations");
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
                displayError(bookVBox, "No result");
            }catch(NumberFormatException nEFE) {
                this.displayError(bookVBox, "Incorrect filter informations");
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
                displayError(bookVBox, "No result");
            }
        });
        searchButtonFilter.setOnAction(e->{
            try {
                this.searchStart=1;
                this.title = titleField.getText().toString();
                this.author = authorField.getText().toString();
                this.yearStart = -1;
                this.yearEnd = -1;
                if(!yearStartField.getText().equals("")) {
                    this.yearStart = Integer.parseInt(yearStartField.getText());
                }
                if(!yearEndField.getText().equals("")) {
                    this.yearEnd = Integer.parseInt(yearEndField.getText());
                }
                this.univ = universeBox.getSelectionModel().getSelectedItem();
                this.searchedBooks = librarian.searchBooks(author, yearStart, yearEnd, univ, title, this.searchStart);
                this.visibleButton(precedent,next);
                this.displayBooksInVBox(bookVBox);
            }catch(EmptyResearchException eRE) {
                this.displayError(bookVBox, "No result");
            }catch(NumberFormatException nFE) {
                this.displayError(bookVBox, "Incorrect filter informations");
            }
        });

        next.setOnAction(e->{
            this.searchStart+=20;
            try {
                this.searchedBooks = librarian.searchBooks(author, yearStart, yearEnd, univ, title, this.searchStart);
                this.visibleButton(precedent,next);
                this.displayBooksInVBox(bookVBox);
            }catch(EmptyResearchException eRE) {
                this.displayError(bookVBox, "No result");
            }
        });

        precedent.setOnAction(e->{
            try {
                this.searchStart-=20;
                this.searchedBooks = librarian.searchBooks(author, yearStart, yearEnd, univ, title, this.searchStart);
                this.visibleButton(precedent,next);
                this.displayBooksInVBox(bookVBox);
            }catch(EmptyResearchException eRE) {
                this.displayError(bookVBox, "No result");
            }
        });

        stage.setScene(homePage);
        stage.show();
    }

    private VBox createBookDisplayer(Book book) {
        VBox bookInfo = new VBox();
        Label title = new Label(book.getTitle());
        title.setStyle("-fx-font-size: 16px;");
        Label author = new Label(book.getCreator());
        author.setStyle("-fx-font-style: italic;");
        bookInfo.getChildren().addAll(title,author);
        bookInfo.setStyle("-fx-background-color: lightgray; -fx-cursor: hand; -fx-padding: 10 20 10 20;-fx-background-radius: 10;");
        bookInfo.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                ImageView imgBook = new ImageView(new Image("file:img/book.png"));
                VBox loanVB = new VBox();
                loanVB.setAlignment(Pos.TOP_CENTER);
                loanVB.setSpacing(10);
                HBox infoHB = new HBox();
                infoHB.setAlignment(Pos.CENTER);
                VBox infoVB = new VBox(
                        new Label("Title: "+book.getTitle()),
                        new Label("Author(s): "+book.getCreator()),
                        new Label("Year: "+book.getYear()),
                        new Label("Edition: "+book.getPublisher()),
                        new Label("Format: "+book.getFormat()),
                        new Label("Identifier: "+book.getIdentifier()));
                infoVB.setAlignment(Pos.CENTER_LEFT);
                infoVB.setStyle("-fx-font-size: 16px;");
                infoVB.setSpacing(10);
                infoHB.getChildren().addAll(imgBook,infoVB);
                ChoiceBox<Customer> customerBox = new ChoiceBox<>();
                customerBox.getItems().addAll(this.librarian.getCustomers());
                Button loanButton = new Button("Borrow");
                loanVB.getChildren().addAll(infoHB,customerBox,loanButton);
                Scene loanInfoScene = new Scene(loanVB);
                loanButton.setOnAction(e->{
                	if(customerBox.getSelectionModel().getSelectedItem()!=null) {
                		Customer choosen = customerBox.getValue();
                		if(!(choosen.canBorrow())) {
                			Alert alert = new Alert(Alert.AlertType.INFORMATION);
	                        alert.setTitle("Error");
	                        alert.setHeaderText(null);
	                        alert.setContentText("This customer can't borrow a book for now");
	                        alert.showAndWait();
                		}else if(librarian.isIdentifierOverBorrowed(book.getIdentifier())){
                			Alert alert = new Alert(Alert.AlertType.INFORMATION);
	                        alert.setTitle("Error");
	                        alert.setHeaderText(null);
	                        alert.setContentText("This book is out of stock for now");
	                        alert.showAndWait();
	                        this.loanCreation.close();
                		}else {
	                		Loan newLoan = new Loan(book.getIdentifier());
	                		this.librarian.addToDatabaseLoan(newLoan, choosen.getIdNumber());
	                		Alert alert = new Alert(Alert.AlertType.INFORMATION);
	                        alert.setTitle("Success");
	                        alert.setHeaderText(null);
	                        alert.setContentText("Loan has been registered");
	                        alert.showAndWait();
	                        this.loanCreation.close();
                		}
                	}
                	
                });
                this.loanCreation.setScene(loanInfoScene);
                this.loanCreation.show();
            }
        });
        return bookInfo;
    }

    private void displayBooksInVBox(VBox bookVbox) {
        bookVbox.getChildren().clear();
        for(Book book: this.searchedBooks) {
            bookVbox.getChildren().add(createBookDisplayer(book));
        }
    }

    private void displayError(VBox bookVbox, String message) {
        bookVbox.getChildren().clear();
        bookVbox.getChildren().add(new Label(message));
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

    // Méthode pour afficher les utilisateurs sous forme de boutons
    private void displayUsers(List<Customer> customers) {
        VBox userListVBox = new VBox();
        userListVBox.setAlignment(Pos.CENTER_LEFT);
        userListVBox.setSpacing(10);
        userListVBox.setPadding(new Insets(10));

        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setSpacing(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name");
        Button searchButton = new Button("Search");
        searchBox.getChildren().addAll(searchField, searchButton);

        for (Customer customer : customers) {
            GridPane customerGrid = new GridPane();
            customerGrid.setHgap(20);
            customerGrid.setVgap(10); 
            customerGrid.setAlignment(Pos.CENTER_LEFT);

            Label idLabel = new Label("" + customer.getIdNumber());
            Label firstNameLabel = new Label(customer.getFirstName());
            Label lastNameLabel = new Label(customer.getLastName());

            ColumnConstraints column1 = new ColumnConstraints();
            column1.setPercentWidth(30);
            ColumnConstraints column2 = new ColumnConstraints();
            column2.setPercentWidth(35);
            ColumnConstraints column3 = new ColumnConstraints();
            column3.setPercentWidth(35);

            customerGrid.getColumnConstraints().addAll(column1, column2, column3);

            customerGrid.add(idLabel, 0, 0);
            customerGrid.add(firstNameLabel, 1, 0);
            customerGrid.add(lastNameLabel, 2, 0);

            Button customerButton = new Button();
            customerButton.setMaxWidth(Double.MAX_VALUE);
            customerButton.setGraphic(customerGrid);
            customerButton.setStyle("-fx-background-color: lightgray; -fx-cursor: hand; -fx-padding: 10 20 10 20;");

            customerButton.setOnAction(e -> showCustomerOptions(customer));
            userListVBox.getChildren().add(customerButton);
        }

        ScrollPane scrollPane = new ScrollPane(userListVBox);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true); // S'assurer que le contenu s'adapte Ã la largeur

        VBox mainVBox = new VBox();
        mainVBox.setAlignment(Pos.CENTER);
        mainVBox.setSpacing(10);
        mainVBox.getChildren().addAll(searchBox, scrollPane);

        VBox.setVgrow(scrollPane, Priority.ALWAYS); // Permettre au ScrollPane de grandir
        BorderPane.setAlignment(mainVBox, Pos.CENTER);

        userPane.setCenter(mainVBox);

        searchButton.setOnAction(e -> {
            String searchText = searchField.getText().toLowerCase();
            List<Customer> filteredCustomers = new ArrayList<>();
            for (Customer customer : this.librarian.getCustomers()) {
                if (customer.getFirstName().toLowerCase().contains(searchText) ||
                        customer.getLastName().toLowerCase().contains(searchText)) {
                    filteredCustomers.add(customer);
                }
            }
            displayUsers(filteredCustomers);
            searchField.clear();
        });
    }

    // Méthode pour afficher les options de modification du client
    private void showCustomerOptions(Customer customer) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(borderPane.getScene().getWindow());

        VBox dialogVBox = new VBox();
        dialogVBox.setAlignment(Pos.CENTER);
        dialogVBox.setSpacing(10);

        Label messageLabel = new Label("Manage a customer");
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
        editDialog.initOwner(userPane.getScene().getWindow());

        VBox editVBox = new VBox();
        editVBox.setAlignment(Pos.CENTER);
        editVBox.setSpacing(10);

        TextField firstNameField = new TextField(customer.getFirstName());
        TextField lastNameField = new TextField(customer.getLastName());
        DatePicker datePicker = new DatePicker();
        Callback<DatePicker, DateCell> dayCellFactory = 
        	    new Callback<DatePicker, DateCell>() {
        	        @Override
        	        public DateCell call(final DatePicker datePicker) {
        	            return new DateCell() {
        	                @Override
        	                public void updateItem(LocalDate date, boolean empty) {
        	                    super.updateItem(date, empty);
        	                    if (date.isAfter(LocalDate.now())) {
        	                        setDisable(true);
        	                    }
        	                }
        	            };
        	        }
        	    };
        datePicker.setValue(customer.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        datePicker.setDayCellFactory(dayCellFactory);
        Button updateButton = new Button("Update");

        updateButton.setOnAction(e -> {
        	LocalDate localDate = datePicker.getValue();
        	datePicker.setDayCellFactory(dayCellFactory);
        	Date date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        	SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
            boolean success = librarian.updateCustomer(
                    customer.getIdNumber(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    formatDate.format(date)
            );
            if (success) {
                editDialog.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Customer updated successfully.");
                alert.showAndWait();
                librarian.fetchCustomers();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update customer.");
                alert.showAndWait();
            }
        });

        editVBox.getChildren().addAll(new Label("First Name"), firstNameField, new Label("Last Name"), lastNameField, new Label("Birth Date"), datePicker, updateButton);

        Scene editDialogScene = new Scene(editVBox, 300, 400);
        editDialog.setScene(editDialogScene);
        editDialog.show();
    }

    // Méthode pour afficher les emprunts d'un client depuis le fichier JSON
    private void showCustomerLoans(Customer customer) {
        Stage loansDialog = new Stage();
        loansDialog.initModality(Modality.APPLICATION_MODAL);
        loansDialog.initOwner(userPane.getScene().getWindow());

        VBox loansVBox = new VBox();
        loansVBox.setAlignment(Pos.CENTER_LEFT);
        loansVBox.setSpacing(10);
        loansVBox.setPadding(new Insets(10));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Label titleLabel = new Label("Customer's Loans");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        try {
	        loansVBox.getChildren().add(titleLabel);
	        for(Loan loanedBook: customer.getLoans()) {
	        	VBox loanGrid = new VBox();
		        loanGrid.setAlignment(Pos.CENTER_LEFT);
		        loanGrid.setMaxWidth(Double.MAX_VALUE);
		        Book book = this.librarian.searchBookFromIdentifier(loanedBook.getIdentifier());
		        Label bookLabel = new Label(book.getTitle());
		        Label dateLoanLabel = new Label("Date Loan: "+sdf.format(loanedBook.getDateLoan()));
		        Label plannedDateBackLabel = new Label("Planned Date Back: "+sdf.format(loanedBook.getPlannedDateBack()));
		        Label effectiveDateBackLabel;
		        if(loanedBook.getEffectiveDateBack()!=null) {
		        	effectiveDateBackLabel = new Label("Returne Date: "+sdf.format(loanedBook.getEffectiveDateBack())); 
		        }else {
		        	effectiveDateBackLabel = new Label("Returne Date: N/A"); 
		        }
		        Label returned = new Label("Returned ?: "+loanedBook.getReturned());
		        Label late = new Label("Late ?: "+loanedBook.getLate());
		        loanGrid.getChildren().addAll(bookLabel,dateLoanLabel,plannedDateBackLabel,effectiveDateBackLabel,returned,late);
		        if(!(loanedBook.getReturned())) {
			        loanGrid.setOnMouseClicked(event -> {
			        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
				        Stage returnStage = new Stage();
				        VBox returnVBox = new VBox();
				        HBox returnHBox = new HBox();
				        Scene returnScene = new Scene(returnVBox);
				        Button yes = new Button("Yes");
				        yes.setOnAction(e->{
				        	this.librarian.markBack(loanedBook);
				        	Alert alert = new Alert(Alert.AlertType.INFORMATION);
				            alert.setTitle("Success");
				            alert.setHeaderText(null);
				            alert.setContentText("The book has been returned");
				            alert.showAndWait();
				            returnStage.close();
				        });
				        Button no = new Button("No");
				        no.setOnAction(e->{
				        	returnStage.close();
				        });
				        returnHBox.getChildren().addAll(yes,no);
				        returnVBox.getChildren().addAll(new Label("Do you want to return this book ?"),returnHBox);
				        returnStage.setScene(returnScene);
				        returnStage.show();
				        }
			        });
		        }
		        Pane loanPane = new Pane();
		        loanPane.getChildren().add(loanGrid);
		        loanPane.setStyle("-fx-background-color: lightgray; -fx-padding: 10 20 10 20; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor:hand");
		        loanPane.setMaxWidth(Double.MAX_VALUE);
		        loansVBox.getChildren().add(loanPane);
	        }	        
	        ScrollPane scrollPane = new ScrollPane(loansVBox);
	        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
	        scrollPane.setFitToWidth(true);
	
	        VBox mainVBox = new VBox();
	        mainVBox.setAlignment(Pos.CENTER);
	        mainVBox.setSpacing(10);
	        mainVBox.getChildren().add(scrollPane);
	
	        Scene loansDialogScene = new Scene(mainVBox, 600, 400);
	        loansDialog.setScene(loansDialogScene);
	        loansDialog.show();
        }catch(Exception e) {
        	e.printStackTrace();
        }
    }

    // Méthode pour afficher le formulaire d'ajout de nouveau client
    private void displayAddCustomerForm() {
        VBox addCustomerVBox = new VBox();
        addCustomerVBox.setAlignment(Pos.CENTER);
        addCustomerVBox.setSpacing(20);
        addCustomerVBox.setStyle("-fx-padding: 20; -fx-border-style: solid inside; -fx-border-width: 2; -fx-border-insets: 5; -fx-border-radius: 5; -fx-border-color: gray;");

        Label titleLabel = new Label("Add New Customer");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox fieldsHBox = new HBox();
        fieldsHBox.setAlignment(Pos.CENTER);
        fieldsHBox.setSpacing(10);

        Label firstNameLabel = new Label("First Name");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        firstNameField.setMaxWidth(150);

        Label lastNameLabel = new Label("Last Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        lastNameField.setMaxWidth(150);

        DatePicker datePicker = new DatePicker();
        fieldsHBox.getChildren().addAll(firstNameLabel, firstNameField, lastNameLabel, lastNameField, new Label("Birth date"),datePicker);

        Button addButton = new Button("Add Customer");

        addButton.setOnAction(e -> {
            Customer newCustomer;
			try {
				LocalDate localDate = datePicker.getValue();
		        Date date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
				newCustomer = new Customer(firstNameField.getText(),lastNameField.getText(),date);
				librarian.addToDatabaseCustomer(newCustomer);
	            displayUsers(this.librarian.getCustomers());
	            Alert alert = new Alert(Alert.AlertType.INFORMATION);
	            alert.setTitle("Success");
	            alert.setHeaderText(null);
	            alert.setContentText("Customer added successfully.");
	            alert.showAndWait();
			} catch (JSONException | IOException | ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });

        addCustomerVBox.getChildren().addAll(titleLabel, fieldsHBox, addButton);

        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(20);
        container.getChildren().add(addCustomerVBox);

        userPane.setTop(container);
    }

    private void displayloanCategories() {
        VBox categoriesVBox = new VBox();
        categoriesVBox.setAlignment(Pos.CENTER);
        categoriesVBox.setSpacing(20);
        categoriesVBox.setStyle("-fx-padding: 20; -fx-border-style: solid inside; -fx-border-width: 2; -fx-border-insets: 5; -fx-border-radius: 5; -fx-border-color: gray;");

        Label titleLabel = new Label("Loan Categories");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox fieldsHBox = new HBox();
        fieldsHBox.setAlignment(Pos.CENTER);
        fieldsHBox.setSpacing(10);

        Button notReturnedButton = new Button("Loans Not Returned");
        notReturnedButton.setPrefWidth(150);
        Button returnedButton = new Button("Loans Returned");
        returnedButton.setPrefWidth(150);
        Button lateButton = new Button("Loans Late");
        lateButton.setPrefWidth(150);

        notReturnedButton.setOnAction(e -> displayloans(getLoansByCategory("notReturned")));
        returnedButton.setOnAction(e -> displayloans(getLoansByCategory("returned")));
        lateButton.setOnAction(e -> displayloans(getLoansByCategory("late")));

        fieldsHBox.getChildren().addAll(notReturnedButton, returnedButton, lateButton);

        categoriesVBox.getChildren().addAll(titleLabel, fieldsHBox);

        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(20);
        container.getChildren().add(categoriesVBox);

        loanPane.setTop(container);
    }

    private List<Loan> getLoansByCategory(String category) {
    	this.librarian.fetchLoans();
        List<Loan> loans = librarian.getLoans();
        List<Loan> filteredLoans = new ArrayList<>();

        for (Loan loan : loans) {
            switch (category) {
                case "notReturned":
                    if (!loan.getReturned()) {
                        filteredLoans.add(loan);
                    }
                    break;
                case "returned":
                    if (loan.getReturned()) {
                        filteredLoans.add(loan);
                    }
                    break;
                case "late":
                    if (loan.getLate()) {
                        filteredLoans.add(loan);
                    }
                    break;
            }
        }
        return filteredLoans;
    }

    private void displayloans(List<Loan> loans) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);
        
        VBox loanVbox = new VBox();
        loanVbox.setSpacing(10);
        loanVbox.setStyle("-fx-padding: 10 10 10 10;");
        scrollPane.setContent(loanVbox);
        try {
	        for (Loan loanedBook : loans) {
	        	VBox loanGrid = new VBox();
	        	loanGrid.setStyle("-fx-padding: 10 10 10 10; -fx-background-color: lightgray; -fx-cursor: hand;");
		        loanGrid.setAlignment(Pos.CENTER_LEFT);
		        loanGrid.setMaxWidth(Double.MAX_VALUE);
		        Book book = this.librarian.searchBookFromIdentifier(loanedBook.getIdentifier());
		        Label bookLabel = new Label(book.getTitle());
		        Label dateLoanLabel = new Label("Date Loan: "+sdf.format(loanedBook.getDateLoan()));
		        Label plannedDateBackLabel = new Label("Planned Date Back: "+sdf.format(loanedBook.getPlannedDateBack()));
		        Label effectiveDateBackLabel;
		        if(loanedBook.getEffectiveDateBack()!=null) {
		        	effectiveDateBackLabel = new Label("Returne Date: "+sdf.format(loanedBook.getEffectiveDateBack())); 
		        }else {
		        	effectiveDateBackLabel = new Label("Returne Date: N/A"); 
		        }
		        Label returned = new Label("Returned ?: "+loanedBook.getReturned());
		        Label late = new Label("Late ?: "+loanedBook.getLate());
		        loanGrid.getChildren().addAll(bookLabel,dateLoanLabel,plannedDateBackLabel,effectiveDateBackLabel,returned,late);
		        if(!(loanedBook.getReturned())) {
			        loanGrid.setOnMouseClicked(event -> {
			        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
				        Stage returnStage = new Stage();
				        VBox returnVBox = new VBox();
				        HBox returnHBox = new HBox();
				        Scene returnScene = new Scene(returnVBox);
				        Button yes = new Button("Yes");
				        yes.setOnAction(e->{
				        	this.librarian.markBack(loanedBook);
				        	this.displayloans(this.librarian.getLoans());
				        	Alert alert = new Alert(Alert.AlertType.INFORMATION);
				            alert.setTitle("Success");
				            alert.setHeaderText(null);
				            alert.setContentText("The book has been returned");
				            alert.showAndWait();
				            returnStage.close();
				        });
				        Button no = new Button("No");
				        no.setOnAction(e->{
				        	returnStage.close();
				        });
				        returnHBox.getChildren().addAll(yes,no);
				        returnVBox.getChildren().addAll(new Label("Do you want to return this book ?"),returnHBox);
				        returnStage.setScene(returnScene);
				        returnStage.show();
				        }
			        });
		        }
		        loanVbox.getChildren().add(loanGrid);
	        }
        }catch(BookNotInDataBaseException be) {
        	System.out.println("Missing data file");
        }
        loanPane.setCenter(scrollPane);
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    public static void main(String[] args) {
        launch(args);
    }

}
