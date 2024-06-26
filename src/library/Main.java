package library;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * The main class for the library application. It handles user authentication, menu navigation, and interaction with the librarian's functionalities.     (terminal's version)
 */
public class Main {
	/**
	 * The main method simulating the use of the librarian software in the operator terminal
	 * @param args
	 * @throws BookNotInDataBaseException	no book correspond to the wanted filters or identifier
	 * @throws URISyntaxException	the request to the api is not correctly make
	 * @throws IOException	the entered informations are incorrect
	 * @throws InterruptedException		thread interrupted
	 */
    public static void main(String[] args) throws BookNotInDataBaseException, URISyntaxException, IOException, InterruptedException {
        String inputUsername = "";
        String inputPassword = "";
        Scanner scanner = new Scanner(System.in);
        boolean isAuthenticated = false;
        String filePath = "data/LibraryData.json";

        while (true) {
            try {
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
                            System.out.println("Error reading the data file. Please check the file path and try again.");
                        }
                    }

                    if (isAuthenticated) {
                        Librarian currentUser = new Librarian(inputUsername, inputPassword);

                        while (true) {
                            try {
                                System.out.println("1. List of users");
                                System.out.println("2. List of loans");
                                System.out.println("3. Book");
                                System.out.println("4. List of most famous loans");
                                System.out.println("5. Return to main menu");
                                System.out.print("Your choice: ");
                                choice = scanner.nextInt();
                                scanner.nextLine();

                                if (choice == 1) {
                                    System.out.println(currentUser.printCustomers());
                                    System.out.println("1. Add a new user");
                                    System.out.println("2. Update a user");
                                    System.out.println("3. Exit to main menu");
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
                                            Customer newCustomer = new Customer(firstName, lastName, birthDate);
                                            currentUser.addToDatabaseCustomer(newCustomer);
                                            System.out.println("User added successfully!");
                                        } catch (ParseException e) {
                                            System.out.println("Invalid birth date format. Please try again.");
                                        } catch (JSONException | IOException e) {
                                            System.out.println("Error adding user to database. Please try again.");
                                        }

                                    } else if (choice == 2) {
                                        System.out.print("Enter the ID of the user to update: ");
                                        int customerId = scanner.nextInt();
                                        scanner.nextLine();
                                        System.out.print("Enter the new firstName: ");
                                        String newFirstName = scanner.nextLine();
                                        System.out.print("Enter the new lastName: ");
                                        String newLastName = scanner.nextLine();
                                        System.out.print("Enter the new birthDate (yyyy-MM-dd): ");
                                        String newBirthDate = scanner.nextLine();

                                        boolean result = currentUser.updateCustomer(customerId, newFirstName, newLastName, newBirthDate);
                                        System.out.println(currentUser.getCustomers());
                                        if (result) {
                                            System.out.println("User updated successfully!");
                                        } else {
                                            System.out.println("User ID not found. Please try again.");
                                        }
                                    } else if (choice == 3) {
                                        continue;
                                    }
                                } else if (choice == 2) {
                                    System.out.println(currentUser.printLoans());
                                    System.out.println("1. Return a book");
                                    System.out.println("2. Exit to main menu");
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
                                    } else if (choice == 2) {
                                        continue;
                                    }
                                } else if (choice == 3) {
                                    System.out.println("1. Borrow a book");
                                    System.out.println("2. Search a book");
                                    System.out.println("3. Exit to main menu");
                                    choice = scanner.nextInt();
                                    scanner.nextLine();

                                    if (choice == 1) {
                                    	try {
	                                        System.out.print("Enter the ark of the book: ");
	                                        String identifier = scanner.nextLine();
	
	                                        System.out.print("Enter the customer ID: ");
	                                        int customerId = scanner.nextInt();
	                                        scanner.nextLine();
	                                        Book book = currentUser.searchBookFromIdentifier(identifier);
	                                        if (currentUser.isIdentifierOverBorrowed(identifier)) {
	                                            System.out.println("This book "+book.getTitle()+" has been borrowed too many times.");
	                                        } else {
	                                        	Loan temp = new Loan(identifier);
	                                            Loan newLoan = new Loan(Loan.nextId.getAndIncrement(),identifier, new Date(), temp.calculateScheduledReturnDate(), null, false, false, customerId);
	                                            currentUser.addToDatabaseLoan(newLoan, customerId);
	                                            System.out.println("Book borrowed successfully!");
	                                        }
                                    	}catch(BookNotInDataBaseException e) {
                                    		System.out.println("This ark doesn't correspond to any book");
                                    	}
                                    } else if (choice == 2) {
                                        System.out.println("1. Search by ark");
                                        System.out.println("2. Search by filters");
                                        System.out.print("Your choice: ");
                                        int searchChoice = scanner.nextInt();
                                        scanner.nextLine();

                                        if (searchChoice == 1) {
                                            System.out.print("Enter the ARK: ");
                                            String ark = scanner.nextLine();
                                            try {
                                                Book book = currentUser.searchBookFromIdentifier(ark);
                                                System.out.println(book);
                                            } catch (BookNotInDataBaseException e) {
                                                System.out.println("Book not found in database.");
                                            }
                                        } else if (searchChoice == 2) {
                                            String creator = "";
                                            Universe univ = Universe.NONE;
                                            int yearStart = -1;
                                            int yearEnd = -1;
                                            String searchTitle = "";
                                            int startResearch = 20;

                                            System.out.print("Enter author (or 'null' to skip): ");
                                            String authorsInput = scanner.nextLine();
                                            if (!authorsInput.equalsIgnoreCase("null")) {
                                                creator = authorsInput;
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
                                                try {
                                                    univ = Universe.valueOf(universesInput.toUpperCase());
                                                } catch (IllegalArgumentException e) {
                                                    System.out.println("Invalid universe. Please enter a valid universe.");
                                                    continue;
                                                }
                                            }

                                            System.out.print("Enter book title (or 'null' to skip): ");
                                            searchTitle = scanner.nextLine();
                                            if (searchTitle.equalsIgnoreCase("null")) {
                                                searchTitle = "";
                                            }

                                            try {
                                                ArrayList<Book> searchedBooks = currentUser.searchBooks(
                                                        creator, yearStart, yearEnd, univ, searchTitle, startResearch
                                                );
                                                for (Book book : searchedBooks) {
                                                    System.out.println(book);
                                                }
                                            } catch (EmptyResearchException e) {
                                                System.out.println("No books found matching the search criteria.");
                                            }
                                        } else if (choice == 3) {
                                            continue;
                                        }
                                    }
                                } else if (choice == 4) {
                                    try {
                                        ArrayList<Book> mostFamousBooksWithCount = currentUser.MostFamousLoan();

                                        System.out.println("Most Famous loans:");
                                        for (Book book : mostFamousBooksWithCount) {
                                            System.out.println(book);
                                            System.out.println("\n");
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Error fetching the most famous loans.");
                                    }
                                } else if (choice == 5) {
                                    break;
                                } else {
                                    System.out.println("Invalid choice. Please try again.");
                                }
                            } catch (Exception e) {
                                System.out.println("An error occurred. Please try again.");
                                scanner.nextLine();
                            }
                        }
                    }
                } else if (choice == 2) {
                    System.out.println("Exiting the application. Goodbye!");
                    break;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred. Please try again.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
}
