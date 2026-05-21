package repo;


import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    // Simulating a database table with an in-memory key-value store (ID -> Password)
    private final Map<String, String> userDatabase;

    public UserRepository() {
        this.userDatabase = new HashMap<>();
        // Seeding dummy user credentials into our local repository
        userDatabase.put("admin", "1234");
        userDatabase.put("student123", "123");
    }

    /**
     * Checks if the given user ID exists in the local database records.
     */
    public boolean isValidId(String id) {
        return userDatabase.containsKey(id);
    }

    /**
     * Checks if the provided password matches the stored credentials for the given ID.
     */
    public boolean isValidPassword(String id, String password) {
        if (!isValidId(id)) {
            return false;
        }
        return userDatabase.get(id).equals(password);
    }
}
