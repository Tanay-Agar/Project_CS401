package biblioConnect_v3;

import java.util.HashMap;
import java.util.Map;

public class LibrarySystemDemo {
    public static void main(String[] args) {
        try {
            LibrarySystem systemInstance = new LibrarySystem();
            LibraryManagementSystem library = systemInstance.getLibrary();

            // Initialize users
            initializeUsers(library);

            // Add sample books
            addSampleBooks(library);

            // Start the UI
            LibraryUI ui = new LibraryUI(library);
            ui.start();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeUsers(LibraryManagementSystem library) {
        try {
            UserService userService = library.getUserService();
            
            userService.registerUser("John Librarian", "john@library.com", "johnlib", "password123", UserRole.LIBRARIAN);
            userService.registerUser("Alice Student", "alice@university.edu", "alicestudent", "password456", UserRole.STUDENT);
            userService.registerUser("Bob Faculty", "bob@university.edu", "bobfaculty", "password789", UserRole.FACULTY);
            
            System.out.println("Initial users registered successfully.");
        } catch (Exception e) {
            System.err.println("Error initializing users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addSampleBooks(LibraryManagementSystem library) {
        try {
            UserService userService = library.getUserService();
            User librarian = userService.getUserByUsername("johnlib");
            if (librarian == null) {
                System.out.println("Librarian not found. Please ensure users are initialized properly.");
                return;
            }
            String librarianId = librarian.getUserId();

            BookService bookService = library.getBookService();

            Map<String, String> physicalBookInfo = new HashMap<>();
            physicalBookInfo.put("location", "Shelf A1");
            bookService.addBook(librarianId, "Physical", "C++ Fundamentals", "Alice Johnson", "1122334455", physicalBookInfo);

            Map<String, String> ebookInfo = new HashMap<>();
            ebookInfo.put("format", "PDF");
            ebookInfo.put("fileSize", "1000");
            ebookInfo.put("downloadLink", "http://example.com/book1");
            bookService.addBook(librarianId, "EBook", "Java Programming", "John Doe", "1234567890", ebookInfo);

            Map<String, String> audiobookInfo = new HashMap<>();
            audiobookInfo.put("format", "MP3");
            audiobookInfo.put("fileSize", "500");
            audiobookInfo.put("downloadLink", "http://example.com/book2");
            audiobookInfo.put("narrator", "John Smith");
            audiobookInfo.put("duration", "180");
            bookService.addBook(librarianId, "Audiobook", "Python Basics", "Jane Smith", "0987654321", audiobookInfo);

            System.out.println("Sample books added successfully.");
        } catch (Exception e) {
            System.err.println("Error adding sample books: " + e.getMessage());
            e.printStackTrace();
        }
    }
}