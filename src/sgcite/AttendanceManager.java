package sgcite;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controller service that coordinates all SG-CITE attendance transactions.
 * Enforces business logic, prevents duplicate records, coordinates custom MergeSort and BinarySearch,
 * and maintains data consistency between the entrance queue and the confirmed attendance roster.
 */
public class AttendanceManager {
    private final CustomQueue<Student> arrivalQueue;
    private final List<Student> confirmedRoster;
    private boolean isRosterSortedById;

    public AttendanceManager() {
        this.arrivalQueue = new CustomQueue<>();
        this.confirmedRoster = new ArrayList<>();
        this.isRosterSortedById = false;
    }

    /**
     * TX-01: Enqueue Student Arrival.
     * Places an arriving student into the entrance check-in queue (FIFO).
     */
    public boolean enqueueStudent(String studentId, String fullName, String program, int yearLevel) {
        if (studentId == null || studentId.trim().isEmpty() || fullName == null || fullName.trim().isEmpty()) {
            return false;
        }

        String cleanedId = studentId.trim();

        // Check if student is already in queue or already checked in
        if (isStudentInQueue(cleanedId) || isStudentInRoster(cleanedId)) {
            return false;
        }

        Student student = new Student(cleanedId, fullName, program, yearLevel);
        return arrivalQueue.enqueue(student);
    }

    /**
     * TX-02 (Single): Dequeue and check in the next student in line.
     */
    public Student processNextCheckIn() {
        if (arrivalQueue.isEmpty()) {
            return null;
        }

        Student student = arrivalQueue.dequeue();
        if (student != null) {
            student.markCheckIn(getCurrentTimestamp());
            confirmedRoster.add(student);
            isRosterSortedById = false;
        }
        return student;
    }

    /**
     * TX-02 (Batch): Dequeues and checks in all students currently waiting in the arrival queue.
     *
     * @return Number of students successfully checked in.
     */
    public int batchProcessCheckIn() {
        int processedCount = 0;
        boolean hasQueuedStudents = !arrivalQueue.isEmpty();

        while (hasQueuedStudents) {
            Student student = arrivalQueue.dequeue();
            if (student != null) {
                student.markCheckIn(getCurrentTimestamp());
                confirmedRoster.add(student);
                processedCount++;
            }
            hasQueuedStudents = !arrivalQueue.isEmpty();
        }

        if (processedCount > 0) {
            isRosterSortedById = false;
        }
        return processedCount;
    }

    /**
     * TX-03: Process Check-Out (Time-Out).
     * Uses custom Binary Search to locate the attendee and record their departure time.
     *
     * @param studentId Target student ID.
     * @return The updated Student object, or null if not found or already checked out.
     */
    public CheckOutResult processCheckOut(String studentId) {
        if (studentId == null || studentId.trim().isEmpty() || confirmedRoster.isEmpty()) {
            return new CheckOutResult(CheckOutStatus.NOT_FOUND, null, 0);
        }

        ensureRosterSortedById();
        BinarySearch.SearchResult result = BinarySearch.searchById(confirmedRoster, studentId);

        if (!result.isFound()) {
            return new CheckOutResult(CheckOutStatus.NOT_FOUND, null, result.getComparisons());
        }

        Student student = result.getStudent();
        if (student.getStatus() == AttendanceStatus.CHECKED_OUT) {
            return new CheckOutResult(CheckOutStatus.ALREADY_CHECKED_OUT, student, result.getComparisons());
        }

        student.markCheckOut(getCurrentTimestamp());
        return new CheckOutResult(CheckOutStatus.SUCCESS, student, result.getComparisons());
    }

    /**
     * TX-04: Search Attendee Record.
     * Performs custom Binary Search on the confirmed roster.
     */
    public BinarySearch.SearchResult searchAttendee(String studentId) {
        if (studentId == null || studentId.trim().isEmpty() || confirmedRoster.isEmpty()) {
            return new BinarySearch.SearchResult(-1, null, 0);
        }

        ensureRosterSortedById();
        return BinarySearch.searchById(confirmedRoster, studentId);
    }

    /**
     * TX-05: Sort Attendance Roster using custom MergeSort.
     */
    public void sortRoster(MergeSort.SortCriteria criteria) {
        if (confirmedRoster.isEmpty() || confirmedRoster.size() == 1) {
            return;
        }

        MergeSort.sort(confirmedRoster, criteria);
        this.isRosterSortedById = (criteria == MergeSort.SortCriteria.STUDENT_ID);
    }

    /**
     * TX-06: View Event Summary & Attendance Analytics.
     */
    public EventAnalytics getAnalytics() {
        int totalQueued = arrivalQueue.size();
        int totalInRoster = confirmedRoster.size();
        int activeInside = 0;
        int checkedOut = 0;

        Map<String, Integer> programCount = new HashMap<>();
        int[] yearLevels = new int[5]; // Indices 1 to 4

        int i = 0;
        boolean hasAttendeesToAnalyze = (i < confirmedRoster.size());
        while (hasAttendeesToAnalyze) {
            Student s = confirmedRoster.get(i);
            if (s.getStatus() == AttendanceStatus.CHECKED_OUT) {
                checkedOut++;
            } else {
                activeInside++;
            }

            String prog = (s.getProgram() != null && !s.getProgram().isEmpty()) ? s.getProgram() : "OTHER";
            programCount.put(prog, programCount.getOrDefault(prog, 0) + 1);

            int yr = s.getYearLevel();
            if (yr >= 1 && yr <= 4) {
                yearLevels[yr]++;
            }

            i++;
            hasAttendeesToAnalyze = (i < confirmedRoster.size());
        }

        return new EventAnalytics(totalQueued, totalInRoster, activeInside, checkedOut, programCount, yearLevels);
    }

