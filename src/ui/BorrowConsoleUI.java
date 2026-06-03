package ui;

import borrow.*;
import model.Book;
import model.BorrowedBook;
import repo.BookRepository;
import repo.BorrowedBookRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class BorrowConsoleUI {
    private final BorrowAutomaton automaton = new BorrowAutomaton();
    private final BookRepository bookRepository;
    private final Scanner scanner = new Scanner(System.in);
    private final String activeUserId;
    private final BorrowedBookRepository userRepo;

    public BorrowConsoleUI(String activeUserId, BorrowedBookRepository userRepo, BookRepository bookRepository) {
        this.activeUserId = activeUserId;
        this.userRepo = userRepo;
        this.bookRepository = bookRepository;
    }

    public void startBorrowLoop() {
        boolean exitStage = false;

        while (!exitStage) {
            BorrowState state = automaton.getCurrentState();

            switch (state) {
                case Q0_IDLE:
                    System.out.println("\n=========================================");
                    System.out.println("[CURRENT STATE]: " + state.name() + " -> " + state.getConsoleMessage());
                    System.out.println("=========================================");
                    System.out.println("=== BOOK SEARCH & BORROW SYSTEM ===");
                    System.out.print("Enter book name to search (or type 'exit' to go back): ");
                    String query = scanner.nextLine().trim();

                    if (query.equalsIgnoreCase("exit")) {
                        exitStage = true;
                    } else {
                        
                        if (hasUnpaidPenalty()) {
                            System.out.println("\n-------------------------------------------------------------");
                            System.out.println("[ACCESS DENIED]: Outstanding penalty detected on your account.");
                            System.out.println("Please return your overdue book(s) and pay the fine first.");
                            System.out.println("Use the 'Returns & Renewals' menu to settle your balance.");
                            System.out.println("-------------------------------------------------------------");
                        } else {
                            automaton.transition(BorrowInput.INPUT_SEARCH);
                            handleBookSearch(query);
                        }
                    }
                    break;

                case Q2_BOOK_NOT_FOUND:
                    System.out.println("\n-------------------------------------------------------------");
                    System.out.println("[FINAL STATE REACHED]: " + state.name());
                    System.out.println("[RESULT]: ERROR / SEARCH FAILED! " + state.getConsoleMessage());
                    System.out.println("-------------------------------------------------------------");
                    System.out.println("Press Enter to return to the initial search screen...");
                    scanner.nextLine();
                    automaton.transition(BorrowInput.GO_BACK_TO_IDLE);
                    break;

                case Q4_BORROWED:
                    System.out.println("\n=========================================");
                    System.out.println("[FINAL STATE REACHED]: " + state.name());
                    System.out.println("[RESULT]: ACCEPT / COMPLETION! " + state.getConsoleMessage());
                    System.out.println("=========================================");
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    automaton.transition(BorrowInput.GO_BACK_TO_IDLE);
                    break;

                case Q5_REJECTED:
                    System.out.println("\n-------------------------------------------------------------");
                    System.out.println("[FINAL STATE REACHED]: " + state.name());
                    System.out.println("[RESULT]: REJECT / DENIED! " + state.getConsoleMessage());
                    System.out.println("-------------------------------------------------------------");
                   
                    System.out.print("Would you like to reserve this book for when it becomes available? (yes/no): ");
                    String reserveAnswer = scanner.nextLine().trim().toLowerCase();
                    if (reserveAnswer.equals("yes")) {
                        automaton.transition(BorrowInput.REQUEST_RESERVATION);
                    } else {
                        System.out.println("Press Enter to recover and return to search...");
                        scanner.nextLine();
                        automaton.transition(BorrowInput.GO_BACK_TO_IDLE);
                    }
                    break;

                case Q6_RESERVED:
                    System.out.println("\n=========================================");
                    System.out.println("[FINAL STATE REACHED]: " + state.name());
                    System.out.println("[RESULT]: ACCEPT / RESERVATION CONFIRMED! " + state.getConsoleMessage());
                    System.out.println("=========================================");
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    automaton.transition(BorrowInput.GO_BACK_TO_IDLE);
                    break;

                case Q7_BOOK_LOST:
                    System.out.println("\n-------------------------------------------------------------");
                    System.out.println("[FINAL STATE REACHED]: " + state.name());
                    System.out.println("[RESULT]: UNAVAILABLE! " + state.getConsoleMessage());
                    System.out.println("-------------------------------------------------------------");
                    System.out.println("Press Enter to return to the main menu...");
                    scanner.nextLine();
                    automaton.transition(BorrowInput.GO_BACK_TO_IDLE);
                    break;

                default:
                    break;
            }
        }
    }

    private void handleBookSearch(String query) {

        System.out.println("\n[CURRENT STATE]: " + automaton.getCurrentState().name() + " -> " + automaton.getCurrentState().getConsoleMessage());
        List<Book> foundBooks = bookRepository.searchBooks(query);

        if (foundBooks.isEmpty()) {
            automaton.transition(BorrowInput.BOOK_NOT_FOUND);
        } else {
            automaton.transition(BorrowInput.BOOK_FOUND);
            presentAvailabilityMenu(foundBooks);
        }
    }
    private boolean hasUnpaidPenalty() {
        return userRepo.getBooksForUser(activeUserId)
                .stream()
                .anyMatch(BorrowedBook::isOverdue);
    }

    private void presentAvailabilityMenu(List<Book> books) {
        System.out.println("\n[CURRENT STATE]: " + automaton.getCurrentState().name() + " -> " + automaton.getCurrentState().getConsoleMessage());
        System.out.println("\n--- Search Results Found ---");
        for (int i = 0; i < books.size(); i++) {
            Book currentBook = books.get(i);
            String status = "";
            if(currentBook.isLost()) status = "Lost";
            else status = currentBook.isAvailable() ? "Available" : "Taken";
            
            System.out.println((i + 1) + " - " + currentBook.getTitle() + " - [" + status + "]");
        }

        System.out.print("\nSelect book number to borrow (or '0' to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) {
                System.out.println("\n[RESULT]: CANCELLATION! Operation terminated by user.");
                automaton.transition(BorrowInput.GO_BACK_TO_IDLE);
                return;
            }

            if (choice < 0 || choice > books.size()) {
                System.out.println("\n-------------------------------------------------------------");
                System.out.println("[ERROR]: Out of bounds selection. Choice matches no library item.");
                System.out.println("-------------------------------------------------------------");
                automaton.transition(BorrowInput.BORROW_REJECTED);
                return;
            }

            Book selectedBook = books.get(choice - 1);
            
            if (selectedBook.isLost()) {
                automaton.transition(BorrowInput.BOOK_IS_LOST);
            } else if (selectedBook.isAvailable()) {
                selectedBook.setAvailable(false);
                Book actualBook = bookRepository.findBookByTitle(selectedBook.getTitle());
                if (actualBook != null) {
                    actualBook.setAvailable(false);
                }
                userRepo.addBook(activeUserId, new BorrowedBook(selectedBook.getTitle(), LocalDate.now()));
                automaton.transition(BorrowInput.CONFIRM_BORROW);
            } else {
                automaton.transition(BorrowInput.BORROW_REJECTED);
            }

        } catch (NumberFormatException e) {
            
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("[ERROR]: Erroneous input format detected! Alphabetic strings are not allowed.");
            System.out.println("-------------------------------------------------------------");
            automaton.transition(BorrowInput.BORROW_REJECTED);
        }
    }
}