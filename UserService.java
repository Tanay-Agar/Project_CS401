package biblioConnect_v3;

import java.sql.SQLException;
import java.util.List;

public interface UserService {
    void registerUser(String name, String email, String username, String password, UserRole role) throws SQLException;
    void removeUser(String librarianId, String userId) throws SQLException;
    User searchUser(String userId) throws SQLException;
    List<User> listAllUsers(String librarianId) throws SQLException;
    boolean verifyLogin(String username, String password) throws SQLException;
    User getUserByUsername(String username) throws SQLException;
    void updateProfile(String userId, String name, String email, String password) throws SQLException;
    User getProfile(String userId) throws SQLException;
    boolean hasPermission(String userId, Permission permission) throws SQLException;
}