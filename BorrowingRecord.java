package biblioConnect_v3;

import java.time.LocalDateTime;

public class BorrowingRecord {
    private int recordId;
    private String userId;
    private String bookId;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private boolean isReservation;
    private LocalDateTime dueDate;

    public BorrowingRecord(String userId, String bookId, LocalDateTime borrowDate) {
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.isReservation = false;
        this.dueDate = borrowDate.plusDays(14); // Default 14-day borrowing period
    }

    // Getters and setters
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public String getUserId() { return userId; }
    public String getBookId() { return bookId; }
    public LocalDateTime getBorrowDate() { return borrowDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
    public boolean isReservation() { return isReservation; }
    public void setReservation(boolean reservation) { isReservation = reservation; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    @Override
    public String toString() {
        return "BorrowingRecord{" +
                "recordId=" + recordId +
                ", userId='" + userId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                ", isReservation=" + isReservation +
                ", dueDate=" + dueDate +
                '}';
    }
}
