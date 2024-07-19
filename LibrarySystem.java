package biblioConnect_v3;

public class LibrarySystem {
    private LibraryManagementSystem library;

    public LibrarySystem() {
        this.library = new LibraryManagementSystemImpl();
    }

    public LibraryManagementSystem getLibrary() {
        return library;
    }
}