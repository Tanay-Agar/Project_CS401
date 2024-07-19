package biblioConnect_v3;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BookServiceImpl implements BookService {
    private BookServiceDAO bookDAO = new BookServiceDAO();
    private UserService userService = new UserServiceImpl();

    @Override
    public void addBook(String librarianId, String bookType, String title, String author, String isbn, Map<String, String> additionalInfo) throws SQLException {
        if (!userService.hasPermission(librarianId, Permission.ADD_BOOK)) {
            throw new SQLException("Unauthorized operation");
        }
        String bookId = UUID.randomUUID().toString();
        bookDAO.addBook(bookId, title, author, isbn, bookType, additionalInfo);
    }

    @Override
    public void removeBook(String librarianId, String bookId) throws SQLException {
        if (!userService.hasPermission(librarianId, Permission.REMOVE_BOOK)) {
            throw new SQLException("Unauthorized operation");
        }
        bookDAO.removeBook(bookId);
    }

    @Override
    public Book searchBook(String bookId) throws SQLException {
        return bookDAO.getBookById(bookId);
    }

    @Override
    public List<Book> listAllBooks(String librarianId) throws SQLException {
        if (!userService.hasPermission(librarianId, Permission.VIEW_ALL_BOOKS)) {
            throw new SQLException("Unauthorized operation");
        }
        return bookDAO.getAllBooks();
    }

    @Override
    public List<Book> listAvailableBooks() throws SQLException {
        return bookDAO.getAllAvailableBooks();
    }

    @Override
    public List<Book> searchBooks(String query) throws SQLException {
        return bookDAO.searchBooks(query);
    }
}