package biblioConnect_v3;

import java.security.SecureRandom;

public abstract class Book {
    protected String bookId;
    protected String title;
    protected String author;
    protected String isbn;
    protected String bookType;
    protected boolean isAvailable;

    public Book(String title, String author, String isbn, String bookType) {
        this.bookId = generateBookId();
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.bookType = bookType;
        this.isAvailable = true;
    }

    public static String generateBookId() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(6);
        String alphaNumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 6; i++) {
            sb.append(alphaNumeric.charAt(random.nextInt(alphaNumeric.length())));
        }
        return sb.toString();
    }

    // Getters and setters
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getBookType() { return bookType; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public boolean isCurrentlyBorrowed() {
        return !isAvailable;
    }

    public abstract String getDetails();

    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", bookType='" + bookType + '\'' +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
