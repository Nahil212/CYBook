package library;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibraryApplication extends Application{

	@Override
	public void start(Stage stage) throws Exception {
		
		Librarian l = new Librarian("l","l");		
		Book book = l.searchBookFromIdentifier("ark:/12148/cb45249689r");

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
		VBox bookInfo = new VBox();

		Image image = new Image("book.png");
		Label title = new Label(book.getTitle());
		Label author = new Label(book.getCreator());
		Label year = new Label(book.getYear()+"");
		Label publisher = new Label(book.getPublisher());
		Label format = new Label(book.getFormat());
		bookInfo.getChildren().addAll(title,author,year,publisher,format);
		borderPane.setCenter(bookInfo);
		
		stage.setScene(homePage);
		stage.show();
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

}
