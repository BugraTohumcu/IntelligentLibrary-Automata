package ui;

import return_renew.*;
import model.Book;
import model.BorrowedBook;
import repo.BookRepository;
import repo.BorrowedBookRepository;
import java.util.List;
import java.util.Scanner;

/**
 * Controller-tier orchestrator managing user streams and updating context parameters.
 * Synchronizes the shared database instances with the automaton structural tracking.
 */
public class ReturnRenewConsoleUI {

    private final ReturnRenewAutomaton automaton;
    private final BorrowedBookRepository borrowedRepo;
    private final BookRepository bookRepo;
    private final Scanner scanner;
    private final String activeUserId;

    public ReturnRenewConsoleUI(String activeUserId, BorrowedBookRepository borrowedRepo, BookRepository bookRepo) {
        this.automaton = new ReturnRenewAutomaton();
        this.borrowedRepo = borrowedRepo;
        this.scanner = new Scanner(System.in);
        this.activeUserId = activeUserId;
        this.bookRepo = bookRepo;
    }

    public void startReturnLoop() {
        System.out.println("\n=========================================");
        System.out.println("   === MY ACCOUNT: RETURNS & RENEWALS === ");
        System.out.println("=========================================");
        BorrowedBook selectedBook = null;

        // Loop continuous evaluation sequence until terminal accept states are hit (Q1 or Q7)
        while (!automaton.getCurrentState().isAcceptState()) {
            ReturnRenewState state = automaton.getCurrentState();
            System.out.println("\n[CURRENT STATE]: " + state.name() + " -> " + state.getConsoleMessage());

            switch (state) {
                case Q0_MAIN_PAGE:
                    if (borrowedRepo.hasBorrowedBooks(activeUserId)) {
                        automaton.transition(ReturnRenewInput.BORROW_FOUND);
                    } else {
                        System.out.println("-> [EMPTY RECORDS]: Account contains no active checked-out items.");
                        automaton.transition(ReturnRenewInput.BORROW_NOT_FOUND);
                    }
                    break;

                case Q2_CHECK_OVERDUE:
                    selectedBook = presentUserBooksAndSelect();
                    if (selectedBook == null) {
                        
                        System.out.println("\n[RESULT]: CANCELLATION! User aborted active target selection.");
                        automaton.transition(ReturnRenewInput.RETURN_MENU);
                    } else {
                        if (selectedBook.isOverdue()) {
                            automaton.transition(ReturnRenewInput.OVERDUE);
                        } else {
                            automaton.transition(ReturnRenewInput.NOT_OVERDUE);
                        }
                    }
                    break;

                case Q3_LATE_PENALTY_DECISION:
                    /*
                     * LATE PENALTY HANDLER:
                     * Overdue books carry a financial penalty that must be settled before return.
                     * User refusing to pay triggers account suspension (Q7).
                     */
                    System.out.println("\n-------------------------------------------------------------");
                    System.out.println("[WARNING / REJECT CONDITION]: Overdue parameters triggered!");
                    System.out.println("A financial penalty is bound to this item. Settlement required.");
                    System.out.println("-------------------------------------------------------------");
                    System.out.print("Type 'pay' to clear charges and check-in, or 'ignore' to pass: ");
                    String penaltyChoice = scanner.nextLine().trim().toLowerCase();

                    if (penaltyChoice.equals("pay")) {
                        borrowedRepo.returnBook(activeUserId, selectedBook);
                        returnBook(selectedBook); // Syncing physical inventory
                        System.out.println("-> Charges settled. Inventory registry cleared.");
                        automaton.transition(ReturnRenewInput.RETURN_BOOK);
                    } else {
                        /*
                         * CRITICAL ERROR/COMPLIANCE HOOK:
                         * Explicit rejection to pay fees routes system to account suspension state.
                         */
                        System.out.println("\n[ERROR]: Payment verification failed or bypassed by user.");
                        automaton.transition(ReturnRenewInput.NOT_RETURN);
                    }
                    break;

                case Q4_RENEWAL_RETURN_MENU:
                    /*
                     * LOST BOOK OPTION:
                     * Extended menu allows reporting a book as lost in addition to return/renew.
                     * Reporting a lost book removes it from the user's account and marks the
                     * library copy permanently unavailable.
                     */
                    System.out.print("Type 'return' to return, 'renew' to extend, or 'lost' to report as lost: ");
                    String action = scanner.nextLine().trim().toLowerCase();

                    if (action.equals("renew")) {
                        automaton.transition(ReturnRenewInput.REQUEST_RENEWAL);
                    } else if (action.equals("return")) {
                        borrowedRepo.returnBook(activeUserId, selectedBook);
                        returnBook(selectedBook); // Syncing physical inventory
                        System.out.println("-> Book processing completed.");
                        automaton.transition(ReturnRenewInput.EXIT_OR_RETURN);
                    } else if (action.equals("lost")) {
                        handleLostBook(selectedBook);
                        automaton.transition(ReturnRenewInput.EXIT_OR_RETURN);
                    } else {
                        System.out.println("\n[ERROR]: Command match failed. Invalid selection input.");
                        // Self loop remains in Q4 implicitly
                    }
                    break;

                case Q5_RENEWAL_APPROVAL:
                    // Random process simulator modeling dynamic backend server criteria approval
                    boolean isApproved = Math.random() > 0.5;

                    if (isApproved) {
                        System.out.println("-> [COMPLETION]: Structural criteria passed. Extension authorized.");
                        automaton.transition(ReturnRenewInput.RENEWAL_ACCEPTED);
                    } else {
                        automaton.transition(ReturnRenewInput.RENEWAL_DENIED);
                    }
                    break;

                case Q6_REJECTED_STATE:
                    System.out.println("\n-------------------------------------------------------------");
                    System.out.println("[RESULT]: REJECT / RENEWAL DENIED! " + state.getConsoleMessage());
                    System.out.println("Safety fallback protocol triggered. Initiating forceful check-in...");
                    System.out.println("-------------------------------------------------------------");
                    borrowedRepo.returnBook(activeUserId, selectedBook);
                    returnBook(selectedBook); // Syncing physical inventory
                    automaton.transition(ReturnRenewInput.EXIT_OR_RETURN);
                    break;

                default:
                    break;
            }
        }

        ReturnRenewState finalState = automaton.getCurrentState();
        System.out.println("\n=========================================");
        System.out.println("[FINAL STATE REACHED]: " + finalState.name());

        if (finalState == ReturnRenewState.Q7_SUSPEND) {
            System.out.println("[RESULT]: REJECT / ACCOUNT SUSPENDED!");
            System.out.println("System configuration Locked. Clearance required via administration desk.");
        } else {
            System.out.println("[RESULT]: ACCEPT / TRANSACTION TERMINATED SUCCESSFULLY.");
        }
        System.out.println("=========================================");
    }

