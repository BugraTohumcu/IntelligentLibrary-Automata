import borrow.*;
import login.*;
import return_renew.*;
import model.*;
import repo.*;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        System.out.println("\n========== Running 10 Tests ==========\n");

        // --- Test 1: BorrowAutomaton initial state ---
        BorrowAutomaton borrowAuto = new BorrowAutomaton();
        TestHelper.assertEquals(BorrowState.Q0_IDLE, borrowAuto.getCurrentState(),
            "Test 1: BorrowAutomaton initial stateis Q0_IDLE");

        // --- Test 2: BorrowAutomaton full happy path ---
        borrowAuto = new BorrowAutomaton();
        borrowAuto.transition(BorrowInput.INPUT_SEARCH);
        borrowAuto.transition(BorrowInput.BOOK_FOUND);
        borrowAuto.transition(BorrowInput.CONFIRM_BORROW);
        TestHelper.assertEquals(BorrowState.Q4_BORROWED, borrowAuto.getCurrentState(),
            "Test 2: BorrowAutomaton full flow IDLE->SEARCHING->AVAILABILITY->BORROWED");

        // --- Test 3: LoginAutomaton successful login ---
        LoginAutomaton loginAuto = new LoginAutomaton();
        loginAuto.transition(LoginInput.ENTER_ID);
        loginAuto.transition(LoginInput.ID_VALID);
        loginAuto.transition(LoginInput.ENTER_PASSWORD);
        loginAuto.transition(LoginInput.PASSWORD_VALID);
        TestHelper.assertEquals(LoginState.Q4_LOGIN_SUCCESS, loginAuto.getCurrentState(),
            "Test 3: LoginAutomaton full success flow reaches Q4_LOGIN_SUCCESS");
        TestHelper.assertTrue(loginAuto.getCurrentState().isAccept(),
            "Test 3b: Q4_LOGIN_SUCCESS is accept state");

        // --- Test 4: LoginAutomaton invalid ID resets to waiting ---
        loginAuto = new LoginAutomaton();
        loginAuto.transition(LoginInput.ENTER_ID);
        loginAuto.transition(LoginInput.ID_INVALID);
        TestHelper.assertEquals(LoginState.Q0_ID_WAITING, loginAuto.getCurrentState(),
            "Test 4: LoginAutomaton invalid ID returns to Q0_ID_WAITING");

        // --- Test 5: ReturnRenewAutomaton no books -> exit accept state ---
        ReturnRenewAutomaton rrAuto = new ReturnRenewAutomaton();
        rrAuto.transition(ReturnRenewInput.BORROW_NOT_FOUND);
        TestHelper.assertEquals(ReturnRenewState.Q1_EXIT_SYSTEM, rrAuto.getCurrentState(),
            "Test 5: ReturnRenewAutomaton no books reaches Q1_EXIT_SYSTEM");
        TestHelper.assertTrue(rrAuto.getCurrentState().isAcceptState(),
            "Test 5b: Q1_EXIT_SYSTEM is accept state");

        // --- Test 6: ReturnRenewAutomaton return non-overdue book ---
        rrAuto = new ReturnRenewAutomaton();
        rrAuto.transition(ReturnRenewInput.BORROW_FOUND);
        rrAuto.transition(ReturnRenewInput.NOT_OVERDUE);
        rrAuto.transition(ReturnRenewInput.EXIT_OR_RETURN);
        TestHelper.assertEquals(ReturnRenewState.Q1_EXIT_SYSTEM, rrAuto.getCurrentState(),
            "Test 6: ReturnRenewAutomaton return non-overdue reaches exit");

        // --- Test 7: Book model constructor and toggle ---
        Book book = new Book("The Hobbit", true);
        TestHelper.assertEquals("The Hobbit", book.getTitle(),
            "Test 7a: Book title matches constructor");
        TestHelper.assertTrue(book.isAvailable(),
            "Test 7b: Book initially available");
        book.setAvailable(false);
        TestHelper.assertFalse(book.isAvailable(),
            "Test 7c: Book availability toggled to false");

        // --- Test 8: BookRepository search case-insensitive and partial ---
        BookRepository bookRepo = new BookRepository();
        List<Book> results = bookRepo.searchBooks("lord");
        TestHelper.assertEquals(2, results.size(),
            "Test 8a: Partial case-insensitive search finds 2 Lord of the Rings");
        results = bookRepo.searchBooks("HARRY POTTER");
        TestHelper.assertEquals(1, results.size(),
            "Test 8b: Uppercase search finds Harry Potter");
        Book found = bookRepo.findBookByTitle("the hobbit");
        TestHelper.assertNotNull(found,
            "Test 8c: Case-insensitive findBookByTitle returns book");

        // --- Test 9: UserRepository credentials ---
        UserRepository userRepo = new UserRepository();
        TestHelper.assertTrue(userRepo.isValidId("admin"),
            "Test 9a: Valid ID 'admin' recognized");
        TestHelper.assertFalse(userRepo.isValidId("hacker"),
            "Test 9b: Invalid ID rejected");
        TestHelper.assertTrue(userRepo.isValidPassword("student123", "123"),
            "Test 9c: Valid password for student123 accepted");
        TestHelper.assertFalse(userRepo.isValidPassword("admin", "wrong"),
            "Test 9d: Wrong password rejected");

        // --- Test 10: BorrowedBookRepository add and return ---
        BorrowedBookRepository borrowedRepo = new BorrowedBookRepository();
        int initialCount = borrowedRepo.getBooksForUser("admin").size();
        TestHelper.assertEquals(2, initialCount,
            "Test 10a: Admin starts with 2 borrowed books");
        BorrowedBook newBook = new BorrowedBook("Test Book", false);
        borrowedRepo.addBook("admin", newBook);
        TestHelper.assertEquals(3, borrowedRepo.getBooksForUser("admin").size(),
            "Test 10b: Admin has 3 books after add");
        borrowedRepo.returnBook("admin", newBook);
        TestHelper.assertEquals(2, borrowedRepo.getBooksForUser("admin").size(),
            "Test 10c: Admin back to 2 books after return");

        TestHelper.printSummary();
    }
}
