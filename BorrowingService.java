package biblioConnect_v3;

import java.sql.SQLException;
import java.util.List;

public interface BorrowingService {
    void borrowBook(String userId, String bookId) throws SQLException;
    void returnBook(String userId, String bookId) throws SQLException;
    List<BorrowingRecord> getUserBorrowingHistory(String userId) throws SQLException;
    String generateReport(String librarianId) throws SQLException;
}