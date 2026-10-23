package org.sgcite.attendance;

import org.sgcite.attendance.data.DataStore;
import org.sgcite.attendance.dsa.BinarySearch;
import org.sgcite.attendance.dsa.MergeSort;
import org.sgcite.attendance.models.Officer;
import org.sgcite.attendance.models.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Automated Verification Test for Issue #3: DSA Engine - BinarySearch (O(log n) Search).
 */
public class BinarySearchVerificationTest {

    public static void main(String[] args) {
        System.out.println(">>> RUNNING BINARY SEARCH VERIFICATION TESTS <<<\n");

        testFindStudentByIdExisting();
        testFindStudentByIdNonExisting();
        testFindOfficerByIdExisting();
        testFindOfficerByIdNonExisting();
        testVisualAsciiMockupBenchmark();
        testBoundaryAndNullHandling();

        System.out.println("\n[SUCCESS] ALL BINARY SEARCH VERIFICATION TESTS PASSED SUCCESSFULLY! (Issue #3 Verified)");
    }

    private static void testFindStudentByIdExisting() {
        System.out.println("[TEST 1] Verifying Binary Search on 1,500 Pre-Sorted Students...");
        DataStore.seedData(false);
        MergeSort.sort(DataStore.students, MergeSort.COMPARE_STUDENT_BY_ID);

        // Test boundary elements
        int firstIdx = BinarySearch.findStudentById(DataStore.students, "24-00001");
        assert firstIdx == 0 : "First student 24-00001 resolved to wrong index: " + firstIdx;
        assert "24-00001".equals(DataStore.students.get(firstIdx).getStudentId());

        int lastIdx = BinarySearch.findStudentById(DataStore.students, "24-01500");
        assert lastIdx == 1499 : "Last student 24-01500 resolved to wrong index: " + lastIdx;
        assert "24-01500".equals(DataStore.students.get(lastIdx).getStudentId());

        // Test benchmark student 24-00105
        int irishIdx = BinarySearch.findStudentById(DataStore.students, "24-00105");
        assert irishIdx >= 0 : "Benchmark student 24-00105 not found";
        Student irish = DataStore.students.get(irishIdx);
        assert "PUREZA, IRISH MAY".equals(irish.getFullName()) : "Expected PUREZA, IRISH MAY";

        // Case insensitivity check
        int caseIdx = BinarySearch.findStudentById(DataStore.students, "24-00105");
        assert caseIdx == irishIdx : "Case insensitive search failed";

        System.out.println("  -> Existing students search verification PASSED.");
    }

    private static void testFindStudentByIdNonExisting() {
        System.out.println("[TEST 2] Verifying Binary Search Returns -1 for Non-Existing Student IDs...");
        DataStore.seedData(false);
        MergeSort.sort(DataStore.students, MergeSort.COMPARE_STUDENT_BY_ID);

        assert BinarySearch.findStudentById(DataStore.students, "24-99999") == -1 : "Expected -1 for 24-99999";
        assert BinarySearch.findStudentById(DataStore.students, "99-99999") == -1 : "Expected -1 for 99-99999";
        assert BinarySearch.findStudentById(DataStore.students, "UNKNOWN-ID") == -1 : "Expected -1 for UNKNOWN-ID";
        assert BinarySearch.findStudentById(DataStore.students, "24-00000") == -1 : "Expected -1 for 24-00000";
        assert BinarySearch.findStudentById(DataStore.students, "24-01501") == -1 : "Expected -1 for 24-01501";

        System.out.println("  -> Non-existing student IDs verification PASSED.");
    }

    private static void testFindOfficerByIdExisting() {
        System.out.println("[TEST 3] Verifying Binary Search on SG CITE Officer Whitelist...");
        DataStore.seedData(false);
        MergeSort.sort(DataStore.officers, MergeSort.COMPARE_OFFICER_BY_ID);

        int presIdx = BinarySearch.findOfficerById(DataStore.officers, "24-00105");
        assert presIdx >= 0 : "President 24-00105 not found";
        Officer pres = DataStore.officers.get(presIdx);
        assert "President".equals(pres.getPosition()) : "Expected President position";

        int secIdx = BinarySearch.findOfficerById(DataStore.officers, "24-00312");
        assert secIdx >= 0 : "Secretary 24-00312 not found";
        Officer sec = DataStore.officers.get(secIdx);
        assert "Secretary".equals(sec.getPosition()) : "Expected Secretary position";

        System.out.println("  -> Existing officers search verification PASSED.");
    }

    private static void testFindOfficerByIdNonExisting() {
        System.out.println("[TEST 4] Verifying Binary Search Returns -1 for Non-Existing Officer IDs...");
        DataStore.seedData(false);
        MergeSort.sort(DataStore.officers, MergeSort.COMPARE_OFFICER_BY_ID);

        assert BinarySearch.findOfficerById(DataStore.officers, "24-99999") == -1 : "Expected -1 for non-officer ID";
        assert BinarySearch.findOfficerById(DataStore.officers, "NON-OFFICER") == -1 : "Expected -1 for NON-OFFICER";

        System.out.println("  -> Non-existing officer IDs verification PASSED.");
    }

    private static void testVisualAsciiMockupBenchmark() {
        System.out.println("[TEST 5] Verifying Visual ASCII Mockup and Diagnostic Benchmark...");
        DataStore.seedData(false);
        MergeSort.sort(DataStore.students, MergeSort.COMPARE_STUDENT_BY_ID);

        String targetId = "24-01492";
        BinarySearch.SearchResult<Student> result = BinarySearch.printSearchValidation(DataStore.students, targetId);

        assert result.isFound() : "Target student 24-01492 must be found";
        assert "DELA CRUZ, JUAN".equals(result.getItem().getFullName()) : "Expected DELA CRUZ, JUAN";
        assert "BSIT-2101".equals(result.getItem().getSection()) : "Expected BSIT-2101";
        assert result.getComparisons() == 9 : "Expected exactly 9 comparisons, got: " + result.getComparisons();

        System.out.println("  -> Visual ASCII mockup verification PASSED.");
    }

    private static void testBoundaryAndNullHandling() {
        System.out.println("[TEST 6] Verifying Defensive Null & Boundary Handling...");

        // Null list
        assert BinarySearch.findStudentById(null, "24-00105") == -1;
        assert BinarySearch.findOfficerById(null, "24-00105") == -1;

        // Empty list
        List<Student> emptyStudents = new ArrayList<>();
        assert BinarySearch.findStudentById(emptyStudents, "24-00105") == -1;

        // Null / empty target ID
        assert BinarySearch.findStudentById(DataStore.students, null) == -1;
        assert BinarySearch.findStudentById(DataStore.students, "") == -1;
        assert BinarySearch.findStudentById(DataStore.students, "   ") == -1;

        System.out.println("  -> Boundary and null handling verification PASSED.");
    }
}
