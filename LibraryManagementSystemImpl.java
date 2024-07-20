package biblioConnect_v3;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryManagementServiceImpl implements LibraryManagementService {
    private UserManagementService userManagementService;

    public LibraryManagementServiceImpl(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @Override
    public void addBook(String title, String author, String isbn, String bookType, String... additionalInfo) {
        Book book = BookFactory.createBook(bookType, title, author, isbn, additionalInfo);
        try {
            DatabaseConnection.create(book);
        } catch (SQLException e) {
            throw new RuntimeException("Error adding book: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeBook(String bookId) {
        try {
            DatabaseConnection.deleteBook(bookId);
        } catch (SQLException e) {
            throw new RuntimeException("Error removing book: " + e.getMessage(), e);
        }
    }

    @Override
    public Book getBook(String bookId) {
        try {
            return DatabaseConnection.read(Book.class, bookId);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting book: " + e.getMessage(), e);
        }
    }

    @Override
    public void borrowBook(String userId, String bookId) {
        try {
            Book book = DatabaseConnection.read(Book.class, bookId);
            if (book == null || !book.isAvailable()) {
                throw new RuntimeException("Book is not available for borrowing.");
            }
            
            book.setAvailable(false);
            DatabaseConnection.update(book);

            BorrowingRecord record = new BorrowingRecord(userId, bookId, LocalDateTime.now());
            DatabaseConnection.create(record);
        } catch (SQLException e) {
            throw new RuntimeException("Error borrowing book: " + e.getMessage(), e);
        }
    }

    @Override
    public void returnBook(String userId, String bookId) {
        try {
            List<BorrowingRecord> records = DatabaseConnection.readAll(BorrowingRecord.class);
            for (BorrowingRecord record : records) {
                if (record.getUserId().equals(userId) && record.getBookId().equals(bookId) && record.getReturnDate() == null) {
                    record.setReturnDate(LocalDateTime.now());
                    DatabaseConnection.update(record);

                    Book book = DatabaseConnection.read(Book.class, bookId);
                    book.setAvailable(true);
                    DatabaseConnection.update(book);

                    // Check for reservations
                    List<BorrowingRecord> reservations = getUserReservations(bookId);
                    if (!reservations.isEmpty()) {
                        BorrowingRecord nextReservation = reservations.get(0);
                        borrowBook(nextReservation.getUserId(), bookId);
                        nextReservation.setReservation(false);
                        DatabaseConnection.update(nextReservation);
                    }

                    return;
                }
            }
            throw new RuntimeException("No active borrowing record found for user " + userId + " and book " + bookId);
        } catch (SQLException e) {
            throw new RuntimeException("Error returning book: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Book> searchBooks(String query) {
        try {
            List<Book> allBooks = DatabaseConnection.readAll(Book.class);
            return allBooks.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase()) || 
                                book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                                book.getIsbn().equals(query))
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error searching books: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BorrowingRecord> getBorrowingHistory(String userId) {
        try {
            List<BorrowingRecord> allRecords = DatabaseConnection.readAll(BorrowingRecord.class);
            return allRecords.stream()
                .filter(record -> record.getUserId().equals(userId))
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting borrowing history: " + e.getMessage(), e);
        }
    }

    @Override
    public void reserveBook(String userId, String bookId) {
        try {
            Book book = DatabaseConnection.read(Book.class, bookId);
            if (book.isAvailable()) {
                throw new RuntimeException("Book is currently available and can be borrowed directly.");
            }
            
            BorrowingRecord reservation = new BorrowingRecord(userId, bookId, LocalDateTime.now());
            reservation.setReservation(true);
            DatabaseConnection.create(reservation);
        } catch (SQLException e) {
            throw new RuntimeException("Error reserving book: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BorrowingRecord> getUserReservations(String userId) {
        try {
            List<BorrowingRecord> allRecords = DatabaseConnection.readAll(BorrowingRecord.class);
            return allRecords.stream()
                .filter(record -> record.getUserId().equals(userId) && record.isReservation() && record.getReturnDate() == null)
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting user reservations: " + e.getMessage(), e);
        }
    }

    @Override
    public LocalDateTime getExpectedReturnDate(String bookId) {
        try {
            List<BorrowingRecord> records = DatabaseConnection.readAll(BorrowingRecord.class);
            return records.stream()
                .filter(record -> record.getBookId().equals(bookId) && !record.isReservation() && record.getReturnDate() == null)
                .findFirst()
                .map(BorrowingRecord::getDueDate)
                .orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting expected return date: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Book> listAllBooks() {
        try {
            return DatabaseConnection.readAll(Book.class);
        } catch (SQLException e) {
            throw new RuntimeException("Error listing all books: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BorrowingRecord> getOverdueBooks() {
        try {
            List<BorrowingRecord> allRecords = DatabaseConnection.readAll(BorrowingRecord.class);
            LocalDateTime now = LocalDateTime.now();
            return allRecords.stream()
                .filter(record -> !record.isReservation() && record.getReturnDate() == null && record.getDueDate().isBefore(now))
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting overdue books: " + e.getMessage(), e);
        }
    }
}
