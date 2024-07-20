package biblioConnect_v3;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/project_db";
    private static final String USER = "project_user";
    private static final String PASSWORD = "Luna123!";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Generic create method
    public static <T> void create(T entity) throws SQLException {
        if (entity instanceof Book) {
            createBook((Book) entity);
        } else if (entity instanceof User) {
            createUser((User) entity);
        } else if (entity instanceof BorrowingRecord) {
            createBorrowingRecord((BorrowingRecord) entity);
        } else {
            throw new IllegalArgumentException("Unsupported entity type");
        }
    }

    // Generic update method
    public static <T> void update(T entity) throws SQLException {
        if (entity instanceof Book) {
            updateBook((Book) entity);
        } else if (entity instanceof User) {
            updateUser((User) entity);
        } else if (entity instanceof BorrowingRecord) {
            updateBorrowingRecord((BorrowingRecord) entity);
        } else {
            throw new IllegalArgumentException("Unsupported entity type");
        }
    }

    // Generic read method
    public static <T> T read(Class<T> clazz, String id) throws SQLException {
        if (clazz == Book.class) {
            return (T) readBook(id);
        } else if (clazz == User.class) {
            return (T) readUser(id);
        } else if (clazz == BorrowingRecord.class) {
            return (T) readBorrowingRecord(Integer.parseInt(id));
        } else {
            throw new IllegalArgumentException("Unsupported entity type");
        }
    }

    // Generic readAll method
    public static <T> List<T> readAll(Class<T> clazz) throws SQLException {
        if (clazz == Book.class) {
            return (List<T>) readAllBooks();
        } else if (clazz == User.class) {
            return (List<T>) readAllUsers();
        } else if (clazz == BorrowingRecord.class) {
            return (List<T>) readAllBorrowingRecords();
        } else {
            throw new IllegalArgumentException("Unsupported entity type");
        }
    }

    // Book DAO methods
    public static void createBook(Book book) throws SQLException {
        String sql = "INSERT INTO Books (bookId, title, author, isbn, bookType, location, format, fileSize, downloadLink, narrator, duration, isAvailable) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "title = VALUES(title), author = VALUES(author), bookType = VALUES(bookType), " +
                     "location = VALUES(location), format = VALUES(format), fileSize = VALUES(fileSize), " +
                     "downloadLink = VALUES(downloadLink), narrator = VALUES(narrator), " +
                     "duration = VALUES(duration), isAvailable = VALUES(isAvailable)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getBookId());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getIsbn());
            pstmt.setString(5, book.getBookType());

            if (book instanceof PhysicalBook) {
                pstmt.setString(6, ((PhysicalBook) book).getLocation());
                pstmt.setNull(7, java.sql.Types.VARCHAR);
                pstmt.setNull(8, java.sql.Types.INTEGER);
                pstmt.setNull(9, java.sql.Types.VARCHAR);
                pstmt.setNull(10, java.sql.Types.VARCHAR);
                pstmt.setNull(11, java.sql.Types.INTEGER);
            } else if (book instanceof EBook) {
                EBook ebook = (EBook) book;
                pstmt.setNull(6, java.sql.Types.VARCHAR);
                pstmt.setString(7, ebook.getFormat());
                pstmt.setInt(8, ebook.getFileSize());
                pstmt.setString(9, ebook.getDownloadLink());
                pstmt.setNull(10, java.sql.Types.VARCHAR);
                pstmt.setNull(11, java.sql.Types.INTEGER);
            } else if (book instanceof AudioBook) {
                AudioBook audiobook = (AudioBook) book;
                pstmt.setNull(6, java.sql.Types.VARCHAR);
                pstmt.setString(7, audiobook.getFormat());
                pstmt.setInt(8, audiobook.getFileSize());
                pstmt.setString(9, audiobook.getDownloadLink());
                pstmt.setString(10, audiobook.getNarrator());
                pstmt.setInt(11, audiobook.getDuration());
            }

            pstmt.setBoolean(12, book.isAvailable());
            pstmt.executeUpdate();
        }
    }

    public static Book readBook(String bookId) throws SQLException {
        String sql = "SELECT * FROM Books WHERE bookId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createBookFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public static void updateBook(Book book) throws SQLException {
        String sql = "UPDATE Books SET title = ?, author = ?, isbn = ?, bookType = ?, location = ?, " +
                     "format = ?, fileSize = ?, downloadLink = ?, narrator = ?, duration = ?, isAvailable = ? WHERE bookId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getBookType());

            if (book instanceof PhysicalBook) {
                pstmt.setString(5, ((PhysicalBook) book).getLocation());
                pstmt.setNull(6, java.sql.Types.VARCHAR);
                pstmt.setNull(7, java.sql.Types.INTEGER);
                pstmt.setNull(8, java.sql.Types.VARCHAR);
                pstmt.setNull(9, java.sql.Types.VARCHAR);
                pstmt.setNull(10, java.sql.Types.INTEGER);
            } else if (book instanceof EBook) {
                EBook ebook = (EBook) book;
                pstmt.setNull(5, java.sql.Types.VARCHAR);
                pstmt.setString(6, ebook.getFormat());
                pstmt.setInt(7, ebook.getFileSize());
                pstmt.setString(8, ebook.getDownloadLink());
                pstmt.setNull(9, java.sql.Types.VARCHAR);
                pstmt.setNull(10, java.sql.Types.INTEGER);
            } else if (book instanceof AudioBook) {
                AudioBook audiobook = (AudioBook) book;
                pstmt.setNull(5, java.sql.Types.VARCHAR);
                pstmt.setString(6, audiobook.getFormat());
                pstmt.setInt(7, audiobook.getFileSize());
                pstmt.setString(8, audiobook.getDownloadLink());
                pstmt.setString(9, audiobook.getNarrator());
                pstmt.setInt(10, audiobook.getDuration());
            }

            pstmt.setBoolean(11, book.isAvailable());
            pstmt.setString(12, book.getBookId());
            pstmt.executeUpdate();
        }
    }

    public static void deleteBook(String bookId) throws SQLException {
        String sql = "DELETE FROM Books WHERE bookId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            pstmt.executeUpdate();
        }
    }

    public static List<Book> readAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(createBookFromResultSet(rs));
            }
        }
        return books;
    }

    private static Book createBookFromResultSet(ResultSet rs) throws SQLException {
        String bookType = rs.getString("bookType");
        String bookId = rs.getString("bookId");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String isbn = rs.getString("isbn");
        boolean isAvailable = rs.getBoolean("isAvailable");

        Book book;
        switch (bookType) {
            case "Physical":
                book = new PhysicalBook(title, author, isbn, rs.getString("location"));
                break;
            case "EBook":
                book = new EBook(title, author, isbn, rs.getString("format"), 
                                 rs.getInt("fileSize"), rs.getString("downloadLink"));
                break;
            case "AudioBook":
                book = new AudioBook(title, author, isbn, rs.getString("format"), 
                                     rs.getInt("fileSize"), rs.getString("downloadLink"), 
                                     rs.getString("narrator"), rs.getInt("duration"));
                break;
            default:
                throw new IllegalArgumentException("Unknown book type: " + bookType);
        }
        book.setBookId(bookId);
        book.setAvailable(isAvailable);
        return book;
    }

    // User DAO methods
    public static void createUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (userId, name, email, username, password, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "name = VALUES(name), password = VALUES(password), role = VALUES(role)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getUsername());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getRole().name());
            pstmt.executeUpdate();
        }
    }

    public static User readUser(String userId) throws SQLException {
        String sql = "SELECT * FROM Users WHERE userId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        UserRole.valueOf(rs.getString("role"))
                    );
                }
            }
        }
        return null;
    }

    public static void updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET name = ?, email = ?, username = ?, password = ?, role = ? WHERE userId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getRole().name());
            pstmt.setString(6, user.getUserId());
            pstmt.executeUpdate();
        }
    }

    public static void deleteUser(String userId) throws SQLException {
        String sql = "DELETE FROM Users WHERE userId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.executeUpdate();
        }
    }

    public static List<User> readAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                    rs.getString("userId"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("username"),
                    rs.getString("password"),
                    UserRole.valueOf(rs.getString("role"))
                ));
            }
        }
        return users;
    }

    // BorrowingRecord DAO methods
    public static void createBorrowingRecord(BorrowingRecord record) throws SQLException {
        String sql = "INSERT INTO BorrowingRecords (userId, bookId, borrowDate, returnDate, isReservation, dueDate) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "returnDate = VALUES(returnDate), isReservation = VALUES(isReservation), dueDate = VALUES(dueDate)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, record.getUserId());
            pstmt.setString(2, record.getBookId());
            pstmt.setTimestamp(3, Timestamp.valueOf(record.getBorrowDate()));
            pstmt.setTimestamp(4, record.getReturnDate() != null ? Timestamp.valueOf(record.getReturnDate()) : null);
            pstmt.setBoolean(5, record.isReservation());
            pstmt.setTimestamp(6, Timestamp.valueOf(record.getDueDate()));
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    record.setRecordId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public static BorrowingRecord readBorrowingRecord(int recordId) throws SQLException {
        String sql = "SELECT * FROM BorrowingRecords WHERE recordId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createBorrowingRecordFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public static void updateBorrowingRecord(BorrowingRecord record) throws SQLException {
        String sql = "UPDATE BorrowingRecords SET userId = ?, bookId = ?, borrowDate = ?, returnDate = ?, isReservation = ?, dueDate = ? WHERE recordId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, record.getUserId());
            pstmt.setString(2, record.getBookId());
            pstmt.setTimestamp(3, Timestamp.valueOf(record.getBorrowDate()));
            pstmt.setTimestamp(4, record.getReturnDate() != null ? Timestamp.valueOf(record.getReturnDate()) : null);
            pstmt.setBoolean(5, record.isReservation());
            pstmt.setTimestamp(6, Timestamp.valueOf(record.getDueDate()));
            pstmt.setInt(7, record.getRecordId());
            pstmt.executeUpdate();
        }
    }

    public static void deleteBorrowingRecord(int recordId) throws SQLException {
        String sql = "DELETE FROM BorrowingRecords WHERE recordId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            pstmt.executeUpdate();
        }
    }

    public static List<BorrowingRecord> readAllBorrowingRecords() throws SQLException {
        List<BorrowingRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM BorrowingRecords";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                records.add(createBorrowingRecordFromResultSet(rs));
            }
        }
        return records;
    }

    private static BorrowingRecord createBorrowingRecordFromResultSet(ResultSet rs) throws SQLException {
        BorrowingRecord record = new BorrowingRecord(
            rs.getString("userId"),
            rs.getString("bookId"),
            rs.getTimestamp("borrowDate").toLocalDateTime()
        );
        record.setRecordId(rs.getInt("recordId"));
        Timestamp returnDate = rs.getTimestamp("returnDate");
        if (returnDate != null) {
            record.setReturnDate(returnDate.toLocalDateTime());
        }
        record.setReservation(rs.getBoolean("isReservation"));
        record.setDueDate(rs.getTimestamp("dueDate").toLocalDateTime());
        return record;
    }

    // Additional utility methods

    public static User readUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        UserRole.valueOf(rs.getString("role"))
                    );
                }
            }
        }
        return null;
    }

    public static List<BorrowingRecord> getBorrowingRecordsByUserId(String userId) throws SQLException {
        List<BorrowingRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM BorrowingRecords WHERE userId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(createBorrowingRecordFromResultSet(rs));
                }
            }
        }
        return records;
    }

    public static List<BorrowingRecord> getBorrowingRecordsByBookId(String bookId) throws SQLException {
        List<BorrowingRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM BorrowingRecords WHERE bookId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(createBorrowingRecordFromResultSet(rs));
                }
            }
        }
        return records;
    }

    public static List<BorrowingRecord> getOverdueBorrowingRecords() throws SQLException {
        List<BorrowingRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM BorrowingRecords WHERE dueDate < ? AND returnDate IS NULL";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(createBorrowingRecordFromResultSet(rs));
                }
            }
        }
        return records;
    }
}
