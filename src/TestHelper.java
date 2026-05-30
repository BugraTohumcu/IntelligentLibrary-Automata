import java.util.Objects;

public class TestHelper {
    private static int passCount = 0;
    private static int failCount = 0;

    public static void assertEquals(Object expected, Object actual, String testName) {
        boolean match = Objects.equals(expected, actual);
        if (match) {
            passCount++;
            System.out.println("[PASS] " + testName);
        } else {
            failCount++;
            System.out.println("[FAIL] " + testName + " | Expected: " + expected + ", Actual: " + actual);
        }
    }

    public static void assertTrue(boolean condition, String testName) {
        if (condition) {
            passCount++;
            System.out.println("[PASS] " + testName);
        } else {
            failCount++;
            System.out.println("[FAIL] " + testName + " | Expected: true, Actual: false");
        }
    }

    public static void assertFalse(boolean condition, String testName) {
        if (!condition) {
            passCount++;
            System.out.println("[PASS] " + testName);
        } else {
            failCount++;
            System.out.println("[FAIL] " + testName + " | Expected: false, Actual: true");
        }
    }

    public static void assertNull(Object actual, String testName) {
        if (actual == null) {
            passCount++;
            System.out.println("[PASS] " + testName);
        } else {
            failCount++;
            System.out.println("[FAIL] " + testName + " | Expected: null, Actual: " + actual);
        }
    }

    public static void assertNotNull(Object actual, String testName) {
        if (actual != null) {
            passCount++;
            System.out.println("[PASS] " + testName);
        } else {
            failCount++;
            System.out.println("[FAIL] " + testName + " | Expected: non-null, Actual: null");
        }
    }

    public static void printSummary() {
        System.out.println("\n========================================");
        System.out.println("Total: " + (passCount + failCount) + " | Passed: " + passCount + " | Failed: " + failCount);
        if (failCount == 0) {
            System.out.println("ALL TESTS PASSED!");
        } else {
            System.out.println("SOME TESTS FAILED!");
        }
        System.out.println("========================================");
    }
}
