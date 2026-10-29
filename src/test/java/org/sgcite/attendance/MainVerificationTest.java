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

    private static void testShutdownFlow() {
        String input = "0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        Main.run(scanner);

        assert DataStore.students.size() == 1500 : "1500 students must be seeded";
        assert DataStore.officers.size() >= 12 : "Officers must be seeded";

        // Check sortedness of students after main initialization
        for (int i = 0; i < DataStore.students.size() - 1; i++) {
            String curr = DataStore.students.get(i).getStudentId();
            String next = DataStore.students.get(i + 1).getStudentId();
            assert curr.compareToIgnoreCase(next) <= 0 : "Students must be sorted by ID";
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
        // Scripted complete multi-role flow:
        // 1. Role: [2] STUDENT -> ID: 24-00105 -> [1] Event Check-In -> Exit? NO -> [2] Submit Requirement -> Exit? YES
        // 2. Role: [1] OFFICER -> ID: 24-00105, Password: sgcite2026 -> [2] Attendance Report -> Exit? NO -> [4] Exit -> Exit? YES
        // 3. Role: [0] SHUTDOWN
        String input = String.join("\n",
                "2",
                "24-00105",
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
                "YES",
                "0"
        ) + "\n";

        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        Main.run(scanner);

        int studentIdx = BinarySearch.findStudentById(DataStore.students, "24-00105");
        assert studentIdx >= 0 : "Benchmark student must exist";
        Student s = DataStore.students.get(studentIdx);
        assert s.isCheckedIn() : "Student must be checked in after officer attendance flush";
        assert s.isRequirementSubmitted() : "Student requirement must be submitted";

        System.out.println("[✓] testFullEndToEndFlow passed.");
    }
}
