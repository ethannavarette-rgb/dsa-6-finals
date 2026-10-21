package sgcite;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Interactive Command Line Interface for the SG-CITE Event Attendance System.
 * Adheres strictly to safety guidelines:
 * - Employs boolean flags for all loop controls (no while(true) loops).
 * - Implements comprehensive input validation and exception shielding to prevent crashes.
 */
public class Main {
    private final AttendanceManager manager;
    private final Scanner scanner;

    public Main() {
        this.manager = new AttendanceManager();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run() {
        boolean isRunning = true;

        System.out.println("================================================================");
        System.out.println("     WELCOME TO SG-CITE EVENT ATTENDANCE MANAGEMENT SYSTEM      ");
        System.out.println("  College of Information and Communications Technology / CS     ");
        System.out.println("================================================================");

        while (isRunning) {
            displayMenu();
            int choice = readIntegerInput("Enter choice (1-9): ", 1, 9);

            switch (choice) {
                case 1:
                    handleEnqueueStudent();
                    break;
                case 2:
                    handleProcessCheckIn();
                    break;
                case 3:
                    handleProcessCheckOut();
                    break;
                case 4:
                    handleSearchAttendee();
                    break;
                case 5:
                    handleSortAndDisplayRoster();
                    break;
                case 6:
                    handleViewAnalytics();
                    break;
                case 7:
                    handleGenerateMockData();
                    break;
                case 8:
                    handleViewQueue();
                    break;
                case 9:
                    System.out.println("\nThank you for using SG-CITE Attendance System. Goodbye!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose a valid number from the menu.");
                    break;
            }

            if (isRunning) {
                System.out.println();
                pausePrompt();
            }
        }

        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n-------------------- MAIN TRANSACTIONS MENU --------------------");
        System.out.println(" [1] [TX-01] Enqueue Student Arrival (Entrance Queue)");
        System.out.println(" [2] [TX-02] Process Check-in / Time-In (Single / Batch Dequeue)");
        System.out.println(" [3] [TX-03] Process Check-out / Time-Out (via Binary Search)");
        System.out.println(" [4] [TX-04] Search Attendee Record (via Binary Search by ID)");
        System.out.println(" [5] [TX-05] Sort & View Attendance Roster (via MergeSort)");
        System.out.println(" [6] [TX-06] View Event Summary & Attendance Analytics");
        System.out.println(" [7] [TX-07] Generate Mock Data (300 to 500 Students)");
        System.out.println(" [8] [INFO ] View Entrance Queue Status");
        System.out.println(" [9] [EXIT ] Exit Program");
        System.out.println("----------------------------------------------------------------");
        System.out.printf("Current Status: Queue: %d waiting | Confirmed Roster: %d attendees%n",
                manager.getQueueCount(), manager.getRosterCount());
        System.out.println("----------------------------------------------------------------");
    }

    // -------------------------------------------------------------
    // TRANSACTION HANDLERS
    // -------------------------------------------------------------

    private void handleEnqueueStudent() {
        System.out.println("\n--- [TX-01] ENQUEUE STUDENT ARRIVAL ---");
        String studentId = readNonEmptyString("Enter Student ID (e.g. 2024-1001): ");
        String fullName = readNonEmptyString("Enter Full Name: ");
        String program = readProgramInput("Enter Program (BSIT / BSCS / BSIS / BLIS): ");
        int yearLevel = readIntegerInput("Enter Year Level (1-4): ", 1, 4);

        boolean enqueued = manager.enqueueStudent(studentId, fullName, program, yearLevel);
        if (enqueued) {
            System.out.printf("[SUCCESS] Student '%s' (%s) enqueued at position #%d.%n",
                    fullName, studentId, manager.getQueueCount());
        } else {
            System.out.println("[FAILED] Could not enqueue student. The Student ID already exists in queue or roster.");
        }
    }

    private void handleProcessCheckIn() {
        System.out.println("\n--- [TX-02] PROCESS CHECK-IN (TIME-IN) ---");
        if (manager.getQueueCount() == 0) {
            System.out.println("[INFO] The arrival queue is currently empty. No students waiting to be checked in.");
            return;
        }

        System.out.println(" 1. Process Next Single Student in Line (Dequeue 1)");
        System.out.println(" 2. Batch Process All Queued Students (Dequeue All)");
        int subChoice = readIntegerInput("Select option (1-2): ", 1, 2);

        if (subChoice == 1) {
            Student s = manager.processNextCheckIn();
            if (s != null) {
                System.out.printf("[SUCCESS] Checked-in: [%s] %s | Time-In: %s%n",
                        s.getStudentId(), s.getFullName(), s.getTimeIn());
            }
        } else {
            int queueBefore = manager.getQueueCount();
            int count = manager.batchProcessCheckIn();
            System.out.printf("[SUCCESS] Successfully batch-processed and checked in %d student(s). Queue is now clear.%n", count);
        }
    }

    private void handleProcessCheckOut() {
        System.out.println("\n--- [TX-03] PROCESS CHECK-OUT (TIME-OUT) ---");
        if (manager.getRosterCount() == 0) {
            System.out.println("[INFO] No attendees currently in the master roster.");
            return;
        }

        String studentId = readNonEmptyString("Enter Student ID to check out: ");
        AttendanceManager.CheckOutResult result = manager.processCheckOut(studentId);

        System.out.printf("[SEARCH DIAGNOSTIC] Binary Search executed in %d comparison(s).%n", result.getComparisons());

        if (result.getStatus() == AttendanceManager.CheckOutStatus.SUCCESS) {
            Student s = result.getStudent();
            System.out.printf("[SUCCESS] Check-out recorded for [%s] %s. Time-Out: %s%n",
                    s.getStudentId(), s.getFullName(), s.getTimeOut());
        } else if (result.getStatus() == AttendanceManager.CheckOutStatus.ALREADY_CHECKED_OUT) {
            Student s = result.getStudent();
            System.out.printf("[NOTICE] Student [%s] %s already checked out at %s.%n",
                    s.getStudentId(), s.getFullName(), s.getTimeOut());
        } else {
            System.out.printf("[NOT FOUND] No checked-in record found for Student ID '%s'.%n", studentId);
        }
    }

    private void handleSearchAttendee() {
        System.out.println("\n--- [TX-04] SEARCH ATTENDEE RECORD (BINARY SEARCH) ---");
        if (manager.getRosterCount() == 0) {
            System.out.println("[INFO] Roster is empty. Try checking in students or generating mock data first.");
            return;
        }

        String studentId = readNonEmptyString("Enter Student ID to search: ");

        long startTime = System.nanoTime();
        BinarySearch.SearchResult result = manager.searchAttendee(studentId);
        long elapsedNanos = System.nanoTime() - startTime;

        System.out.println("\n--------------------- SEARCH RESULT ---------------------");
        System.out.printf("Algorithm Used   : Custom Binary Search O(log n)%n");
        System.out.printf("Comparisons Made : %d (Max possible for %d records: ~%d)%n",
                result.getComparisons(), manager.getRosterCount(),
                (int) Math.ceil(Math.log(manager.getRosterCount()) / Math.log(2)));
        System.out.printf("Search Duration  : %.3f ms (%d ns)%n", elapsedNanos / 1_000_000.0, elapsedNanos);
        System.out.println("---------------------------------------------------------");

        if (result.isFound()) {
            Student s = result.getStudent();
            System.out.printf("Student ID       : %s%n", s.getStudentId());
            System.out.printf("Full Name        : %s%n", s.getFullName());
            System.out.printf("Program & Year   : %s - Year %d%n", s.getProgram(), s.getYearLevel());
            System.out.printf("Time-In          : %s%n", s.getTimeIn());
            System.out.printf("Time-Out         : %s%n", s.getTimeOut());
            System.out.printf("Current Status   : %s%n", s.getStatus());
        } else {
            System.out.printf("[NOT FOUND] No attendee record matches Student ID '%s'.%n", studentId);
        }
        System.out.println("---------------------------------------------------------");
    }

    private void handleSortAndDisplayRoster() {
        System.out.println("\n--- [TX-05] SORT & VIEW ATTENDANCE ROSTER (MERGESORT) ---");
        List<Student> roster = manager.getConfirmedRoster();
        if (roster.isEmpty()) {
            System.out.println("[INFO] Roster is currently empty. No attendees to display.");
            return;
        }

        System.out.println("Select Sorting Criteria (Custom MergeSort O(n log n)):");
        System.out.println(" 1. Sort by Student ID (Ascending)");
        System.out.println(" 2. Sort by Full Name (Alphabetical A-Z)");
        System.out.println(" 3. Sort by Time-In (Chronological)");
        int sortOption = readIntegerInput("Select option (1-3): ", 1, 3);

        MergeSort.SortCriteria criteria = MergeSort.SortCriteria.STUDENT_ID;
        if (sortOption == 2) {
            criteria = MergeSort.SortCriteria.FULL_NAME;
        } else if (sortOption == 3) {
            criteria = MergeSort.SortCriteria.TIME_IN;
        }

        long start = System.nanoTime();
        manager.sortRoster(criteria);
        long elapsed = System.nanoTime() - start;

        System.out.printf("[SUCCESS] MergeSort completed in %.3f ms (%d ns) for %d records.%n",
                elapsed / 1_000_000.0, elapsed, roster.size());

        // Display pagination/summary options for 300-500 attendees
        System.out.println("\nDisplay Options:");
        System.out.println(" 1. View First 25 Records (Recommended for clean terminal)");
        System.out.println(" 2. View All " + roster.size() + " Records");
        System.out.println(" 3. Custom Limit");
        int viewOption = readIntegerInput("Select option (1-3): ", 1, 3);

        int limit = 25;
        if (viewOption == 2) {
            limit = roster.size();
        } else if (viewOption == 3) {
            limit = readIntegerInput("Enter number of records to show (1-" + roster.size() + "): ", 1, roster.size());
        }

        printStudentTable(roster, limit);
    }

    private void handleViewAnalytics() {
        System.out.println("\n--- [TX-06] EVENT SUMMARY & ATTENDANCE ANALYTICS ---");
        AttendanceManager.EventAnalytics analytics = manager.getAnalytics();

        System.out.println("================================================================");
        System.out.println("              SG-CITE EVENT ATTENDANCE DASHBOARD                ");
        System.out.println("================================================================");
        System.out.printf(" Entrance Queue (Waiting to Enter) : %d%n", analytics.getQueuedCount());
        System.out.printf(" Confirmed Master Roster           : %d%n", analytics.getTotalRoster());
        System.out.printf(" Currently Inside Venue            : %d%n", analytics.getActiveInside());
        System.out.printf(" Checked-Out (Departed)            : %d%n", analytics.getCheckedOut());
        System.out.println("----------------------------------------------------------------");

        System.out.println("Distribution by Degree Program:");
        for (Map.Entry<String, Integer> entry : analytics.getProgramCount().entrySet()) {
            double percent = (analytics.getTotalRoster() > 0)
                    ? (entry.getValue() * 100.0 / analytics.getTotalRoster()) : 0.0;
            System.out.printf(" - %-6s : %4d student(s)  (%.1f%%)%n", entry.getKey(), entry.getValue(), percent);
        }

        System.out.println("\nDistribution by Year Level:");
        int[] years = analytics.getYearLevels();
        for (int y = 1; y <= 4; y++) {
            double percent = (analytics.getTotalRoster() > 0)
                    ? (years[y] * 100.0 / analytics.getTotalRoster()) : 0.0;
            System.out.printf(" - Year %d : %4d student(s)  (%.1f%%)%n", y, years[y], percent);
        }
        System.out.println("================================================================");
    }

    private void handleGenerateMockData() {
        System.out.println("\n--- [TX-07] GENERATE MOCK DATA (300 TO 500 ATTENDEES) ---");
        System.out.println("Recommended population size is between 300 and 500 students.");
        int count = readIntegerInput("Enter number of mock students to generate (300 - 500): ", 300, 500);

        int generated = manager.generateMockData(count);
        System.out.printf("[SUCCESS] Generated %d realistic SG-CITE student records.%n", generated);
        System.out.printf("Current Status: %d in Master Roster | %d waiting in Entrance Queue.%n",
                manager.getRosterCount(), manager.getQueueCount());
    }

    private void handleViewQueue() {
        System.out.println("\n--- [INFO] ENTRANCE CHECK-IN QUEUE STATUS ---");
        CustomQueue<Student> queue = manager.getArrivalQueue();
        if (queue.isEmpty()) {
            System.out.println("[INFO] The check-in queue is currently empty.");
            return;
        }

        System.out.printf("Total students currently waiting in queue: %d%n", queue.size());
        Student front = queue.peek();
        if (front != null) {
            System.out.printf("Next in line to be checked in (Front of Queue): [%s] %s (%s - Year %d)%n",
                    front.getStudentId(), front.getFullName(), front.getProgram(), front.getYearLevel());
        }

        List<Student> queueList = queue.toList();
        int showCount = Math.min(10, queueList.size());
        System.out.printf("\nShowing next %d student(s) in arrival order:%n", showCount);
        printStudentTable(queueList, showCount);
    }

    // -------------------------------------------------------------
    // SAFE INPUT & DISPLAY UTILITIES (CRASH PREVENTION)
    // -------------------------------------------------------------

    private void printStudentTable(List<Student> students, int limit) {
        System.out.println("+------------+-------------------------+---------+------+-------------+-------------+-------------+");
        System.out.println("| Student ID | Full Name               | Program | Year | Time-In     | Time-Out    | Status      |");
        System.out.println("+------------+-------------------------+---------+------+-------------+-------------+-------------+");

        int displayLimit = Math.min(limit, students.size());
        int i = 0;
        boolean hasRows = (i < displayLimit);

        while (hasRows) {
            Student s = students.get(i);
            System.out.printf("| %-10s | %-23s | %-7s | %-4d | %-11s | %-11s | %-11s |%n",
                    truncate(s.getStudentId(), 10),
                    truncate(s.getFullName(), 23),
                    truncate(s.getProgram(), 7),
                    s.getYearLevel(),
                    truncate(s.getTimeIn(), 11),
                    truncate(s.getTimeOut(), 11),
                    s.getStatus());
            i++;
            hasRows = (i < displayLimit);
        }

        System.out.println("+------------+-------------------------+---------+------+-------------+-------------+-------------+");
        if (displayLimit < students.size()) {
            System.out.printf("[Showing %d of %d total records. Use Sort & View to display more.]%n",
                    displayLimit, students.size());
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 2) + "..";
    }

    private int readIntegerInput(String prompt, int min, int max) {
        int value = -1;
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input == null) {
                input = "";
            }
            input = input.trim();

            try {
                value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    valid = true;
                } else {
                    System.out.printf("[ERROR] Number must be between %d and %d. Please try again.%n", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid numeric input. Please enter a valid number.");
            }
        }
        return value;
    }

    private String readNonEmptyString(String prompt) {
        String result = "";
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                result = input.trim();
                valid = true;
            } else {
                System.out.println("[ERROR] Input cannot be blank. Please try again.");
            }
        }
        return result;
    }

    private String readProgramInput(String prompt) {
        String result = "";
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null) {
                input = input.trim().toUpperCase();
                if (input.equals("BSIT") || input.equals("BSCS") || input.equals("BSIS") || input.equals("BLIS")) {
                    result = input;
                    valid = true;
                } else if (!input.isEmpty()) {
                    // Also permit other custom programs within CITE
                    result = input;
                    valid = true;
                } else {
                    System.out.println("[ERROR] Program cannot be blank. Recommended: BSIT, BSCS, BSIS, BLIS.");
                }
            }
        }
        return result;
    }

    private void pausePrompt() {
        System.out.print("Press [Enter] to return to the main menu...");
        scanner.nextLine();
    }
}
