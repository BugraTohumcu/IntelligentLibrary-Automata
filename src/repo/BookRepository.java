package repo;

import model.Book;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    private final List<Book> bookDatabase;

    public BookRepository() {
        this.bookDatabase = new ArrayList<>();
        // Seeding dummy books data into our local library repository
        // Book title and availability
        bookDatabase.add(new Book("Introduction to Automata Theory", false));
        bookDatabase.add(new Book("Clean Code by Robert C. Martin", false));
        bookDatabase.add(new Book("Harry Potter and the Sorcerer's Stone", true));
        bookDatabase.add(new Book("The Lord of the Rings", false)); 
        bookDatabase.add(new Book("The Lord of the Rings", true)); 
        bookDatabase.add(new Book("American Pyscho", true));
        bookDatabase.add(new Book("Crime and Punishment", false)); 
        bookDatabase.add(new Book("The Hobbit", true));
    }

    /**
     * Performs a case-insensitive search across book titles.
     */
    public List<Book> searchBooks(String query) {
        List<Book> results = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase().trim();

        for (Book book : bookDatabase) {
            // Case-insensitive check using toLowerCase()
            if (book.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                Book bookCopy = new Book(book.getTitle(), book.isAvailable());
                bookCopy.setLost(book.isLost());
                results.add(bookCopy);
            }
        }
        return results;
    }

    /**
     * Finds a book in the library by its exact title.
     */
    public Book findBookByTitle(String title) {
        for (Book book : bookDatabase) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }
}