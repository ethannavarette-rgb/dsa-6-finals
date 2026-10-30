package org.sgcite.attendance;

import org.sgcite.attendance.data.DataStore;
import org.sgcite.attendance.dsa.BinarySearch;
import org.sgcite.attendance.models.Student;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class MainVerificationTest {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(">>> RUNNING MAIN CONSOLE INTEGRATION & QA VERIFICATION TEST (Issue #10)");
        System.out.println("================================================================================");

        testShutdownFlow();
        testInvalidInputsAndRecovery();
        testFullEndToEndFlow();

        System.out.println("================================================================================");
        System.out.println("[SUCCESS] All Issue #10 Main loop integration and QA tests passed!");
        System.out.println("================================================================================");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            System.err.println("[FAIL] " + message);
            throw new AssertionError(message);
        }
    }

    private static void testShutdownFlow() {
        String input = "0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        Main.run(scanner);

        assertTrue(DataStore.students.size() == 1500, "1500 students must be seeded");
        assertTrue(DataStore.officers.size() >= 12, "Officers must be seeded");

        // Verify section distribution: exactly 100 students per section
        for (String sec : DataStore.SECTIONS) {
            int count = 0;
            for (Student s : DataStore.students) {
                if (sec.equals(s.getSection())) {
                    count++;
                }
            }
            assertTrue(count == 100, "Section " + sec + " must have exactly 100 students, found " + count);
        }

        // Check sortedness of students after main initialization
        for (int i = 0; i < DataStore.students.size() - 1; i++) {
            String curr = DataStore.students.get(i).getStudentId();
            String next = DataStore.students.get(i + 1).getStudentId();
            assertTrue(curr.compareToIgnoreCase(next) <= 0, "Students must be sorted by ID");
        }

        System.out.println("[✓] testShutdownFlow passed.");
    }

    private static void testInvalidInputsAndRecovery() {
        // Feed empty strings, letters, invalid numbers, then shutdown
        String input = String.join("\n",
                "",
                "abc",
                "99",
                "0"
        ) + "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        Main.run(scanner);
        System.out.println("[✓] testInvalidInputsAndRecovery passed.");
    }

    private static void testFullEndToEndFlow() {
        // Scripted complete multi-role flow following required business logic:
        // 1. Role: [2] STUDENT -> ID: 24-00105
        //    -> [1] Event Check-In (Enqueued in FIFO) -> Exit? NO
        //    -> [1] Event Check-In again (Duplicate rejected) -> Exit? NO
        //    -> [2] Submit Requirement (Rejected: not confirmed checked-in yet) -> Exit? YES
        // 2. Role: [1] OFFICER -> ID: 24-00105, Password: sgcite2026
        //    -> [2] Attendance Report (Flushes queue, marks student checked in) -> Exit? NO
        //    -> [4] View Action History -> Exit? NO
        //    -> [5] Exit Officer Dashboard -> Exit? YES
        // 3. Role: [2] STUDENT -> ID: 24-00105
        //    -> [1] Event Check-In (Rejected: already checked in) -> Exit? NO
        //    -> [2] Submit Requirement (Accepted: requirementSubmitted=true) -> Exit? NO
        //    -> [2] Submit Requirement again (Rejected: already submitted) -> Exit? YES
        // 4. Role: [1] OFFICER -> ID: 24-00105, Password: sgcite2026
        //    -> [2] Attendance Report (Shows 1 attendee, 1 requirement submitted) -> Exit? NO
        //    -> [5] Exit Officer Dashboard -> Exit? YES
        // 5. Role: [0] SHUTDOWN
        String input = String.join("\n",
                "2",
                "24-00105",
                "1",
                "NO",
                "1",
                "NO",
                "2",
                "YES",
                "1",
                "24-00105",
                "sgcite2026",
                "2",
                "NO",
                "4",
                "NO",
                "5",
                "YES",
                "2",
                "24-00105",
                "1",
                "NO",
                "2",
                "NO",
                "2",
                "YES",
                "1",
                "24-00105",
                "sgcite2026",
                "2",
                "NO",
                "5",
                "YES",
                "0"
        ) + "\n";

        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        Main.run(scanner);

        int studentIdx = BinarySearch.findStudentById(DataStore.students, "24-00105");
        assertTrue(studentIdx >= 0, "Benchmark student must exist");
        Student s = DataStore.students.get(studentIdx);
        assertTrue(s.isCheckedIn(), "Student must be checked in after officer attendance flush");
        assertTrue(s.isRequirementSubmitted(), "Student requirement must be submitted after confirmed check-in");
        assertTrue(DataStore.sectionAttendanceMap.get("BSIT-2101") == 1, "Section BSIT-2101 must have 1 attendee");

        System.out.println("[✓] testFullEndToEndFlow passed.");
    }
}