    /**
     * TX-07: Generate Mock Data (300 to 500 Students).
     * Populates the system with realistic SG-CITE attendee records for testing and demonstration.
     *
     * @param count Number of mock students to generate (between 300 and 500).
     * @return Actual number of mock students created.
     */
    public int generateMockData(int count) {
        if (count < 1) {
            count = 350;
        }

        String[] firstNames = {
                "Juan", "Maria", "Mark", "Angelo", "Christian", "Jasmine", "Bea", "Joshua",
                "Patricia", "Daniel", "Samantha", "Kevin", "Nicole", "Gabriel", "Renz", "Althea",
                "Ezekiel", "Camille", "John", "Princess", "Carl", "Angelica", "Paolo", "Chloe"
        };

        String[] lastNames = {
                "Santos", "Reyes", "Cruz", "Bautista", "Ocampo", "Garcia", "Mendoza", "Torres",
                "Tomas", "Andrada", "Castillo", "Flores", "Villanueva", "Ramos", "Castro", "Rivera",
                "Aquino", "Navarro", "Salazar", "Mercado", "De Leon", "Pascual", "Valdez", "Soriano"
        };

        String[] programs = {"BSIT", "BSCS", "BSIS", "BLIS"};

        int existingCount = confirmedRoster.size();
        int queuePortion = Math.min(30, count / 10); // Keep around 20-30 in queue for queue testing
        int rosterPortion = count - queuePortion;

        int added = 0;
        boolean isGenerating = (added < count);

        while (isGenerating) {
            int seq = existingCount + added + 1;
            String studentId = String.format("2024-%04d", seq);

            String firstName = firstNames[(added * 7) % firstNames.length];
            String lastName = lastNames[(added * 11) % lastNames.length];
            String fullName = firstName + " " + lastName;

            String program = programs[(added * 3) % programs.length];
            int year = (added % 4) + 1;

            if (added < rosterPortion) {
                // Add to confirmed roster
                int minute = (added % 60);
                int hour = 8 + (added / 60) % 2;
                String timeIn = String.format("%02d:%02d AM", hour, minute);

                Student s = new Student(studentId, fullName, program, year, timeIn);

                // Simulate ~25% having already checked out
                if (added % 4 == 0) {
                    s.markCheckOut(String.format("11:%02d AM", (minute + 15) % 60));
                }

                confirmedRoster.add(s);
            } else {
                // Add to entrance queue
                arrivalQueue.enqueue(new Student(studentId, fullName, program, year));
            }

            added++;
            isGenerating = (added < count);
        }

        isRosterSortedById = false;
        return added;
    }

    // Helper queries & safeguards
    public CustomQueue<Student> getArrivalQueue() {
        return arrivalQueue;
    }

    public List<Student> getConfirmedRoster() {
        return Collections.unmodifiableList(confirmedRoster);
    }

    public int getRosterCount() {
        return confirmedRoster.size();
    }

    public int getQueueCount() {
        return arrivalQueue.size();
    }

    public boolean isRosterSortedById() {
        return isRosterSortedById;
    }

    private void ensureRosterSortedById() {
        if (!isRosterSortedById) {
            MergeSort.sort(confirmedRoster, MergeSort.SortCriteria.STUDENT_ID);
            this.isRosterSortedById = true;
        }
    }

    private boolean isStudentInQueue(String studentId) {
        List<Student> queued = arrivalQueue.toList();
        int i = 0;
        boolean checking = (i < queued.size());
        while (checking) {
            if (queued.get(i).getStudentId().equalsIgnoreCase(studentId)) {
                return true;
            }
            i++;
            checking = (i < queued.size());
        }
        return false;
    }

    private boolean isStudentInRoster(String studentId) {
        int i = 0;
        boolean checking = (i < confirmedRoster.size());
        while (checking) {
            if (confirmedRoster.get(i).getStudentId().equalsIgnoreCase(studentId)) {
                return true;
            }
            i++;
            checking = (i < confirmedRoster.size());
        }
        return false;
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
        return sdf.format(new Date());
    }

    // Result and Analytics containers
    public enum CheckOutStatus {
        SUCCESS,
        NOT_FOUND,
        ALREADY_CHECKED_OUT
    }

    public static class CheckOutResult {
        private final CheckOutStatus status;
        private final Student student;
        private final int comparisons;

        public CheckOutResult(CheckOutStatus status, Student student, int comparisons) {
            this.status = status;
            this.student = student;
            this.comparisons = comparisons;
        }

        public CheckOutStatus getStatus() {
            return status;
        }

        public Student getStudent() {
            return student;
        }

        public int getComparisons() {
            return comparisons;
        }
    }

    public static class EventAnalytics {
        private final int queuedCount;
        private final int totalRoster;
        private final int activeInside;
        private final int checkedOut;
        private final Map<String, Integer> programCount;
        private final int[] yearLevels;

        public EventAnalytics(int queuedCount, int totalRoster, int activeInside, int checkedOut,
                              Map<String, Integer> programCount, int[] yearLevels) {
            this.queuedCount = queuedCount;
            this.totalRoster = totalRoster;
            this.activeInside = activeInside;
            this.checkedOut = checkedOut;
            this.programCount = programCount;
            this.yearLevels = yearLevels;
        }

        public int getQueuedCount() {
            return queuedCount;
        }

        public int getTotalRoster() {
            return totalRoster;
        }

        public int getActiveInside() {
            return activeInside;
        }

        public int getCheckedOut() {
            return checkedOut;
        }

        public Map<String, Integer> getProgramCount() {
            return programCount;
        }

        public int[] getYearLevels() {
            return yearLevels;
        }
    }
}
