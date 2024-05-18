package library;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String inputUsername = "";
        String inputPassword = "";
        Scanner scanner = new Scanner(System.in);
        boolean isAuthenticated = false;
        String filePath = "/home/cytech/CYBookult3/data/LibraryData.json";

        System.out.println("1. Connect");
        System.out.println("2. Exit");
        System.out.print("Your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            while (!isAuthenticated) {
                System.out.print("Enter your username: ");
                inputUsername = scanner.nextLine();
                System.out.print("Enter your password: ");
                inputPassword = scanner.nextLine();
                try {
                    String content = new String(Files.readAllBytes(Paths.get(filePath)));
                    JSONObject jsonObject = new JSONObject(content);
                    JSONArray librarians = jsonObject.getJSONArray("librarians");
                    for (int i = 0; i < librarians.length(); i++) {
                        JSONObject librarian = librarians.getJSONObject(i);
                        if (librarian.getString("pseudonym").equals(inputUsername) && librarian.getString("password").equals(inputPassword)) {
                            isAuthenticated = true;
                            break;
                        }
                    }
                    if (isAuthenticated) {
                        System.out.println("Login successful!");
                    } else {
                        System.out.println("Invalid username or password. Please try again.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (isAuthenticated) {
                Librarian currentUser = new Librarian(inputUsername, inputPassword, filePath);
                currentUser.fetchCustomers();
                currentUser.fetchLoans();

                while (true) {
                    System.out.println("1. List of users");
                    System.out.println("2. List of loans");
                    System.out.println("3. Book");
                    System.out.println("4. Exit");
                    System.out.print("Your choice: ");
                    choice = scanner.nextInt();
                    scanner.nextLine();

                    if (choice == 1) {
                        System.out.println(currentUser.printCustomers());
                        System.out.println("1. Add a new user");
                        System.out.println("2. Delete a user");
                        System.out.println("3. Exit");
                        choice = scanner.nextInt();
                        scanner.nextLine();

                        if (choice == 1) {
                            System.out.println("Enter the firstName :");
                            String firstName = scanner.nextLine();
                            System.out.println("Enter the lastName : ");
                            String lastName = scanner.nextLine();
                            System.out.println("Enter the birthDate (yyyy-MM-dd) : ");
                            String birthDateStr = scanner.nextLine();

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date birthDate = null;
                            try {
                                birthDate = dateFormat.parse(birthDateStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Customer newCustomer = new Customer(firstName, lastName, birthDate);
                            currentUser.addToDatabaseCustomer(newCustomer);
                        } else if (choice == 2) {
                            // Code pour supprimer un utilisateur
                        }
                    } else if (choice == 2) {
                        System.out.println(currentUser.printLoans());
                        System.out.println("1. Return a book");
                        System.out.println("2. Exit");
                        choice = scanner.nextInt();
                        scanner.nextLine();

                        if (choice == 1) {
                            // Code pour retourner un livre
                        }
                    } else if (choice == 3) {
                        // Code pour gérer les livres
                        System.out.println("1. Borrow a book");
                        System.out.println("2. Search a book");
                        System.out.println("3. Exit");
                    } else if (choice == 4) {
                        break;
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
        }
    }
}

