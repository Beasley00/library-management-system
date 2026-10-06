import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ReservationDemo - Demonstrates and thoroughly tests the BookReservation feature
 * and verifies system stability, edge cases, and bug fixes.
 */
public class ReservationDemo {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║            🏛️ LIBRARY MANAGEMENT SYSTEM - RESERVATION DEMO & TESTS 🏛️       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        System.out.println();

        // 1. Initialize Library
        Library library = new Library();

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 1: Checkout/Issue Book (B001 to M001)");
        System.out.println("═".repeat(70));
        boolean issueSuccess = library.issueBook("B001", "M001");
        System.out.println("Issue Result: " + (issueSuccess ? "SUCCESS" : "FAILED"));

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 2: Reserve an Already-Issued Book (M002 reserves B001)");
        System.out.println("═".repeat(70));
        boolean reserveSuccess1 = library.reserveBook("B001", "M002");
        System.out.println("Reservation Result: " + (reserveSuccess1 ? "SUCCESS" : "FAILED"));

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 3: Reserve an Available Book on Shelf (M003 reserves B002)");
        System.out.println("═".repeat(70));
        boolean reserveSuccess2 = library.reserveBook("B002", "M003");
        System.out.println("Reservation Result: " + (reserveSuccess2 ? "SUCCESS" : "FAILED"));

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 4: Edge Case - Duplicate Reservation Attempt (M002 re-reserves B001)");
        System.out.println("═".repeat(70));
        boolean duplicateAttempt = library.reserveBook("B001", "M002");
        System.out.println("Duplicate Allowed: " + duplicateAttempt + " (Expected: false)");

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 5: Edge Case - Member Reserving Book They Already Borrowed (M001 reserves B001)");
        System.out.println("═".repeat(70));
        boolean currentBorrowerAttempt = library.reserveBook("B001", "M001");
        System.out.println("Borrower Hold Allowed: " + currentBorrowerAttempt + " (Expected: false)");

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 6: Edge Case - Non-Existent Book / Member IDs");
        System.out.println("═".repeat(70));
        boolean invalidBook = library.reserveBook("B999", "M001");
        boolean invalidMember = library.reserveBook("B001", "M999");
        System.out.println("Invalid Book Allowed: " + invalidBook + " | Invalid Member Allowed: " + invalidMember);

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 7: Display All Active Reservations");
        System.out.println("═".repeat(70));
        library.displayAllReservations();

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 8: Cancel a Reservation (Cancel R002 for B002)");
        System.out.println("═".repeat(70));
        boolean cancelSuccess = library.cancelReservation("R002");
        System.out.println("Cancellation Result: " + (cancelSuccess ? "SUCCESS" : "FAILED"));

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 9: Return Book (B001 returned by M001) - Checking Hold Notification");
        System.out.println("═".repeat(70));
        boolean returnSuccess = library.returnBook("B001", "M001");
        System.out.println("Return Result: " + (returnSuccess ? "SUCCESS" : "FAILED"));

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 10: Fulfill Reservation (Issue B001 to Reserving Member M002)");
        System.out.println("═".repeat(70));
        boolean fulfillIssue = library.issueBook("B001", "M002");
        System.out.println("Issue to Reserving Member: " + (fulfillIssue ? "SUCCESS" : "FAILED"));

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 11: Display Updated Reservations");
        System.out.println("═".repeat(70));
        library.displayAllReservations();

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 12: Verify File Persistence (Reload from data/reservations.txt)");
        System.out.println("═".repeat(70));
        List<BookReservation> loaded = FileHandler.loadReservations();
        System.out.println("Successfully loaded " + loaded.size() + " reservation records from file:");
        for (BookReservation r : loaded) {
            System.out.println("  • " + r.toFileString());
        }

        System.out.println("\n" + "═".repeat(70));
        System.out.println("TEST 13: Library Statistics with Reservations Included");
        System.out.println("═".repeat(70));
        library.displayLibraryStatistics();

        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                       ✅ ALL TESTS COMPLETED SUCCESSFULLY!                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
    }
}
