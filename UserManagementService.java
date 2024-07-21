package biblioConnect_v3;

import java.util.List;

public interface UserManagementService {
    void registerUser(String name, String email, String username, String password, UserRole role);
    void updateUser(String userId, String name, String email, String username, String password);
    User getUser(String username);
    boolean authenticateUser(String username, String password);
    boolean hasPermission(String username, String action);
    List<User> listAllMembers();
    void removeMember(String userId);
}