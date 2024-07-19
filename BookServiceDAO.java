package biblioConnect_v3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookServiceDAO {

    public void addBook(String bookId, String title, String author, String isbn, String bookType, Map<String, String> additionalInfo) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // First, add the book to the Books table
            String insertBookQuery = "INSERT INTO Books (bookId, title, author, isbn, bookType) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertBookQuery)) {
                stmt.setString(1, bookId);
                stmt.setString(2, title);
                stmt.setString(3, author);
                stmt.setString(4, isbn);
                stmt.setString(5, bookType);
                stmt.executeUpdate();
            }

            // Then, add additional info based on book type
            switch (bookType.toLowerCase()) {
                case "physical":
                    addPhysicalBook(conn, bookId, additionalInfo.get("location"));
                    break;
                case "ebook":
                    addEBook(conn, bookId, additionalInfo.get("format"), Integer.parseInt(additionalInfo.get("fileSize")), additionalInfo.get("downloadLink"));
                    break;
                case "audiobook":
                    addAudiobook(conn, bookId, additionalInfo.get("format"), Integer.parseInt(additionalInfo.get("fileSize")), additionalInfo.get("downloadLink"), additionalInfo.get("narrator"), Integer.parseInt(additionalInfo.get("duration")));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid book type: " + bookType);
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addPhysicalBook(Connection conn, String bookId, String location) throws SQLException {
        String query = "INSERT INTO PhysicalBooks (bookId, location) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookId);
            stmt.setString(2, location);
            stmt.executeUpdate();
        }
    }

    private void addEBook(Connection conn, String bookId, String format, int fileSize, String downloadLink) throws SQLException {
        String query = "INSERT INTO EBooks (bookId, format, fileSize, downloadLink) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookId);
            stmt.setString(2, format);
            stmt.setInt(3, fileSize);
            stmt.setString(4, downloadLink);
            stmt.executeUpdate();
        }
    }

    private void addAudiobook(Connection conn, String bookId, String format, int fileSize, String downloadLink, String narrator, int duration) throws SQLException {
        String query = "INSERT INTO Audiobooks (bookId, format, fileSize, downloadLink, narrator, duration) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookId);
            stmt.setString(2, format);
            stmt.setInt(3, fileSize);
            stmt.setString(4, downloadLink);
            stmt.setString(5, narrator);
            stmt.setInt(6, duration);
            stmt.executeUpdate();
        }
    }

    public void removeBook(String bookId) throws SQLException {
        String query = "DELETE FROM Books WHERE bookId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookId);
            stmt.executeUpdate();
        }
    }

    public Book getBookById(String bookId) throws SQLException {
        String query = "SELECT * FROM Books WHERE bookId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createBookFromResultSet(rs);
            }
        }
        return null;
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                books.add(createBookFromResultSet(rs));
            }
        }
        return books;
    }

    public List<Book> getAllAvailableBooks() throws SQLException {
        List<Book> availableBooks = new ArrayList<>();
        String query = "SELECT b.* FROM Books b LEFT JOIN BorrowingRecords br ON b.bookId = br.bookId AND br.returnDate IS NULL WHERE br.bookId IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                availableBooks.add(createBookFromResultSet(rs));
            }
        }
        return availableBooks;
    }

    public List<Book> searchBooks(String searchTerm) throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE title LIKE ? OR author LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(createBookFromResultSet(rs));
            }
        }
        return books;
    }

    private Book createBookFromResultSet(ResultSet rs) throws SQLException {
        String bookType = rs.getString("bookType");
        String bookId = rs.getString("bookId");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String isbn = rs.getString("isbn");

        switch (bookType.toLowerCase()) {
            case "physical":
                return getPhysicalBook(bookId, title, author, isbn);
            case "ebook":
                return getEBook(bookId, title, author, isbn);
            case "audiobook":
                return getAudiobook(bookId, title, author, isbn);
            default:
                throw new SQLException("Unknown book type: " + bookType);
        }
    }

    private PhysicalBook getPhysicalBook(String bookId, String title, String author, String isbn) throws SQLException {
        String query = "SELECT location FROM PhysicalBooks WHERE bookId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PhysicalBook(bookId, title, author, isbn, rs.getString("location"));
            }
        }
        throw new SQLException("Physical book details not found for bookId: " + bookId);
    }

    private EBook getEBook(String bookId, String title, String author, String isbn) throws SQLException {
        String query = "SELECT format, fileSize, downloadLink FROM EBooks WHERE bookId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new EBook(bookId, title, author, isbn, rs.getString("format"), rs.getInt("fileSize"), rs.getString("downloadLink"));
            }
        }
        throw new SQLException("EBook details not found for bookId: " + bookId);
    }

    private Audiobook getAudiobook(String bookId, String title, String author, String isbn) throws SQLException {
        String query = "SELECT format, fileSize, downloadLink, narrator, duration FROM Audiobooks WHERE bookId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Audiobook(bookId, title, author, isbn, rs.getString("format"), rs.getInt("fileSize"), rs.getString("downloadLink"), rs.getString("narrator"), rs.getInt("duration"));
            }
        }
        throw new SQLException("Audiobook details not found for bookId: " + bookId);
    }
}