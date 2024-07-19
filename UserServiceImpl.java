package biblioConnect_v3;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    private UserServiceDAO userDAO = new UserServiceDAO();

    @Override
    public void registerUser(String name, String email, String username, String password, UserRole role) throws SQLException {
        String userId = UUID.randomUUID().toString();
        userDAO.addUser(userId, name, email, username, password, role);
    }

    @Override
    public void removeUser(String librarianId, String userId) throws SQLException {
        if (!hasPermission(librarianId, Permission.REMOVE_USER)) {
            throw new SQLException("Unauthorized operation");
        }
        userDAO.deleteUser(userId);
    }

    @Override
    public User searchUser(String userId) throws SQLException {
        return userDAO.getUserById(userId);
    }

    @Override
    public List<User> listAllUsers(String librarianId) throws SQLException {
        if (!hasPermission(librarianId, Permission.VIEW_ALL_USERS)) {
            throw new SQLException("Unauthorized operation");
        }
        return userDAO.getAllUsers();
    }

    @Override
    public boolean verifyLogin(String username, String password) throws SQLException {
        User user = userDAO.getUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        return userDAO.getUserByUsername(username);
    }

    @Override
    public void updateProfile(String userId, String name, String email, String password) throws SQLException {
        if (!hasPermission(userId, Permission.UPDATE_PROFILE)) {
            throw new SQLException("Unauthorized operation");
        }
        userDAO.updateUser(userId, name, email, password);
    }

    @Override
    public User getProfile(String userId) throws SQLException {
        return userDAO.getUserById(userId);
    }

    @Override
    public boolean hasPermission(String userId, Permission permission) throws SQLException {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new SQLException("User not found");
        }
        return user.getRole().getPermissions().contains(permission);
    }
}