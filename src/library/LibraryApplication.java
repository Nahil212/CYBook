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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibraryApplication extends Application{

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
                JSONObject librarian = librarians.getJSONObject(i);
                if(librarian.getString("pseudonym").equals(pseudoField.getText()) && librarian.getString("password").equals(passwordField.getText())) {
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
		String author = "";
		int yearStart = -1;
		int yearEnd = -1;
		String searchTitle = "Dragon ball GT";
		int searchStart = 1;
		Universe univ = Universe.NONE;
		ArrayList<Book> searchedBooks = new ArrayList<Book>();
				
		// Top
		VBox filter = new VBox();
		filter.setAlignment(Pos.CENTER);
		filter.setSpacing(10);
		filter.setPrefHeight(125);
		
		Label idSearch = new Label("Search by identifier");
		HBox idFields = new HBox();
		idFields.setAlignment(Pos.TOP_CENTER);
		TextField isbn = new TextField();
		Button searchISBN = new Button("Search");
		isbn.setPromptText("ISBN");
		TextField issn = new TextField();
		Button searchISSN = new Button("Search");
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
		for(Book book: searchedBooks) {
			bookVBox.getChildren().add(createBookDisplayer(book));
		}
		scrollBook.setContent(bookVBox);
		
		
		// Bottom
		HBox changePage = new HBox();
		Button precedent = new Button("<<");
		Button next = new Button(">>");
		changePage.getChildren().addAll(precedent,next);
		changePage.setAlignment(Pos.CENTER);
		changePage.setPrefHeight(50);
		changePage.setSpacing(10);
		
		borderPane.setLeft(icons);
		borderPane.setTop(filter);
		borderPane.setCenter(scrollBook);
		borderPane.setBottom(changePage);
		stage.setScene(signInPage);
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
	
	public static void main(String[] args) {
		launch(args);
	}

}
