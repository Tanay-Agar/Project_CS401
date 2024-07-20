package biblioConnect_v3;

public class AudioBook extends Book {
    private String format;
    private int fileSize;
    private String downloadLink;
    private String narrator;
    private int duration;

    public AudioBook(String title, String author, String isbn, String format, int fileSize, String downloadLink, String narrator, int duration) {
        super(title, author, isbn, "AudioBook");
        this.format = format;
        this.fileSize = fileSize;
        this.downloadLink = downloadLink;
        this.narrator = narrator;
        this.duration = duration;
    }

    public String getFormat() { return format; }
    public int getFileSize() { return fileSize; }
    public String getDownloadLink() { return downloadLink; }
    public String getNarrator() { return narrator; }
    public int getDuration() { return duration; }

    @Override
    public String getDetails() {
        return "AudioBook - Format: " + format + ", Size: " + fileSize + "KB, Download: " + downloadLink + 
               ", Narrator: " + narrator + ", Duration: " + duration + " minutes";
    }
}
