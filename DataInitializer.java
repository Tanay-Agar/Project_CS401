package biblioConnect_v3;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataInitializer {
    private List<User> users = new ArrayList<>();
    private List<Book> books = new ArrayList<>();
    private Random random = new Random();

    public void initializeData() {
        System.out.println("Creating sample users and books...");
        createUsers();
        createBooks();
        createSampleBorrowings();
        System.out.println("Sample data created in memory.");
    }

    private void createUsers() {
        users.add(new User("John Doe", "john.doe@example.com", "johnd", "password", UserRole.STUDENT));
        users.add(new User("Jane Smith", "jane.smith@example.com", "janes", "password", UserRole.STUDENT));
        users.add(new User("Bob Johnson", "bob.johnson@example.com", "bobj", "password", UserRole.FACULTY));
        users.add(new User("Alice Brown", "alice.brown@example.com", "aliceb", "password", UserRole.FACULTY));
        users.add(new User("Librarian One", "librarian1@library.com", "librarian1", "password", UserRole.LIBRARIAN));
    }

    private void createBooks() {
        books.add(new PhysicalBook("To Kill a Mockingbird", "Harper Lee", "9780446310789", "Shelf A1"));
        books.add(new PhysicalBook("1984", "George Orwell", "9780451524935", "Shelf B2"));
        books.add(new EBook("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", "PDF", 2048, "https://example.com/greatgatsby.pdf"));
        books.add(new EBook("Pride and Prejudice", "Jane Austen", "9780141439518", "EPUB", 1536, "https://example.com/prideandprejudice.epub"));
        books.add(new AudioBook("The Catcher in the Rye", "J.D. Salinger", "9780241950425", "MP3", 102400, "https://example.com/catcherintherye.mp3", "John Smith", 480));
    }

    private void createSampleBorrowings() {
        if (!users.isEmpty() && !books.isEmpty()) {
            try {
                BorrowingRecord borrowing1 = new BorrowingRecord(users.get(0).getUserId(), books.get(0).getBookId(), LocalDateTime.now().minusDays(5));
                BorrowingRecord borrowing2 = new BorrowingRecord(users.get(1).getUserId(), books.get(1).getBookId(), LocalDateTime.now().minusDays(2));
                borrowing2.setReservation(true);
                
                DatabaseConnection.create(borrowing1);
                DatabaseConnection.create(borrowing2);
                
                books.get(0).setAvailable(false);
                DatabaseConnection.update(books.get(0));
                
            } catch (SQLException e) {
                System.out.println("Error creating sample borrowings: " + e.getMessage());
            }
        }
    }

    public void saveToDatabase() {
        System.out.println("Saving sample data to database...");
        try {
            for (User user : users) {
                DatabaseConnection.create(user);
            }
            for (Book book : books) {
                DatabaseConnection.create(book);
            }
            System.out.println("Sample data saved to database successfully.");
        } catch (SQLException e) {
            System.out.println("Error saving sample data to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Book> getBooks() {
        return books;
    }
}