package model;

public class Book {
    private final String title;
    private boolean isAvailable;
    private boolean isLost;

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

    public boolean isLost(){
        return this.isLost;
    }

    public void setLost(boolean lost){
        this.isLost = lost;
    }
}