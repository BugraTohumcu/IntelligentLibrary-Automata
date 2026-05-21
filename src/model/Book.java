package model;

public class Book {
    private final String title;
    private boolean isAvailable;

    public Book(String title, boolean isAvailable) {
        this.title = title;
        this.isAvailable = isAvailable;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}