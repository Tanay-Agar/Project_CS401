package biblioConnect_v3;

import java.util.List;

public class BiblioConnectApplication {
    public static void main(String[] args) {
        System.out.println("Starting BiblioConnect Library Management System...");
        
        try (LibraryManagementSystem system = new LibraryManagementImpl()) {
            initializeSampleData(system);
            verifyDatabaseContent(system);
            
            try (LibraryUI ui = new LibraryUI(system)) {
                ui.start();
            }
        } catch (Exception e) {
            System.out.println("An error occurred while running BiblioConnect: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeSampleData(LibraryManagementSystem system) {
        System.out.println("Initializing sample data...");
        DataInitializer dataInitializer = new DataInitializer();
        try {
            dataInitializer.initializeData();
            dataInitializer.saveToDatabase();
            System.out.println("Sample data initialized and saved to database.");
        } catch (Exception e) {
            System.out.println("Error initializing sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void verifyDatabaseContent(LibraryManagementSystem system) {
        System.out.println("Verifying database content...");
        try {
            List<User> users = system.getUserManagementService().listAllMembers();
            List<Book> books = system.getLibraryManagementService().listAllBooks();
            System.out.println("Users in database: " + users.size());
            System.out.println("Books in database: " + books.size());
            
            if (!users.isEmpty()) {
                User sampleUser = users.get(0);
                System.out.println("Sample user: " + sampleUser.getUsername() + ", Role: " + sampleUser.getRole());
            }
            
            if (!books.isEmpty()) {
                Book sampleBook = books.get(0);
                System.out.println("Sample book: " + sampleBook.getTitle() + " by " + sampleBook.getAuthor());
            }
        } catch (Exception e) {
            System.out.println("Error verifying database content: " + e.getMessage());
            e.printStackTrace();
        }
    }
}