    /**
     * Intercepts user catalog manipulation streams and isolates selection anomalies.
     */
    private BorrowedBook presentUserBooksAndSelect() {
        List<BorrowedBook> books = borrowedRepo.getBooksForUser(activeUserId);
        System.out.println("\n--- Your Borrowed Books ---");
        for (int i = 0; i < books.size(); i++) {
            String status = books.get(i).isOverdue() ? "[OVERDUE]" : "[Valid]";
            System.out.println((i + 1) + ". " + books.get(i).getTitle()
                    + " | Due: " + books.get(i).getDueDate() + " " + status);
        }

        System.out.print("\nSelect the book number to manage (or '0' to exit): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return null;
            if (choice > 0 && choice <= books.size()) return books.get(choice - 1);
            System.out.println("\n[ERROR]: Choice outside valid list indexes.");
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR]: Non-numeric evaluation format submitted.");
        }
        return null;
    }

    private void returnBook(BorrowedBook selectedBook) {
        Book book = bookRepo.findBookByTitle(selectedBook.getTitle());
        if (book != null) {
            book.setAvailable(true);
        }
    }

    /**
     * LOST BOOK HANDLER:
     * Removes the book from the user's active loans and marks the library copy
     * as unavailable (lost — no longer available for borrowing until restocked).
     */
    private void handleLostBook(BorrowedBook lostBook) {
        System.out.println("\n-------------------------------------------------------------");
        System.out.println("[LOST BOOK REPORT]: Marking book as lost: " + lostBook.getTitle());
        System.out.println("-------------------------------------------------------------");

        // Remove from user's borrowed list
        borrowedRepo.returnBook(activeUserId, lostBook);

        // Mark library copy as permanently unavailable (lost, not returned to shelf)
        Book libraryBook = bookRepo.findBookByTitle(lostBook.getTitle());
        if (libraryBook != null) {
            libraryBook.setAvailable(false);
            libraryBook.setLost(true);
        }

        System.out.println("-> Book removed from your account.");
        System.out.println("-> Library inventory updated: copy marked as lost.");
        System.out.println("-> A replacement fee has been applied to your account.");
    }
}