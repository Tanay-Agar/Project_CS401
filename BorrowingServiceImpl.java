package biblioConnect_v3;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class BorrowingServiceImpl implements BorrowingService {
    private BorrowingServiceDAO borrowingDAO = new BorrowingServiceDAO();
    private UserService userService = new UserServiceImpl();
    private BookService bookService = new BookServiceImpl();

    @Override
    public void borrowBook(String userId, String bookId) throws SQLException {
        if (!userService.hasPermission(userId, Permission.BORROW_BOOK)) {
            throw new SQLException("Unauthorized operation");
        }
        if (borrowingDAO.isBookBorrowed(bookId)) {
            throw new SQLException("Book is already borrowed");
        }
        BorrowingRecord record = new BorrowingRecord(userId, bookId, LocalDateTime.now());
        borrowingDAO.createBorrowingRecord(record);
    }

    @Override
    public void returnBook(String userId, String bookId) throws SQLException {
        if (!userService.hasPermission(userId, Permission.RETURN_BOOK)) {
            throw new SQLException("Unauthorized operation");
        }
        BorrowingRecord record = borrowingDAO.getBorrowingRecord(userId, bookId);
        if (record == null) {
            throw new SQLException("Borrowing record not found");
        }
        record.setReturnDate(LocalDateTime.now());
        borrowingDAO.updateBorrowingRecord(record);
    }

    @Override
    public List<BorrowingRecord> getUserBorrowingHistory(String userId) throws SQLException {
        return borrowingDAO.getBorrowingHistoryByUserId(userId);
    }

    @Override
    public String generateReport(String userId) throws SQLException {
        if (!userService.hasPermission(userId, Permission.GENERATE_REPORT)) {
            throw new SQLException("Unauthorized operation");
        }
        List<BorrowingRecord> records = borrowingDAO.getBorrowingHistoryByUserId(userId);
        StringBuilder report = new StringBuilder("Borrowing Report for user: " + userId + "\n");
        for (BorrowingRecord record : records) {
            report.append(record.toString()).append("\n");
        }
        return report.toString();
    }
}