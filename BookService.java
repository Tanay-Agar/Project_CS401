package biblioConnect_v3;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BookService {
    void addBook(String librarianId, String bookType, String title, String author, String isbn, Map<String, String> additionalInfo) throws SQLException;
    void removeBook(String librarianId, String bookId) throws SQLException;
    Book searchBook(String bookId) throws SQLException;
    List<Book> listAllBooks(String librarianId) throws SQLException;
    List<Book> listAvailableBooks() throws SQLException;
    List<Book> searchBooks(String query) throws SQLException;
}