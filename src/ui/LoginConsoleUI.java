package ui;

import login.*;
import repo.UserRepository;
import java.util.Scanner;

public class LoginConsoleUI {

    private String loggedInUserId;
    private final LoginAutomaton automaton;
    private final UserRepository userRepository; 
    private final Scanner scanner;
    private String temporaryIdEntered; 

    public LoginConsoleUI(UserRepository userRepository) {
        this.automaton = new LoginAutomaton();
        this.userRepository = userRepository; 
        this.scanner = new Scanner(System.in);
    }

    public boolean startLoginLoop() {
        System.out.println("\n=========================================");
        System.out.println("         === LIBRARY SYSTEM LOGIN ===    ");
        System.out.println("=========================================");

        // Senin enumundaki isAccept() metodunu kontrol ediyoruz
        while (!automaton.getCurrentState().isAccept()) {
            LoginState state = automaton.getCurrentState();
            System.out.println("\n[CURRENT STATE]: " + state.name() + " -> " + state.getConsoleMessage());

            switch (state) {
                case Q0_ID_WAITING:
                    System.out.print("Enter your User ID: ");
                    temporaryIdEntered = scanner.nextLine().trim();
                    
                    // Otomatta Q0 -> Q1 geçişini tetikle
                    automaton.transition(LoginInput.ENTER_ID); 
                    handleIdValidation(temporaryIdEntered);
                    break;

                case Q2_PASSWORD_WAITING:
                    System.out.print("Enter your Password: ");
                    String password = scanner.nextLine().trim();
                    
                    // Otomatta Q2 -> Q3 geçişini tetikle
                    automaton.transition(LoginInput.ENTER_PASSWORD);
                    handlePasswordValidation(temporaryIdEntered, password);
                    break;

                default:
                    // Q1 ve Q3 gibi doğrulama (ara) durumları otomatik işlendiği için UI burada bekleme yapmaz
                    break;
            }
        }

        // BAŞARILI BİTİŞ DURUMU GÖSTERİMİ (Accept / Completion Result)
        System.out.println("\n=========================================");
        System.out.println("[FINAL STATE REACHED]: " + automaton.getCurrentState().name());
        System.out.println("[RESULT]: ACCEPT! " + automaton.getCurrentState().getConsoleMessage());
        System.out.println("=========================================");
        
        this.loggedInUserId = temporaryIdEntered;
        return true;
    }

    private void handleIdValidation(String id) {
        System.out.println("\n[CURRENT STATE]: " + automaton.getCurrentState().name() + " -> " + automaton.getCurrentState().getConsoleMessage());
        
        if (userRepository.isValidId(id)) {
            System.out.println("-> [SUCCESS]: ID found in database records.");
            automaton.transition(LoginInput.ID_VALID); // Q1 -> Q2
        } else {
            // HOCANIN İSTEDİĞİ UYGUN HATA MESAJI (Appropriate message for invalid input)
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("[ERROR]: The entered User ID '" + id + "' does not exist!");
            System.out.println("[CANCELLATION/RESET]: Resetting automaton back to initial state.");
            System.out.println("-------------------------------------------------------------");
            automaton.transition(LoginInput.ID_INVALID); // Q1 -> Q0'a geri fırlatır
        }
    }

    private void handlePasswordValidation(String id, String password) {
        System.out.println("\n[CURRENT STATE]: " + automaton.getCurrentState().name() + " -> " + automaton.getCurrentState().getConsoleMessage());
        
        if (userRepository.isValidPassword(id, password)) {
            System.out.println("-> [SUCCESS]: Password matches system credentials.");
            automaton.transition(LoginInput.PASSWORD_VALID); // Q3 -> Q4 (Accept State)
        } else {
            // HOCANIN İSTEDİĞİ REJECT / ERROR MESAJI
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("[REJECT]: Incorrect password entered for user '" + id + "'!");
            System.out.println("[RETRY]: Returning to password input state.");
            System.out.println("-------------------------------------------------------------");
            automaton.transition(LoginInput.PASSWORD_INVALID); // Q3 -> Q2'ye geri fırlatır
        }
    }

    public String getLoggedInUserId() {
        return loggedInUserId;
    }
}