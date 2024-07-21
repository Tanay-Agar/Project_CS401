package biblioConnect_v3;

import java.sql.SQLException;
import java.util.List;

public class UserManagementServiceImpl implements UserManagementService {

    @Override
    public void registerUser(String name, String email, String username, String password, UserRole role) {
        User newUser = new User(name, email, username, password, role);
        try {
            DatabaseConnection.create(newUser);
        } catch (SQLException e) {
            throw new RuntimeException("Error registering user: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateUser(String userId, String name, String email, String username, String password) {
        try {
            User user = DatabaseConnection.read(User.class, userId);
            if (user != null) {
                // Check if the new username already exists (if it's different from the current one)
                if (!user.getUsername().equals(username)) {
                    User existingUser = DatabaseConnection.readUserByUsername(username);
                    if (existingUser != null) {
                        throw new RuntimeException("Username already exists. Please choose a different username.");
                    }
                }
                
                user.setName(name);
                user.setEmail(email);
                user.setUsername(username);
                user.setPassword(password);
                DatabaseConnection.update(user);
            } else {
                throw new RuntimeException("User not found: " + userId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    @Override
    public User getUser(String username) {
        try {
            return DatabaseConnection.readUserByUsername(username);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        try {
            User user = DatabaseConnection.readUserByUsername(username);
            return user != null && user.getPassword().equals(password);
        } catch (SQLException e) {
            throw new RuntimeException("Error authenticating user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasPermission(String username, String action) {
        User user = getUser(username);
        if (user != null) {
            switch (action) {
                case "BORROW_BOOK":
                case "RETURN_BOOK":
                case "RESERVE_BOOK":
                    return true;
                case "ADD_BOOK":
                case "REMOVE_BOOK":
                case "VIEW_ALL_USERS":
                case "VIEW_OVERDUE_BOOKS":
                    return user.getRole() == UserRole.LIBRARIAN;
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public List<User> listAllMembers() {
        try {
            return DatabaseConnection.readAll(User.class);
        } catch (SQLException e) {
            throw new RuntimeException("Error listing all members: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeMember(String userId) {
        try {
            DatabaseConnection.deleteUser(userId);
        } catch (SQLException e) {
            throw new RuntimeException("Error removing member: " + e.getMessage(), e);
        }
    }
}