package biblioConnect_v3;

import java.util.*;
import java.sql.SQLException;

public class LibraryUI {
    private Map<String, Runnable> commands = new HashMap<>();
    private LibraryManagementSystem library;
    private User currentUser;
    private Scanner scanner;

    public LibraryUI(LibraryManagementSystem library) {
        this.library = library;
        this.scanner = new Scanner(System.in);
        initializeCommands();
    }

    private void initializeCommands() {
        commands.put("Search for a book", this::searchBook);
        commands.put("View my borrowing history", this::viewBorrowingHistory);
        commands.put("Borrow a book", this::borrowBook);
        commands.put("Return a book", this::returnBook);
        commands.put("Update my profile", this::updateProfile);
        commands.put("Add a new book", this::addBook);
        commands.put("Remove a book", this::removeBook);
        commands.put("View all users", this::viewAllUsers);
        commands.put("View all books", this::viewAllBooks);
        commands.put("View available books", this::viewAvailableBooks);
        commands.put("Generate report", this::generateReport);
        commands.put("Logout", this::logout);
    }

    public void start() {
        System.out.println("Welcome to the Library Management System!");
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n--- Login Menu ---");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        int choice = getValidInput("Enter your choice: ", 1, 2);
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                System.out.println("Thank you for using the Library Management System. Goodbye!");
                System.exit(0);
        }
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("\n--- Library Management System ---");
            System.out.println("Welcome, " + currentUser.getName() + "!");
            List<String> options = new ArrayList<>();
            for (Map.Entry<String, Runnable> entry : commands.entrySet()) {
                String option = entry.getKey();
                if (option.equals("Logout") || hasPermissionForOption(option)) {
                    options.add(option);
                }
            }
            for (int i = 0; i < options.size(); i++) {
                System.out.println((i + 1) + ". " + options.get(i));
            }

