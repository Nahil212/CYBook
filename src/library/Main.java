package library;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import org.json.XML;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        String inputUsername = "";
        String inputPassword = "";
        Scanner scanner = new Scanner(System.in);
        boolean isAuthenticated = false;
        String filePath = "/home/cytech/CYBook/data/LibraryData.json";

        System.out.println("1. Connect");
        System.out.println("2. Exit");
        System.out.println("Your choice : ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            while (!isAuthenticated) {
                System.out.println("Enter your username: ");
                inputUsername = scanner.nextLine();
                System.out.println("Enter your password: ");
                inputPassword = scanner.nextLine();
                try
                {
                    String content = new String(Files.readAllBytes(Paths.get(filePath)));
                    JSONObject jsonObject = new JSONObject(content);
                    JSONArray librarians = jsonObject.getJSONArray("librarians");
                    for (int i = 0; i < librarians.length(); i++)
                    {
                        JSONObject librarian = librarians.getJSONObject(i);
                        if (librarian.getString("pseudonym").equals(inputUsername) && librarian.getString("password").equals(inputPassword))
                        {
                            isAuthenticated = true;
                            break;
                        }
                    }
                    if (isAuthenticated)
                    {
                        System.out.println("Login successful!");
                    }
                    else
                    {
                        System.out.println("Invalid username or password. Please try again.");
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
            if(isAuthenticated){
                Librarian currentUser = new Librarian(inputUsername, inputPassword);
                currentUser.fetchCustomers();


                System.out.println("1. List of user");
                System.out.println("2. List of loan");
                System.out.println("3. Book");
                System.out.println("4. Exit");
                choice =scanner.nextInt();
                scanner.nextLine();
                if(choice == 1){
                    System.out.println(currentUser.printCustomers());
                    System.out.println("1. Add a new user");
                    System.out.println("2. Delete a user");
                    System.out.println("3. Exit");
                }
                if(choice == 2){
                    //afficher liste emprunts

                    System.out.println("1. Return a book");
                    System.out.println("2. Exit");
                }
                if(choice == 3){
                    System.out.println("1. To borrow a book");
                    System.out.println("2. Search a book");
                    System.out.println("3. Exit");
                }



            }

        }
    }
}


