package biblioConnect_v3;

public class EBook extends Book {
    private String format;
    private int fileSize;
    private String downloadLink;

    public EBook(String bookId, String title, String author, String isbn, String format, int fileSize, String downloadLink) {
        super(bookId, title, author, isbn, "EBook");
        this.format = format;
        this.fileSize = fileSize;
        this.downloadLink = downloadLink;
    }

    public String getFormat() { return format; }
    public int getFileSize() { return fileSize; }
    public String getDownloadLink() { return downloadLink; }

    @Override
    public String getDetails() {
        return "EBook - Format: " + format + ", Size: " + fileSize + "KB, Download: " + downloadLink;
    }
}