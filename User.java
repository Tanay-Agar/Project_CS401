package biblioConnect_v3;

import java.security.SecureRandom;

public class User {
    private String userId;
    private String name;
    private String email;
    private String username;
    private String password;
    private UserRole role;

    public User(String name, String email, String username, String password, UserRole role) {
        this(generateUserId(), name, email, username, password, role);
    }

    public User(String userId, String name, String email, String username, String password, UserRole role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    private static String generateUserId() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(6);
        String alphaNumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 6; i++) {
            sb.append(alphaNumeric.charAt(random.nextInt(alphaNumeric.length())));
        }
        return sb.toString();
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UserRole getRole() { return role; }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}
