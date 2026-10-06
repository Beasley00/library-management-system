import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Library class - Main business logic class
 * Manages books, members, librarians, and reservations with file persistence
 */
public class Library {
    private List<Book> books;
    private List<Member> members;
    private List<Librarian> librarians;
    private List<BookReservation> reservations;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Constructor
    public Library() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.librarians = new ArrayList<>();
        this.reservations = new ArrayList<>();
        loadAllData();
    }
    
    // Load all data from files
    public void loadAllData() {
        books = FileHandler.loadBooks();
        members = FileHandler.loadMembers();
        librarians = FileHandler.loadLibrarians();
        reservations = FileHandler.loadReservations();
        
        System.out.println("📚 Data loaded successfully!");
        System.out.println("Books: " + books.size() + " | Members: " + members.size() + 
                           " | Librarians: " + librarians.size() + " | Reservations: " + reservations.size());
    }
    
    // Save all data to files
    public boolean saveAllData() {
        boolean success = true;
        success &= FileHandler.saveBooks(books);
        success &= FileHandler.saveMembers(members);
        success &= FileHandler.saveLibrarians(librarians);
        success &= FileHandler.saveReservations(reservations);
        
        if (success) {
            System.out.println("✅ All data saved successfully!");
        } else {
            System.out.println("❌ Error saving some data!");
        }
        return success;
    }
    
    // Book Management Methods
    public boolean addBook(Book book) {
        if (findBookById(book.getBookId()) == null) {
            books.add(book);
            saveAllData();
            System.out.println("✅ Book added successfully: " + book.getTitle());
            return true;
        } else {
            System.out.println("❌ Book with ID " + book.getBookId() + " already exists!");
            return false;
        }
    }
    
    public Book findBookById(String bookId) {
        return books.stream()
                .filter(book -> book.getBookId().equals(bookId))
                .findFirst()
                .orElse(null);
    }
    
    public List<Book> searchBooks(String query) {
        String lowerQuery = query.toLowerCase();
        return books.stream()
                .filter(book -> 
                    book.getTitle().toLowerCase().contains(lowerQuery) ||
                    book.getAuthor().toLowerCase().contains(lowerQuery) ||
                    book.getCategory().toLowerCase().contains(lowerQuery) ||
                    book.getBookId().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
    
    public List<Book> getAvailableBooks() {
        return books.stream()
                .filter(book -> !book.isIssued())
                .collect(Collectors.toList());
    }
    
    public List<Book> getIssuedBooks() {
        return books.stream()
                .filter(Book::isIssued)
                .collect(Collectors.toList());
    }
    
    // Member Management Methods
    public boolean addMember(Member member) {
        if (findMemberById(member.getId()) == null) {
            members.add(member);
            saveAllData();
            System.out.println("✅ Member added successfully: " + member.getName());
            return true;
        } else {
            System.out.println("❌ Member with ID " + member.getId() + " already exists!");
            return false;
        }
    }
    
    public Member findMemberById(String memberId) {
        return members.stream()
                .filter(member -> member.getId().equals(memberId))
                .findFirst()
                .orElse(null);
    }
    
    // Librarian Management Methods
    public boolean addLibrarian(Librarian librarian) {
        if (findLibrarianById(librarian.getId()) == null) {
            librarians.add(librarian);
            saveAllData();
            System.out.println("✅ Librarian added successfully: " + librarian.getName());
            return true;
        } else {
            System.out.println("❌ Librarian with ID " + librarian.getId() + " already exists!");
            return false;
        }
    }
    
    public Librarian findLibrarianById(String librarianId) {
        return librarians.stream()
                .filter(librarian -> librarian.getId().equals(librarianId))
                .findFirst()
                .orElse(null);
    }
    
    // Book Issue/Return Methods
    public boolean issueBook(String bookId, String memberId) {
        Book book = findBookById(bookId);
        Member member = findMemberById(memberId);
        
        if (book == null) {
            System.out.println("❌ Book not found with ID: " + bookId);
            return false;
        }
        
        if (member == null) {
            System.out.println("❌ Member not found with ID: " + memberId);
            return false;
        }
        
        if (book.isIssued()) {
            System.out.println("❌ Book is already issued to: " + book.getIssuedTo());
            return false;
        }
        
        if (!member.canIssueMoreBooks()) {
            System.out.println("❌ Member has reached maximum book limit (" + member.getMaxBooksAllowed() + ")");
            return false;
        }
        
        // Calculate dates
        LocalDate issueDate = LocalDate.now();
        LocalDate returnDate = issueDate.plusDays(14); // 2 weeks loan period
        
        // Issue the book
        if (book.issueBook(memberId, issueDate.format(DATE_FORMAT), returnDate.format(DATE_FORMAT))) {
            member.addIssuedBook(bookId);

            // If this member had an active reservation for this book, mark it fulfilled
            for (BookReservation res : reservations) {
                if (res.isActive() && res.getBookId().equals(bookId) && res.getMemberId().equals(memberId)) {
                    res.fulfill();
                    System.out.println("🔖 Reservation " + res.getReservationId() + " marked as FULFILLED.");
                    break;
                }
            }

            saveAllData();
            
            System.out.println("✅ Book issued successfully!");
            System.out.println("📖 Book: " + book.getTitle());
            System.out.println("👤 Member: " + member.getName());
            System.out.println("📅 Issue Date: " + issueDate.format(DATE_FORMAT));
            System.out.println("📅 Return Date: " + returnDate.format(DATE_FORMAT));
            return true;
        }
        
        return false;
    }
    
    public boolean returnBook(String bookId, String memberId) {
        Book book = findBookById(bookId);
        Member member = findMemberById(memberId);
        
        if (book == null) {
            System.out.println("❌ Book not found with ID: " + bookId);
            return false;
        }
        
        if (member == null) {
            System.out.println("❌ Member not found with ID: " + memberId);
            return false;
        }
        
        if (!book.isIssued()) {
            System.out.println("❌ Book is not currently issued!");
            return false;
        }
        
        if (!book.getIssuedTo().equals(memberId)) {
            System.out.println("❌ Book is issued to different member: " + book.getIssuedTo());
            return false;
        }
        
        if (!member.hasIssuedBook(bookId)) {
            System.out.println("❌ Member doesn't have this book issued!");
            return false;
        }
        
        // Return the book
        if (book.returnBook()) {
            member.removeIssuedBook(bookId);
            saveAllData();
            
            System.out.println("✅ Book returned successfully!");
            System.out.println("📖 Book: " + book.getTitle());
            System.out.println("👤 Member: " + member.getName());
            System.out.println("📅 Return Date: " + LocalDate.now().format(DATE_FORMAT));

            // Check if there are active reservations waiting for this book
            List<BookReservation> waiting = getReservationsByBook(bookId).stream()
                    .filter(BookReservation::isActive)
                    .collect(Collectors.toList());
            if (!waiting.isEmpty()) {
                System.out.println("🔔 Note: This book has " + waiting.size() + " active hold/reservation(s) waiting!");
                System.out.println("   Next in line: Member " + waiting.get(0).getMemberId() + 
                                   " (Reservation: " + waiting.get(0).getReservationId() + ")");
            }

            return true;
        }
        
        return false;
    }

    // Reservation Management Methods
    public boolean reserveBook(String bookId, String memberId) {
        Book book = findBookById(bookId);
        Member member = findMemberById(memberId);

        if (book == null) {
            System.out.println("❌ Book not found with ID: " + bookId);
            return false;
        }

        if (member == null) {
            System.out.println("❌ Member not found with ID: " + memberId);
            return false;
        }

        // Check if member already has an active reservation for this book
        boolean alreadyReserved = reservations.stream()
                .anyMatch(r -> r.isActive() && r.getBookId().equals(bookId) && r.getMemberId().equals(memberId));
        if (alreadyReserved) {
            System.out.println("❌ Member already has an active reservation for this book!");
            return false;
        }

        // Check if member currently has the book issued
        if (book.isIssued() && memberId.equals(book.getIssuedTo())) {
            System.out.println("❌ Member already has this book currently checked out!");
            return false;
        }

        String reservationId = "R" + String.format("%03d", reservations.size() + 1);
        String today = LocalDate.now().format(DATE_FORMAT);

        BookReservation reservation = new BookReservation(reservationId, bookId, memberId, today);
        reservations.add(reservation);
        saveAllData();

        System.out.println("✅ Book reserved successfully!");
        System.out.println("🔖 Reservation ID : " + reservationId);
        System.out.println("📖 Book Title      : " + book.getTitle());
        System.out.println("👤 Reserved For   : " + member.getName());
        System.out.println("📅 Reservation Date: " + today);
        if (book.isIssued()) {
            System.out.println("ℹ️  Book is currently checked out to " + book.getIssuedTo() + 
                               " (Due: " + book.getReturnDate() + "). Hold is queued.");
        } else {
            System.out.println("ℹ️  Book is available on shelf for pickup.");
        }
        return true;
    }

    public boolean cancelReservation(String reservationId) {
        BookReservation res = reservations.stream()
                .filter(r -> r.getReservationId().equalsIgnoreCase(reservationId))
                .findFirst()
                .orElse(null);

        if (res == null) {
            System.out.println("❌ Reservation not found with ID: " + reservationId);
            return false;
        }

        if (!res.isActive()) {
            System.out.println("❌ Reservation " + reservationId + " is not active (Status: " + res.getStatus() + ")");
            return false;
        }

        res.cancel();
        saveAllData();
        System.out.println("✅ Reservation " + reservationId + " cancelled successfully.");
        return true;
    }

    public List<BookReservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public List<BookReservation> getActiveReservations() {
        return reservations.stream()
                .filter(BookReservation::isActive)
                .collect(Collectors.toList());
    }

    public List<BookReservation> getReservationsByBook(String bookId) {
        return reservations.stream()
                .filter(r -> r.getBookId().equals(bookId))
                .collect(Collectors.toList());
    }

    public List<BookReservation> getReservationsByMember(String memberId) {
        return reservations.stream()
                .filter(r -> r.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    public void displayAllReservations() {
        if (reservations.isEmpty()) {
            System.out.println("🔖 No reservations recorded in the library.");
            return;
        }

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("🔖 ALL BOOK RESERVATIONS");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Total Reservations: " + reservations.size());
        System.out.println("Active: " + getActiveReservations().size());
        System.out.println("═══════════════════════════════════════");
        for (BookReservation r : reservations) {
            System.out.println(r);
        }
        System.out.println("═══════════════════════════════════════");
    }
    
    // Display Methods
    public void displayAllBooks() {
        if (books.isEmpty()) {
            System.out.println("📚 No books available in the library.");
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("📚 ALL BOOKS IN LIBRARY");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Total Books: " + books.size());
        System.out.println("Available: " + getAvailableBooks().size());
        System.out.println("Issued: " + getIssuedBooks().size());
        System.out.println("═══════════════════════════════════════");
        
        for (Book book : books) {
            System.out.println(book);
        }
        System.out.println("═══════════════════════════════════════");
    }
    
    public void displayAllMembers() {
        if (members.isEmpty()) {
            System.out.println("👥 No members registered in the library.");
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("👥 ALL LIBRARY MEMBERS");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Total Members: " + members.size());
        System.out.println("═══════════════════════════════════════");
        
        for (Member member : members) {
            System.out.println(member + " | Books: " + member.getCurrentBooksCount() + "/" + member.getMaxBooksAllowed());
        }
        System.out.println("═══════════════════════════════════════");
    }
    
    public void displayLibraryStatistics() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("📊 LIBRARY STATISTICS");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Total Books: " + books.size());
        System.out.println("Available Books: " + getAvailableBooks().size());
        System.out.println("Issued Books: " + getIssuedBooks().size());
        System.out.println("Total Members: " + members.size());
        System.out.println("Total Librarians: " + librarians.size());
        System.out.println("Total Reservations: " + reservations.size() + " (Active: " + getActiveReservations().size() + ")");
        
        // Books by category
        System.out.println("\n📚 Books by Category:");
        books.stream()
                .collect(Collectors.groupingBy(Book::getCategory, Collectors.counting()))
                .forEach((category, count) -> System.out.println("  " + category + ": " + count));
        
        // Members by type
        System.out.println("\n👥 Members by Type:");
        members.stream()
                .collect(Collectors.groupingBy(Member::getMembershipType, Collectors.counting()))
                .forEach((type, count) -> System.out.println("  " + type + ": " + count));
        
        System.out.println("═══════════════════════════════════════");
    }
    
    // Getters
    public List<Book> getAllBooks() { return new ArrayList<>(books); }
    public List<Member> getAllMembers() { return new ArrayList<>(members); }
    public List<Librarian> getAllLibrarians() { return new ArrayList<>(librarians); }
    
    // Backup data
    public boolean backupData() {
        return FileHandler.backupData(books, members, librarians, reservations);
    }
}
