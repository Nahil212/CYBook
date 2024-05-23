package library;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.time.LocalDate;

public class LibraryApplication extends Application{
	private int searchStart = 1;
	private int yearStart = -1;
	private int yearEnd = -1;
	private String title="";
	private String author ="";
	private Universe univ=Universe.NONE;
	private ArrayList<Book> searchedBooks = new ArrayList<Book>();
	private Stage loanCreation = new Stage();
	Librarian librarian = new Librarian("","");

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
		Stage loanCreation = new Stage();
	    
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
			}catch(NumberFormatException nEFE) {
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
		Label author = new Label(book.getCreator());
		bookInfo.getChildren().addAll(title,author);
		bookInfo.setStyle("-fx-background-color: blue; -fx-cursor: hand;");
		bookInfo.setOnMouseClicked(event -> {
		    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
		    	ImageView imgBook = new ImageView(new Image("file:img/book.png"));
		    	VBox loanVB = new VBox();
		    	HBox infoHB = new HBox();
		    	VBox infoVB = new VBox(
		    			new Label("Title: "+book.getTitle()),
		    			new Label("Author(s): "+book.getCreator()),
		    			new Label("Year: "+book.getYear()),
		    			new Label("Edition: "+book.getPublisher()),
		    			new Label("Format: "+book.getFormat()),
		    			new Label("Identifier: "+book.getIdentifier()));
		    	DatePicker datePicker = new DatePicker();
		    	datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
		            @Override
		            public DateCell call(DatePicker datePicker) {
		                return new DateCell() {
		                    @Override
		                    public void updateItem(LocalDate date, boolean empty) {
		                        super.updateItem(date, empty);
		                        if (date.isBefore(LocalDate.now())) {
		                            setDisable(true);
		                        }
		                    }
		                };
		            }
		        });
		    	infoHB.getChildren().addAll(imgBook,infoVB);
		    	ChoiceBox<Customer> customerBox = new ChoiceBox<>();
		    	customerBox.getItems().addAll(this.librarian.getCustomers());
		    	Button loanButton = new Button("Borrow");
		    	loanVB.getChildren().addAll(infoHB,datePicker,customerBox,loanButton);
		    	Scene loanInfoScene = new Scene(loanVB);
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
	
		
	public static void main(String[] args) {
		launch(args);
	}

}
