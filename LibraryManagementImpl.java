package biblioConnect_v3;

public class LibraryManagementImpl implements LibraryManagementSystem {
    private UserManagementService userManagementService;
    private LibraryManagementService libraryManagementService;

    public LibraryManagementImpl() {
        this.userManagementService = new UserManagementServiceImpl();
        this.libraryManagementService = new LibraryManagementServiceImpl(this.userManagementService);
    }

    @Override
    public UserManagementService getUserManagementService() {
        return userManagementService;
    }

    @Override
    public LibraryManagementService getLibraryManagementService() {
        return libraryManagementService;
    }

    @Override
    public void close() {
        // Implement any necessary cleanup here
        System.out.println("Closing LibraryManagementImpl...");
    }
}