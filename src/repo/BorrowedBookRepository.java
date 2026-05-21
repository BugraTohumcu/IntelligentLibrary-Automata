package repo;


import model.BorrowedBook;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simulates the database layer for managing active borrowed books per user.
 */
public class BorrowedBookRepository {
    
    // Maps a User ID (e.g., "admin") to their list of currently borrowed books
    private final Map<String, List<BorrowedBook>> activeBorrowings;

    public BorrowedBookRepository() {
        this.activeBorrowings = new HashMap<>();
        
        // Seeding dummy data for the user "admin"
        List<BorrowedBook> adminBooks = new ArrayList<>();
        adminBooks.add(new BorrowedBook("Clean Code by Robert C. Martin", false)); // Not overdue
        adminBooks.add(new BorrowedBook("Introduction to Automata Theory", true)); // OVERDUE!
        
        activeBorrowings.put("admin", adminBooks);
    }

    /**
     * Checks if the user currently holds any borrowed books.
     */
    public boolean hasBorrowedBooks(String userId) {
        return activeBorrowings.containsKey(userId) && !activeBorrowings.get(userId).isEmpty();
    }

    /**
     * Retrieves the list of borrowed books for a specific user.
     */
    public List<BorrowedBook> getBooksForUser(String userId) {
        return activeBorrowings.getOrDefault(userId, new ArrayList<>());
    }

    /**
     * Simulates removing the book from the user's active list upon return.
     */
    public void returnBook(String userId, BorrowedBook book) {
        if (activeBorrowings.containsKey(userId)) {
            activeBorrowings.get(userId).remove(book);
        }
    }

    /**
     * Adds a newly borrowed book to the user's active list.
     */
    public void addBook(String userId, BorrowedBook book) {
        // If the user doesn't have a list yet, create one
        activeBorrowings.putIfAbsent(userId, new ArrayList<>());
        // Add the new book to their list
        activeBorrowings.get(userId).add(book);
    }
}