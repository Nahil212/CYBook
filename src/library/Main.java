package library;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String inputUsername = "";
        String inputPassword = "";
        Scanner scanner = new Scanner(System.in);
        boolean isAuthenticated = false;
        String filePath = "data/LibraryData.json";

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
                Librarian currentUser = new Librarian(inputUsername, inputPassword);
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
                        System.out.println("2. Exit");
                        choice = scanner.nextInt();
                        scanner.nextLine();

                        if (choice == 1) {
                            System.out.print("Enter the firstName: ");
                            String firstName = scanner.nextLine();
                            System.out.print("Enter the lastName: ");
                            String lastName = scanner.nextLine();
                            System.out.print("Enter the birthDate (yyyy-MM-dd): ");
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

                        }
                    } else if (choice == 2) {
                        System.out.println(currentUser.printLoans());
                        System.out.println("1. Return a book");
                        System.out.println("2. Exit");
                        choice = scanner.nextInt();
                        scanner.nextLine();


                            if (choice == 1) {
                                System.out.print("Enter the loan ID of the book to return: ");
                                int loanId = scanner.nextInt();
                                scanner.nextLine();

                                
                                Loan loanToReturn = null;
                                for (Loan loan : currentUser.getLoans()) {
                                    if (loan.getId() == loanId) {
                                        loanToReturn = loan;
                                        break;
                                    }
                                }

                                if (loanToReturn != null) {
                                    
                                    currentUser.markBack(loanToReturn);
                                    System.out.println("Book returned successfully!");
                                } else {
                                    System.out.println("Loan ID not found. Please enter a valid loan ID.");
                                }
                            }

                    } else if (choice == 3) {
                        System.out.println("1. Borrow a book");
                        System.out.println("2. Search a book");
                        System.out.println("3. Exit");
                        choice = scanner.nextInt();
                        scanner.nextLine();

                        if (choice == 1) {
                            System.out.print("Enter the book identifier: ");
                            String identifier = scanner.nextLine();

                            System.out.print("Enter the customer ID: ");
                            int customerId = scanner.nextInt();
                            scanner.nextLine();

                            Loan newLoan = new Loan(identifier);
                            currentUser.addToDatabaseLoan(newLoan, customerId);
                            System.out.println("Book borrowed is a success yahouuu!");
                        } else if (choice == 2) {
                            System.out.println("1. Search by ISBN");
                            System.out.println("2. Search by filters");
                            System.out.print("Your choice: ");
                            int searchChoice = scanner.nextInt();
                            scanner.nextLine();

                            if (searchChoice == 1) {
                                System.out.print("Enter the ISBN: ");
                                long isbn = scanner.nextLong();
                                scanner.nextLine();

                                try {
                                    Book book = currentUser.searchBookFromISBN(isbn);
                                    System.out.println(book);
                                } catch (IOException | InterruptedException |
                                         BookNotInDataBaseException | URISyntaxException e) {
                                    System.out.println("Error: " + e.getClass());
                                }
                            } else if (searchChoice == 2) {
                                ArrayList<String> listCreator = new ArrayList<>();
                                ArrayList<Universe> listUniverse = new ArrayList<>();
                                int yearStart = -1;
                                int yearEnd = -1;
                                String searchTitle = "";
                                int startResearch = 20; 

                                System.out.print("Enter authors (comma separated, or 'null' to skip): ");
                                String authorsInput = scanner.nextLine();
                                if (!authorsInput.equalsIgnoreCase("null")) {
                                    String[] authorsArray = authorsInput.split(",");
                                    for (String author : authorsArray) {
                                        listCreator.add(author.trim());
                                    }
                                }

                                System.out.print("Enter start year ('null' to skip): ");
                                String yearStartInput = scanner.nextLine();
                                if (!yearStartInput.equalsIgnoreCase("null")) {
                                    yearStart = Integer.parseInt(yearStartInput);
                                }

                                System.out.print("Enter end year ('null' to skip): ");
                                String yearEndInput = scanner.nextLine();
                                if (!yearEndInput.equalsIgnoreCase("null")) {
                                    yearEnd = Integer.parseInt(yearEndInput);
                                }

                                System.out.print("Enter universes (comma separated, options: MUSIC, YOUTH, IMAGEANDMAP, or 'null' to skip): ");
                                String universesInput = scanner.nextLine();
                                if (!universesInput.equalsIgnoreCase("null")) {
                                    String[] universesArray = universesInput.split(",");
                                    for (String universe : universesArray) {
                                        listUniverse.add(Universe.valueOf(universe.trim().toUpperCase()));
                                    }
                                }

                                System.out.print("Enter book title (or 'null' to skip): ");
                                searchTitle = scanner.nextLine();
                                if (searchTitle.equalsIgnoreCase("null")) {
                                    searchTitle = "";
                                }

                                try {
                                    ArrayList<Book> searchedBooks = currentUser.searchBooks(
                                            listCreator, yearStart, yearEnd, listUniverse, searchTitle, startResearch
                                    );
                                    for (Book book : searchedBooks) {
                                        System.out.println(book);
                                    }
                                } catch (IOException | InterruptedException |
                                         URISyntaxException | EmptyResearchException e) {
                                    System.out.println("Error: " + e.getClass());
                                }
                            } else if (choice == 3) {

                            }
                        }
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
