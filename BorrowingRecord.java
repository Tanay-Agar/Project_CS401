package biblioConnect_v3;

import java.time.LocalDateTime;

public class BorrowingRecord {
    private String userId;
    private String bookId;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;

    public BorrowingRecord(String userId, String bookId, LocalDateTime borrowDate) {
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
    }

    public String getUserId() { return userId; }
    public String getBookId() { return bookId; }
    public LocalDateTime getBorrowDate() { return borrowDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }

    @Override
    public String toString() {
        return "BorrowingRecord{" +
                "userId='" + userId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                '}';
    }
}