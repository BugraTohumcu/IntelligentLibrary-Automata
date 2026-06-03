package model;

import java.time.LocalDate;

/**
 * Represents a book currently held by a user.
 * Overdue status is calculated automatically based on a 14-day borrowing period.
 */
public class BorrowedBook {
    private final String title;
    private final LocalDate borrowDate;

    private static final int BORROW_PERIOD_DAYS = 14;

    public BorrowedBook(String title, LocalDate borrowDate) {
        this.title = title;
        this.borrowDate = borrowDate;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return borrowDate.plusDays(BORROW_PERIOD_DAYS);
    }

    /**
     * Automatically calculated — no manual setter needed.
     * Returns true if today is past the due date.
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(getDueDate());
    }
}