package biblioConnect_v3;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class LibraryUI implements AutoCloseable {
    private LibraryManagementSystem system;
    private Scanner scanner;
    private User currentUser;

    public LibraryUI(LibraryManagementSystem system) {
        this.system = system;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            try {
                if (currentUser == null) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
                System.out.println("Restarting the application...");
            }
        }
    }

    private void showLoginMenu() {
        while (true) {
            System.out.println("\n1. Login");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        login();
                        return;
                    case 2:
                        System.out.println("Goodbye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (system.getUserManagementService().authenticateUser(username, password)) {
            currentUser = system.getUserManagementService().getUser(username);
            System.out.println("Login successful. Welcome, " + currentUser.getName() + "!");
        } else {
            System.out.println("Login failed. Invalid username or password.");
        }
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("\n1. Search for a book");
            System.out.println("2. Borrow a book");
            System.out.println("3. Return a book");
            System.out.println("4. View borrowing history");
            System.out.println("5. Reserve a book");
            System.out.println("6. View my reservations");
            System.out.println("7. List all books");
            System.out.println("8. Update my profile");
            System.out.println("9. Logout");
            if (currentUser.getRole() == UserRole.LIBRARIAN) {
                System.out.println("10. Add a book");
                System.out.println("11. Remove a book");
                System.out.println("12. View overdue books");
                System.out.println("13. Register new user");
                System.out.println("14. List all users");
                System.out.println("15. Remove user");
            }
            System.out.print("Choose an option: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1: searchBook(); break;
                    case 2: borrowBook(); break;
                    case 3: returnBook(); break;
                    case 4: viewBorrowingHistory(); break;
                    case 5: reserveBook(); break;
                    case 6: viewMyReservations(); break;
                    case 7: listAllBooks(); break;
                    case 8: updateProfile(); break;
                    case 9: logout(); return;
                    case 10: 
                        if (currentUser.getRole() == UserRole.LIBRARIAN) addBook();
                        else System.out.println("Invalid option.");
                        break;
                    case 11:
                        if (currentUser.getRole() == UserRole.LIBRARIAN) removeBook();
                        else System.out.println("Invalid option.");
                        break;
                    case 12:
                        if (currentUser.getRole() == UserRole.LIBRARIAN) viewOverdueBooks();
                        else System.out.println("Invalid option.");
                        break;
                    case 13:
                        if (currentUser.getRole() == UserRole.LIBRARIAN) registerNewUser();
                        else System.out.println("Invalid option.");
                        break;
                    case 14:
                        if (currentUser.getRole() == UserRole.LIBRARIAN) listAllUsers();
                        else System.out.println("Invalid option.");
                        break;
                    case 15:
                        if (currentUser.getRole() == UserRole.LIBRARIAN) removeUser();
                        else System.out.println("Invalid option.");
                        break;
                    default: System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void searchBook() {
        System.out.print("Enter search query: ");
        String query = scanner.nextLine();
        List<Book> results = system.getLibraryManagementService().searchBooks(query);
        if (results.isEmpty()) {
            System.out.println("No books found.");
        } else {
            for (Book book : results) {
                System.out.println(book.getTitle() + " by " + book.getAuthor() + " (ID: " + book.getBookId() + ")");
            }
        }
    }

    private void borrowBook() {
        System.out.print("Enter book ID to borrow: ");
        String bookId = scanner.nextLine();
        try {
            system.getLibraryManagementService().borrowBook(currentUser.getUserId(), bookId);
            System.out.println("Book borrowed successfully.");
        } catch (Exception e) {
            System.out.println("Error borrowing book: " + e.getMessage());
        }
    }

    private void returnBook() {
        System.out.print("Enter book ID to return: ");
        String bookId = scanner.nextLine();
        try {
            system.getLibraryManagementService().returnBook(currentUser.getUserId(), bookId);
            System.out.println("Book returned successfully.");
        } catch (Exception e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }

    private void viewBorrowingHistory() {
        List<BorrowingRecord> history = system.getLibraryManagementService().getBorrowingHistory(currentUser.getUserId());
        if (history.isEmpty()) {
            System.out.println("No borrowing history.");
        } else {
            for (BorrowingRecord record : history) {
                System.out.println("Book ID: " + record.getBookId() + 
                                   ", Borrowed: " + record.getBorrowDate() + 
                                   ", Returned: " + (record.getReturnDate() != null ? record.getReturnDate() : "Not returned yet"));
            }
        }
    }

    private void reserveBook() {
        System.out.print("Enter book ID to reserve: ");
        String bookId = scanner.nextLine();
        try {
            system.getLibraryManagementService().reserveBook(currentUser.getUserId(), bookId);
            System.out.println("Book reserved successfully.");
            LocalDateTime expectedReturnDate = system.getLibraryManagementService().getExpectedReturnDate(bookId);
            if (expectedReturnDate != null) {
                System.out.println("Expected return date: " + expectedReturnDate);
            }
        } catch (Exception e) {
            System.out.println("Error reserving book: " + e.getMessage());
        }
    }

    private void viewMyReservations() {
        List<BorrowingRecord> reservations = system.getLibraryManagementService().getUserReservations(currentUser.getUserId());
        if (reservations.isEmpty()) {
            System.out.println("You have no active reservations.");
        } else {
            for (BorrowingRecord reservation : reservations) {
                System.out.println("Book ID: " + reservation.getBookId() + 
                                   ", Reserved on: " + reservation.getBorrowDate());
                LocalDateTime expectedReturnDate = system.getLibraryManagementService().getExpectedReturnDate(reservation.getBookId());
                if (expectedReturnDate != null) {
                    System.out.println("Expected return date: " + expectedReturnDate);
                }
            }
        }
    }

    private void listAllBooks() {
        List<Book> books = system.getLibraryManagementService().listAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
        } else {
            for (Book book : books) {
                System.out.println(book.getTitle() + " by " + book.getAuthor() + " (ID: " + book.getBookId() + ")");
            }
        }
    }

    private void updateProfile() {
        System.out.println("Updating your profile:");
        System.out.print("Enter new name (or press enter to keep current): ");
        String name = scanner.nextLine();
        if (name.isEmpty()) name = currentUser.getName();

        System.out.print("Enter new email (or press enter to keep current): ");
        String email = scanner.nextLine();
        if (email.isEmpty()) email = currentUser.getEmail();

        System.out.print("Enter new username (or press enter to keep current): ");
        String username = scanner.nextLine();
        if (username.isEmpty()) username = currentUser.getUsername();

        System.out.print("Enter new password (or press enter to keep current): ");
        String password = scanner.nextLine();
        if (password.isEmpty()) password = currentUser.getPassword();

        try {
            system.getUserManagementService().updateUser(currentUser.getUserId(), name, email, username, password);
            System.out.println("Profile updated successfully.");
            currentUser = system.getUserManagementService().getUser(username); // Use the new username
        } catch (Exception e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("Logged out successfully.");
    }

    private void addBook() {
        System.out.println("Adding a new book:");
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Enter book type (Physical/EBook/AudioBook): ");
        String bookType = scanner.nextLine();

        String[] additionalInfo = null;
        switch (bookType.toLowerCase()) {
            case "physical":
                System.out.print("Enter location: ");
                String location = scanner.nextLine();
                additionalInfo = new String[]{location};
                break;
            case "ebook":
                System.out.print("Enter format: ");
                String format = scanner.nextLine();
                System.out.print("Enter file size (in KB): ");
                String fileSize = scanner.nextLine();
                System.out.print("Enter download link: ");
                String downloadLink = scanner.nextLine();
                additionalInfo = new String[]{format, fileSize, downloadLink};
                break;
            case "audiobook":
                System.out.print("Enter format: ");
                format = scanner.nextLine();
                System.out.print("Enter file size (in KB): ");
                fileSize = scanner.nextLine();
                System.out.print("Enter download link: ");
                downloadLink = scanner.nextLine();
                System.out.print("Enter narrator: ");
                String narrator = scanner.nextLine();
                System.out.print("Enter duration (in minutes): ");
                String duration = scanner.nextLine();
                additionalInfo = new String[]{format, fileSize, downloadLink, narrator, duration};
                break;
            default:
                System.out.println("Invalid book type. Book not added.");
                return;
        }

        try {
            system.getLibraryManagementService().addBook(title, author, isbn, bookType, additionalInfo);
            System.out.println("Book added successfully.");
        } catch (Exception e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    private void removeBook() {
        System.out.print("Enter book ID to remove: ");
        String bookId = scanner.nextLine();
        try {
            system.getLibraryManagementService().removeBook(bookId);
            System.out.println("Book removed successfully.");
        } catch (Exception e) {
            System.out.println("Error removing book: " + e.getMessage());
        }
    }

    private void viewOverdueBooks() {
        List<BorrowingRecord> overdueBooks = system.getLibraryManagementService().getOverdueBooks();
        if (overdueBooks.isEmpty()) {
            System.out.println("No overdue books.");
        } else {
            System.out.println("Overdue books:");
            for (BorrowingRecord record : overdueBooks) {
                System.out.println("Book ID: " + record.getBookId() + 
                                   ", User ID: " + record.getUserId() + 
                                   ", Due Date: " + record.getDueDate());
            }
        }
    }

    private void registerNewUser() {
        System.out.println("Registering new user:");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter role (STUDENT/FACULTY/LIBRARIAN): ");
        String roleString = scanner.nextLine();
        UserRole role = UserRole.valueOf(roleString.toUpperCase());

        try {
            system.getUserManagementService().registerUser(name, email, username, password, role);
            System.out.println("User registered successfully.");
        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }

    private void listAllUsers() {
        List<User> users = system.getUserManagementService().listAllMembers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            for (User user : users) {
                System.out.println(user.getName() + " (" + user.getUsername() + ") - " + user.getRole());
            }
        }
    }

    private void removeUser() {
        System.out.print("Enter username of user to remove: ");
        String username = scanner.nextLine();
        try {
            User userToRemove = system.getUserManagementService().getUser(username);
            if (userToRemove != null) {
                system.getUserManagementService().removeMember(userToRemove.getUserId());
                System.out.println("User removed successfully.");
            } else {
                System.out.println("User not found.");
            }
        } catch (Exception e) {
            System.out.println("Error removing user: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
