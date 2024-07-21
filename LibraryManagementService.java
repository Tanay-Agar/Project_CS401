package biblioConnect_v3;

import java.time.LocalDateTime;
import java.util.List;

public interface LibraryManagementService {
    void addBook(String title, String author, String isbn, String bookType, String... additionalInfo);
    void removeBook(String bookId);
    Book getBook(String bookId);
    void borrowBook(String userId, String bookId);
    void returnBook(String userId, String bookId);
    List<Book> searchBooks(String query);
    List<BorrowingRecord> getBorrowingHistory(String userId);
    void reserveBook(String userId, String bookId);
    List<BorrowingRecord> getUserReservations(String userId);
    LocalDateTime getExpectedReturnDate(String bookId);
    List<Book> listAllBooks();
    List<BorrowingRecord> getOverdueBooks();
}