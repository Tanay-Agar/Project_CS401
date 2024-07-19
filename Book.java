package biblioConnect_v3;

public abstract class Book {
    protected String bookId;
    protected String title;
    protected String author;
    protected String isbn;
    protected String bookType;

    public Book(String bookId, String title, String author, String isbn, String bookType) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.bookType = bookType;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getBookType() { return bookType; }

    public abstract String getDetails();

    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", bookType='" + bookType + '\'' +
                ", details='" + getDetails() + '\'' +
                '}';
    }
}