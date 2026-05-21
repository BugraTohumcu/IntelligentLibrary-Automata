import ui.BorrowConsoleUI;
import ui.LoginConsoleUI;
import ui.ReturnRenewConsoleUI;

import java.util.Scanner;

import repo.BookRepository;
import repo.BorrowedBookRepository;
import repo.UserRepository;

public class App {

    private enum AppContext {
        LOGIN_STAGE,
        MAIN_MENU,
        BORROW_STAGE,
        RETURN_RENEW_STAGE,
        EXIT
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AppContext currentContext = AppContext.LOGIN_STAGE;
        String currentUserId = "";
        BorrowedBookRepository userBookDatabase = new BorrowedBookRepository();
        UserRepository userRepository = new UserRepository();
        BookRepository bookRepository = new BookRepository();
        
        System.out.println("=============================================");
        System.out.println("  WELCOME TO THE AUTOMATON LIBRARY SYSTEM   ");
        System.out.println("=============================================");


        // The master loop executing the active UI component
        while (currentContext != AppContext.EXIT) {
            
            switch (currentContext) {
                case LOGIN_STAGE:
                    LoginConsoleUI loginUI = new LoginConsoleUI(userRepository);
                    boolean loginSuccessful = loginUI.startLoginLoop();
                    
                    if (loginSuccessful) {
                        currentContext = AppContext.MAIN_MENU;
                        currentUserId = loginUI.getLoggedInUserId();
                    } else {
                        currentContext = AppContext.EXIT; // Terminate if login fails/aborts
                    }
                    break;

                case MAIN_MENU:
                    currentContext = showMainMenu(scanner);
                    break;

                case BORROW_STAGE:
                    BorrowConsoleUI borrowUI = new BorrowConsoleUI(currentUserId, userBookDatabase, bookRepository);
                    borrowUI.startBorrowLoop(); 
                    
                    currentContext = AppContext.MAIN_MENU; 
                    break;

                case RETURN_RENEW_STAGE:
                    ReturnRenewConsoleUI returnUI = new ReturnRenewConsoleUI(currentUserId, userBookDatabase, bookRepository);
                    returnUI.startReturnLoop();
                    
                    currentContext = AppContext.MAIN_MENU; 
                    break;
                default:
                    break;
            }
        }

        System.out.println("\n=============================================");
        System.out.println(" System shut down gracefully. Goodbye!       ");
        System.out.println("=============================================");
        scanner.close();
    }

    private static AppContext showMainMenu(Scanner scanner) {
        System.out.println("\n=========================================");
        System.out.println("               MAIN MENU                 ");
        System.out.println("=========================================");
        System.out.println("1. Book Search & Borrowing");
        System.out.println("2. My Account (Return / Renew Books)");
        System.out.println("3. Exit System");
        System.out.print("Please enter your choice (1-3): ");
        
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                return AppContext.BORROW_STAGE;
            case "2":
                return AppContext.RETURN_RENEW_STAGE;
            case "3":
                return AppContext.EXIT;
            default:
                System.out.println("Invalid selection! Please enter 1, 2, or 3.");
                return AppContext.MAIN_MENU; // Stay in the menu
        }
    }
}