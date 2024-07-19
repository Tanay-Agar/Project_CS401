package biblioConnect_v3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServiceDAO {
    
    public User getUserById(String userId) throws SQLException {
        String query = "SELECT * FROM Users WHERE userId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
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
        return null;
    }

    public void addUser(String userId, String name, String email, String username, String password, UserRole role) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM Users WHERE email = ? OR username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, email);
            checkStmt.setString(2, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // User with this email or username already exists, update it
                String updateQuery = "UPDATE Users SET name = ?, password = ?, role = ? WHERE email = ? OR username = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, name);
                    updateStmt.setString(2, password);
                    updateStmt.setString(3, role.name());
                    updateStmt.setString(4, email);
                    updateStmt.setString(5, username);
                    updateStmt.executeUpdate();
                }
            } else {
                // User doesn't exist, insert new record
                String insertQuery = "INSERT INTO Users (userId, name, email, username, password, role) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, userId);
                    insertStmt.setString(2, name);
                    insertStmt.setString(3, email);
                    insertStmt.setString(4, username);
                    insertStmt.setString(5, password);
                    insertStmt.setString(6, role.name());
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    public void deleteUser(String userId) throws SQLException {
        String query = "DELETE FROM Users WHERE userId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
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

    public User getUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
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
        return null;
    }

    public void updateUser(String userId, String name, String email, String password) throws SQLException {
        String query = "UPDATE Users SET name = ?, email = ?, password = ? WHERE userId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, userId);
            stmt.executeUpdate();
        }
    }
}