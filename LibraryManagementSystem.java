package biblioConnect_v3;

public interface LibraryManagementSystem extends AutoCloseable {
    UserManagementService getUserManagementService();
    LibraryManagementService getLibraryManagementService();
}
