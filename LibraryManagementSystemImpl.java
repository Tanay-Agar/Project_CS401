package biblioConnect_v3;

public class LibraryManagementSystemImpl implements LibraryManagementSystem {
    private UserService userService = new UserServiceImpl();
    private BookService bookService = new BookServiceImpl();
    private BorrowingService borrowingService = new BorrowingServiceImpl();

    @Override
    public UserService getUserService() {
        return userService;
    }

    @Override
    public BookService getBookService() {
        return bookService;
    }

    @Override
    public BorrowingService getBorrowingService() {
        return borrowingService;
    }
}