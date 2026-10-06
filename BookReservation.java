/**
 * BookReservation class representing a book hold/reservation in the library.
 * Demonstrates encapsulation, data modeling, and file persistence.
 */
public class BookReservation {
    private String reservationId;
    private String bookId;
    private String memberId;
    private String reservationDate;
    private String status; // PENDING, FULFILLED, CANCELLED

    // Constructor for new reservation
    public BookReservation(String reservationId, String bookId, String memberId, String reservationDate) {
        this.reservationId = reservationId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.reservationDate = reservationDate;
        this.status = "PENDING";
    }

    // Constructor for loading from file
    public BookReservation(String reservationId, String bookId, String memberId, 
                           String reservationDate, String status) {
        this.reservationId = reservationId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.reservationDate = reservationDate;
        this.status = status;
    }

    // Getters
    public String getReservationId() { return reservationId; }
    public String getBookId() { return bookId; }
    public String getMemberId() { return memberId; }
    public String getReservationDate() { return reservationDate; }
    public String getStatus() { return status; }

    // Setters
    public void setStatus(String status) { this.status = status; }

    // Status check and update methods
    public boolean isActive() {
        return "PENDING".equalsIgnoreCase(this.status);
    }

    public void fulfill() {
        this.status = "FULFILLED";
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    // Convert to file format (CSV line)
    public String toFileString() {
        return reservationId + "," + bookId + "," + memberId + "," + reservationDate + "," + status;
    }

    // Create BookReservation from file string
    public static BookReservation fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        if (parts.length >= 5) {
            String resId = parts[0].trim();
            String bId = parts[1].trim();
            String mId = parts[2].trim();
            String date = parts[3].trim();
            String stat = parts[4].trim();
            return new BookReservation(resId, bId, mId, date, stat);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("Reservation: %s | Book ID: %s | Member ID: %s | Date: %s | Status: %s",
                reservationId, bookId, memberId, reservationDate, status);
    }

    // Display detailed info
    public void displayDetails() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("🔖 RESERVATION DETAILS");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Reservation ID   : " + reservationId);
        System.out.println("Book ID          : " + bookId);
        System.out.println("Member ID        : " + memberId);
        System.out.println("Reservation Date : " + reservationDate);
        System.out.println("Status           : " + status);
        System.out.println("═══════════════════════════════════════");
    }
}
