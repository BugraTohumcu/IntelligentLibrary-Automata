package model;

/**
 * Represents a book currently held by a user.
 * Tracks the title and whether the return date has passed (overdue).
 */
public class BorrowedBook {
    private final String title;
    private boolean isOverdue;

    public BorrowedBook(String title, boolean isOverdue) {
        this.title = title;
        this.isOverdue = isOverdue;
    }

    public String getTitle() {
        return title;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }
}