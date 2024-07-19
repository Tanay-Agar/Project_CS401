package biblioConnect_v3;

public interface LibraryManagementSystem {
    UserService getUserService();
    BookService getBookService();
    BorrowingService getBorrowingService();
}