            int choice = getValidInput("Enter your choice: ", 1, options.size());
            String selectedOption = options.get(choice - 1);
            commands.get(selectedOption).run();
            if (selectedOption.equals("Logout")) {
                break;
            }
        }
    }

    private boolean hasPermissionForOption(String option) {
        try {
            switch (option) {
                case "Borrow a book":
                    return library.getUserService().hasPermission(currentUser.getUserId(), Permission.BORROW_BOOK);
                case "Return a book":
                    return library.getUserService().hasPermission(currentUser.getUserId(), Permission.RETURN_BOOK);
                case "Add a new book":
                    return library.getUserService().hasPermission(currentUser.getUserId(), Permission.ADD_BOOK);
                case "Remove a book":
                    return library.getUserService().hasPermission(currentUser.getUserId(), Permission.REMOVE_BOOK);
                case "View all users":
                    return library.getUserService().hasPermission(currentUser.getUserId(), Permission.VIEW_ALL_USERS);
                case "Generate report":
                    return library.getUserService().hasPermission(currentUser.getUserId(), Permission.GENERATE_REPORT);
                default:
                    return true;
            }
        } catch (Exception e) {
            System.out.println("Error checking permissions: " + e.getMessage());
            return false;
        }
    }

    private int getValidInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void login() {
        String username = getInput("Enter username: ");
        String password = getInput("Enter password: ");

        try {
            if (library.getUserService().verifyLogin(username, password)) {
                currentUser = library.getUserService().getUserByUsername(username);
                System.out.println("Login successful. Welcome, " + currentUser.getName() + "!");
            } else {
                System.out.println("Invalid username or password. Please try again.");
            }
        } catch (Exception e) {
            handleException("Login failed", e);
        }
    }

    private String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private void handleException(String message, Exception e) {
        System.out.println(message + ": " + e.getMessage());
    }

    private void searchBook() {
        String query = getInput("Enter book title or author: ");
        try {
            List<Book> books = library.getBookService().searchBooks(query);
            if (books.isEmpty()) {
                System.out.println("No books found matching your query.");
            } else {
                System.out.println("Search results:");
                for (Book book : books) {
                    System.out.println(book);
                }
            }
        } catch (Exception e) {
            handleException("Search failed", e);
        }
    }

    private void viewBorrowingHistory() {
        try {
            List<BorrowingRecord> history = library.getBorrowingService().getUserBorrowingHistory(currentUser.getUserId());
            if (history.isEmpty()) {
                System.out.println("You have no borrowing history.");
            } else {
                System.out.println("Your borrowing history:");
                for (BorrowingRecord record : history) {
                    System.out.println(record);
                }
            }
        } catch (Exception e) {
            handleException("Failed to retrieve borrowing history", e);
        }
    }

    private void borrowBook() {
        String bookId = getInput("Enter the ID of the book you want to borrow: ");
        try {
            library.getBorrowingService().borrowBook(currentUser.getUserId(), bookId);
            System.out.println("Book borrowed successfully.");
        } catch (Exception e) {
            handleException("Failed to borrow book", e);
        }
    }

    private void returnBook() {
        String bookId = getInput("Enter the ID of the book you want to return: ");
        try {
            library.getBorrowingService().returnBook(currentUser.getUserId(), bookId);
            System.out.println("Book returned successfully.");
        } catch (Exception e) {
            handleException("Failed to return book", e);
        }
    }

    private void updateProfile() {
        String name = getInput("Enter new name (or press enter to skip): ");
        String email = getInput("Enter new email (or press enter to skip): ");
        String password = getInput("Enter new password (or press enter to skip): ");

        try {
            library.getUserService().updateProfile(currentUser.getUserId(), 
                name.isEmpty() ? currentUser.getName() : name,
                email.isEmpty() ? currentUser.getEmail() : email,
                password.isEmpty() ? currentUser.getPassword() : password);
            System.out.println("Profile updated successfully.");
            // Refresh the current user object
            currentUser = library.getUserService().getUserByUsername(currentUser.getUsername());
        } catch (Exception e) {
            handleException("Failed to update profile", e);
        }
    }

    private void addBook() {
        try {
            String title = getInput("Enter book title: ");
            String author = getInput("Enter author: ");
            String isbn = getInput("Enter ISBN: ");
            String bookType = getInput("Enter book type (Physical/EBook/Audiobook): ");
            
            Map<String, String> additionalInfo = new HashMap<>();
            switch (bookType.toLowerCase()) {
                case "physical":
                    additionalInfo.put("location", getInput("Enter shelf location: "));
                    break;
                case "ebook":
                case "audiobook":
                    additionalInfo.put("format", getInput("Enter file format: "));
                    additionalInfo.put("fileSize", getInput("Enter file size in KB: "));
                    additionalInfo.put("downloadLink", getInput("Enter download link: "));
                    if (bookType.equalsIgnoreCase("audiobook")) {
                        additionalInfo.put("narrator", getInput("Enter narrator name: "));
                        additionalInfo.put("duration", getInput("Enter duration in minutes: "));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid book type");
            }

            library.getBookService().addBook(currentUser.getUserId(), bookType, title, author, isbn, additionalInfo);
            System.out.println("Book added successfully.");
        } catch (Exception e) {
            handleException("Failed to add book", e);
        }
    }

    private void removeBook() {
        String bookId = getInput("Enter the ID of the book you want to remove: ");
        try {
            library.getBookService().removeBook(currentUser.getUserId(), bookId);
            System.out.println("Book removed successfully.");
        } catch (Exception e) {
            handleException("Failed to remove book", e);
        }
    }

    private void viewAllUsers() {
        try {
            List<User> users = library.getUserService().listAllUsers(currentUser.getUserId());
            System.out.println("All users:");
            for (User user : users) {
                System.out.println(user);
            }
        } catch (Exception e) {
            handleException("Failed to view all users", e);
        }
    }

    private void viewAllBooks() {
        try {
            List<Book> books = library.getBookService().listAllBooks(currentUser.getUserId());
            System.out.println("All books:");
            for (Book book : books) {
                System.out.println(book);
            }
        } catch (Exception e) {
            handleException("Failed to view all books", e);
        }
    }

    private void viewAvailableBooks() {
        try {
            List<Book> books = library.getBookService().listAvailableBooks();
            System.out.println("Available books:");
            for (Book book : books) {
                System.out.println(book);
            }
        } catch (Exception e) {
            handleException("Failed to view available books", e);
        }
    }

    private void generateReport() {
        try {
            String report = library.getBorrowingService().generateReport(currentUser.getUserId());
            System.out.println("Generated Report:");
            System.out.println(report);
        } catch (Exception e) {
            handleException("Failed to generate report", e);
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("Logged out successfully.");
    }
}