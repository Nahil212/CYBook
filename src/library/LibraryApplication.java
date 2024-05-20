package library;


import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibraryApplication extends Application{

	@Override
	public void start(Stage stage) throws Exception {
		
		Librarian l = new Librarian("l","l");
		String author = "";
		int yearStart = -1;
		int yearEnd = -1;
		String searchTitle = "Dragon ball GT";
		int searchStart = 1;
		
		ArrayList<Book> searchedBooks = l.searchBooks(author, yearStart, yearEnd, Universe.NONE, searchTitle, searchStart);

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
				
		signInVBox.getChildren().add(pseudonym);
		signInVBox.getChildren().add(pseudoField);
		signInVBox.getChildren().add(password);
		signInVBox.getChildren().add(passwordField);
		signInVBox.getChildren().add(signInButton);
		
		
		// HOME PAGE
		BorderPane borderPane = new BorderPane();
		Scene homePage = new Scene(borderPane);
		
		// Top
		VBox filter = new VBox();
		filter.setAlignment(Pos.TOP_CENTER);
		
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
		ChoiceBox<Universe> universeBox = new ChoiceBox<>();
		universeBox.getItems().addAll(Universe.values());
		
		filter.getChildren().addAll(idSearch,idFields,filterSearch,universeBox);
		
		
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
		
		borderPane.setTop(filter);
		borderPane.setCenter(scrollBook);
		borderPane.setBottom(changePage);
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
	
	public static void main(String[] args) {
		launch(args);
	}

}
