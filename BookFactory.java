package biblioConnect_v3;

import java.util.Map;

public class BookFactory {
    public static Book createBook(String bookType, String bookId, String title, String author, String isbn, Map<String, String> additionalInfo) {
        switch (bookType.toLowerCase()) {
            case "physical":
                return new PhysicalBook(bookId, title, author, isbn, additionalInfo.get("location"));
            case "ebook":
                return new EBook(bookId, title, author, isbn, 
                                 additionalInfo.get("format"), 
                                 Integer.parseInt(additionalInfo.get("fileSize")), 
                                 additionalInfo.get("downloadLink"));
            case "audiobook":
                return new Audiobook(bookId, title, author, isbn, 
                                     additionalInfo.get("format"), 
                                     Integer.parseInt(additionalInfo.get("fileSize")), 
                                     additionalInfo.get("downloadLink"), 
                                     additionalInfo.get("narrator"), 
                                     Integer.parseInt(additionalInfo.get("duration")));
            default:
                throw new IllegalArgumentException("Unknown book type: " + bookType);
        }
    }
}