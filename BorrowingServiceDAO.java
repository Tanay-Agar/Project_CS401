package biblioConnect_v3;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BorrowingServiceDAO {

    public void createBorrowingRecord(BorrowingRecord record) throws SQLException {
        String query = "INSERT INTO BorrowingRecords (borrowId, userId, bookId, borrowDate, returnDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, record.getUserId());
            stmt.setString(3, record.getBookId());
            stmt.setTimestamp(4, Timestamp.valueOf(record.getBorrowDate()));
            stmt.setTimestamp(5, record.getReturnDate() != null ? Timestamp.valueOf(record.getReturnDate()) : null);
            stmt.executeUpdate();
        }
    }

    public void updateBorrowingRecord(BorrowingRecord record) throws SQLException {
        String query = "UPDATE BorrowingRecords SET returnDate = ? WHERE userId = ? AND bookId = ? AND borrowDate = ? AND returnDate IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(record.getReturnDate()));
            stmt.setString(2, record.getUserId());
            stmt.setString(3, record.getBookId());
            stmt.setTimestamp(4, Timestamp.valueOf(record.getBorrowDate()));
            stmt.executeUpdate();
        }
    }

    public BorrowingRecord getBorrowingRecord(String userId, String bookId) throws SQLException {
        String query = "SELECT * FROM BorrowingRecords WHERE userId = ? AND bookId = ? AND returnDate IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new BorrowingRecord(
                    rs.getString("userId"),
                    rs.getString("bookId"),
                    rs.getTimestamp("borrowDate").toLocalDateTime()
                );
            }
        }
        return null;
    }

    public List<BorrowingRecord> getBorrowingHistoryByUserId(String userId) throws SQLException {
        List<BorrowingRecord> records = new ArrayList<>();
        String query = "SELECT * FROM BorrowingRecords WHERE userId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BorrowingRecord record = new BorrowingRecord(
                    rs.getString("userId"),
                    rs.getString("bookId"),
                    rs.getTimestamp("borrowDate").toLocalDateTime()
                );
                Timestamp returnTimestamp = rs.getTimestamp("returnDate");
                if (returnTimestamp != null) {
                    record.setReturnDate(returnTimestamp.toLocalDateTime());
                }
                records.add(record);
            }
        }
        return records;
    }

    public boolean isBookBorrowed(String bookId) throws SQLException {
        String query = "SELECT COUNT(*) FROM BorrowingRecords WHERE bookId = ? AND returnDate IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}