# Library Management System — Assignment Submission & Documentation

**Name:** Carter Beasley

**Project Option:** Option C — Library Management System

**Repository:** *\[Insert GitHub Repository Link Here\]*

## Part 1: Reading Reflection

The author challenges the Silicon Valley narrative that artificial intelligence has solved coding. Using his experience as an early AI adopter and systems engineer, he makes a distinction between code generation and software engineering.

* Creation vs. Maintenance Costs: Generating code is the initial phase of software lifecycle. In production systems at scale, the majority of engineering costs and technical risks reside in maintainability, security, scalability, resilience, observability, and debugging subtle edge cases.

* Stochastic Models vs. Deterministic Realities: LLMs are inherently pattern-matching engines with "jagged intelligence." While natural language tasks tolerate approximate outputs, code execution is more strict. Models can catch obvious syntax errors, but does not eliminate logical hallucinations or architectural misalignments.

* Human Accountability & Code Comprehension: Code serves as the authoritative source of truth defining how a system behaves. Because an AI cannot be held legally or operationally accountable (it cannot face fines, prison sentences, or career fallout), humans remain entirely liable for generated code. Developers cannot responsibly vouch for or maintain systems they do not deeply understand.

### Which Point Do You Agree With? Why?

**Agreed Point:** Value is rapidly migrating from code generation to code verification, and engineers must retain deep comprehension of the codebase to maintain accountability.

In computer science, writing syntax is rarely the hardest bottleneck. Understanding the problem space, reasoning through corner cases, and maintaining architectural cohesion are. When developers blindly accept AI-generated code without understanding the underlying logic, they will not be able to help collaboration or fix major errors. An engineer who does not know how and why their code executes cannot isolate conditions, memory leaks, or major failures.

### Which Point Do You Disagree With or Question? Why?

**Disagreed Point:** He claims that we are "at least two revolutions away" from AI making reliable contributions to architecture, and his somewhat dismissive stance toward prompt scaffolding, skills, and agentic workflows.

Rather than viewing AI as either an all-knowing architect or a generator of disposable "slop," the most effective engineering teams use AI as an interactive pair programmer that explores alternatives, scaffolds tests, and drafts boilerplate, while human engineers maintain architectural guardrails.


## Part 4: Testing and Documentation

### 1. Description of Your Changes

#### Feature Added and Why

Implemented a Book Reservation System that allows members to place holds on both available and currently issued books. The system prevents duplicate holds and self-holds, alerts librarians when a reserved book is returned, automatically fulfills holds upon checkout, and persists data to data/reservations.txt. This feature was chosen because it introduces a realistic, essential library workflow, integrates cleanly with existing domain objects without violating Single Responsibility, and demonstrates complex state management (PENDING, FULFILLED, CANCELLED).

#### Classes Modified and Added

* BookReservation.java (New): Domain model tracking reservation IDs, book/member IDs, dates, and status. Contains methods for state transitions (fulfill(), cancel()).

* ReservationDemo.java (New): Standalone automated integration suite covering 13 distinct scenarios.

* Library.java (Modified): Added reservation lists and lifecycle methods (reserveBook(), cancelReservation()). Updated issueBook() to auto-fulfill holds and returnBook() to trigger queue alerts.

* FileHandler.java (Modified): Added serialization, deserialization, and backup logic for reservations.txt.

* LibraryManagementApp.java (Modified): Added reservation menu options and patched a critical UI bug by dynamically computing box widths in printDataBox().

#### How the New Class is Used

When a patron reserves a book, Library.reserveBook() validates the IDs and checks for active duplicates. A unique BookReservation object is instantiated, appended to the Library's internal list, and immediately written to data/reservations.txt by the FileHandler. On application startup, the file is parsed back into memory.

### 2. Impact Analysis

#### Certainty in Functionality

The core architecture changes are strictly additive. No existing domain variables or method signatures were altered. Additionally, modifying LibraryManagementApp.java actively fixed an existing IllegalArgumentException crash, thereby improving stability.

#### Testing Performed

* 13 scenarios validating standard checkouts, shelf holds, duplicate/self-hold rejections, invalid ID handling, hold cancellations, hold fulfillment, and file persistence round-trips.
   
* UI Testing: Manually verified the "Display Issued Books" layout renders correctly without crashing. Tested all new console menu inputs for placing, viewing, and canceling reservations.

#### Limitations of Testing

* In a multi-user environment, race conditions could occur if two users attempt to reserve the final copy of a book simultaneously.

* The system tracks reservation dates but lacks simulated time testing for automated hold expiration.

#### Areas Where Bugs Might Still Exist

* FileHandler relies on simple comma separation. If a user inputs a name or address containing an unescaped comma, the split(",") logic will misalign fields upon reload.