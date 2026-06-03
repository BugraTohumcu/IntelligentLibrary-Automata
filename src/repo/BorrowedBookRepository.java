package repo;

import model.BorrowedBook;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simulates the database layer for managing active borrowed books per user.
 */
public class BorrowedBookRepository {

    private final Map<String, List<BorrowedBook>> activeBorrowings;

    public BorrowedBookRepository() {
        this.activeBorrowings = new HashMap<>();

        // Seeding dummy data for the user "admin"
        List<BorrowedBook> adminBooks = new ArrayList<>();

        // Borrowed today → valid (due in 14 days)
        adminBooks.add(new BorrowedBook("Clean Code by Robert C. Martin", LocalDate.now()));

        // Borrowed 20 days ago → OVERDUE (14-day period has passed)
        adminBooks.add(new BorrowedBook("Introduction to Automata Theory", LocalDate.now().minusDays(20)));

        activeBorrowings.put("admin", adminBooks);
    }

    public boolean hasBorrowedBooks(String userId) {
        return activeBorrowings.containsKey(userId) && !activeBorrowings.get(userId).isEmpty();
    }

    public List<BorrowedBook> getBooksForUser(String userId) {
        return activeBorrowings.getOrDefault(userId, new ArrayList<>());
    }

    public void returnBook(String userId, BorrowedBook book) {
        if (activeBorrowings.containsKey(userId)) {
            activeBorrowings.get(userId).remove(book);
        }
    }

    /**
     * Adds a newly borrowed book with today as the borrow date.
     */
    public void addBook(String userId, BorrowedBook book) {
        activeBorrowings.putIfAbsent(userId, new ArrayList<>());
        activeBorrowings.get(userId).add(book);
    }
}