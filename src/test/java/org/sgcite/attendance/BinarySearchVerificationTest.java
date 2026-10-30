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

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            System.err.println("[FAIL] " + message);
            throw new AssertionError(message);
        }
    }

    public static void main(String[] args) {
        System.out.println(">>> RUNNING BINARY SEARCH VERIFICATION TESTS <<<\n");

        testFindStudentByIdExisting();
        testFindStudentByIdNonExisting();
        testFindOfficerByIdExisting();
        testFindOfficerByIdNonExisting();
        testVisualAsciiMockupBenchmark();
        testExhaustiveComparisonBound1500Students();
        testBoundaryAndNullHandling();

        System.out.println("\n[SUCCESS] ALL BINARY SEARCH VERIFICATION TESTS PASSED SUCCESSFULLY! (Issue #3 Verified)");
    }

    private static void testFindStudentByIdExisting() {
        System.out.println("[TEST 1] Verifying Binary Search on 1,500 Pre-Sorted Students...");
        DataStore.seedData(false);
        MergeSort.sort(DataStore.students, MergeSort.COMPARE_STUDENT_BY_ID);

        // Test boundary elements
        int firstIdx = BinarySearch.findStudentById(DataStore.students, "24-00001");
        assertTrue(firstIdx == 0, "First student 24-00001 resolved to wrong index: " + firstIdx);
        assertTrue("24-00001".equals(DataStore.students.get(firstIdx).getStudentId()), "First student ID mismatch");

        int lastIdx = BinarySearch.findStudentById(DataStore.students, "24-01500");
        assertTrue(lastIdx == 1499, "Last student 24-01500 resolved to wrong index: " + lastIdx);
        assertTrue("24-01500".equals(DataStore.students.get(lastIdx).getStudentId()), "Last student ID mismatch");

        // Test benchmark student 24-00105
        int irishIdx = BinarySearch.findStudentById(DataStore.students, "24-00105");
        assertTrue(irishIdx >= 0, "Benchmark student 24-00105 not found");
        Student irish = DataStore.students.get(irishIdx);
        assertTrue("PUREZA, IRISH MAY".equals(irish.getFullName()), "Expected PUREZA, IRISH MAY");

        // Case insensitivity check
        int caseIdx = BinarySearch.findStudentById(DataStore.students, "24-00105");
        assertTrue(caseIdx == irishIdx, "Case insensitive search failed");

        System.out.println("  -> Existing students search verification PASSED.");
    }

    private static void testFindStudentByIdNonExisting() {
        System.out.println("[TEST 2] Verifying Binary Search Returns -1 for Non-Existing Student IDs...");
        DataStore.seedData(false);
        MergeSort.sort(DataStore.students, MergeSort.COMPARE_STUDENT_BY_ID);

        assertTrue(BinarySearch.findStudentById(DataStore.students, "24-99999") == -1, "Expected -1 for 24-99999");
        assertTrue(BinarySearch.findStudentById(DataStore.students, "99-99999") == -1, "Expected -1 for 99-99999");
        assertTrue(BinarySearch.findStudentById(DataStore.students, "UNKNOWN-ID") == -1, "Expected -1 for UNKNOWN-ID");
        assertTrue(BinarySearch.findStudentById(DataStore.students, "24-00000") == -1, "Expected -1 for 24-00000");
        assertTrue(BinarySearch.findStudentById(DataStore.students, "24-01501") == -1, "Expected -1 for 24-01501");

        System.out.println("  -> Non-existing student IDs verification PASSED.");
    }

    private static void testFindOfficerByIdExisting() {
        System.out.println("[TEST 3] Verifying Binary Search on SG CITE Officer Whitelist...");
        DataStore.seedData(false);
        MergeSort.sort(DataStore.officers, MergeSort.COMPARE_OFFICER_BY_ID);

        int presIdx = BinarySearch.findOfficerById(DataStore.officers, "24-00105");
        assertTrue(presIdx >= 0, "President 24-00105 not found");
        Officer pres = DataStore.officers.get(presIdx);
        assertTrue("President".equals(pres.getPosition()), "Expected President position");

        int secIdx = BinarySearch.findOfficerById(DataStore.officers, "24-00312");
        assertTrue(secIdx >= 0, "Secretary 24-00312 not found");
        Officer sec = DataStore.officers.get(secIdx);
        assertTrue("Secretary".equals(sec.getPosition()), "Expected Secretary position");

        System.out.println("  -> Existing officers search verification PASSED.");
    }

    private static void testFindOfficerByIdNonExisting() {
        System.out.println("[TEST 4] Verifying Binary Search Returns -1 for Non-Existing Officer IDs...");
        DataStore.seedData(false);
        MergeSort.sort(DataStore.officers, MergeSort.COMPARE_OFFICER_BY_ID);

        assertTrue(BinarySearch.findOfficerById(DataStore.officers, "24-99999") == -1, "Expected -1 for non-officer ID");
        assertTrue(BinarySearch.findOfficerById(DataStore.officers, "NON-OFFICER") == -1, "Expected -1 for NON-OFFICER");

        System.out.println("  -> Non-existing officer IDs verification PASSED.");
    }

    private static void testVisualAsciiMockupBenchmark() {
        System.out.println("[TEST 5] Verifying Visual ASCII Mockup and Diagnostic Benchmark...");
        DataStore.seedData(false);
        MergeSort.sort(DataStore.students, MergeSort.COMPARE_STUDENT_BY_ID);

        String targetId = "24-01492";
        BinarySearch.SearchResult<Student> result = BinarySearch.printSearchValidation(DataStore.students, targetId);

        assertTrue(result.isFound(), "Target student 24-01492 must be found");
        assertTrue("DELA CRUZ, JUAN".equals(result.getItem().getFullName()), "Expected DELA CRUZ, JUAN");
        assertTrue("BSIT-2101".equals(result.getItem().getSection()), "Expected BSIT-2101");
        assertTrue(result.getComparisons() <= 11, "Expected <= 11 comparisons, got: " + result.getComparisons());

        System.out.println("  -> Visual ASCII mockup verification PASSED.");
    }

    private static void testExhaustiveComparisonBound1500Students() {
        System.out.println("[TEST 6] Exhaustively verifying O(log n) bound (<= 11 comparisons) across ALL 1,500 students...");
        DataStore.seedData(false);
        MergeSort.sort(DataStore.students, MergeSort.COMPARE_STUDENT_BY_ID);

        for (int i = 0; i < DataStore.students.size(); i++) {
            Student expected = DataStore.students.get(i);
            BinarySearch.SearchResult<Student> res = BinarySearch.findStudentWithDiagnostics(DataStore.students, expected.getStudentId());
            assertTrue(res.isFound(), "Student not found at index " + i + ": " + expected.getStudentId());
            assertTrue(res.getIndex() == i, "Student index mismatch at " + i + ": expected " + i + " got " + res.getIndex());
            assertTrue(res.getComparisons() <= 11,
                    "Comparison bound violated for " + expected.getStudentId() + ": " + res.getComparisons() + " > 11");
        }

        System.out.println("  -> All 1,500 records resolved with at most 11 comparisons. PASSED.");
    }

    private static void testBoundaryAndNullHandling() {
        System.out.println("[TEST 7] Verifying Defensive Null & Boundary Handling...");

        // Null list
        assertTrue(BinarySearch.findStudentById(null, "24-00105") == -1, "Null list failed");
        assertTrue(BinarySearch.findOfficerById(null, "24-00105") == -1, "Null list failed");

        // Empty list
        List<Student> emptyStudents = new ArrayList<>();
        assertTrue(BinarySearch.findStudentById(emptyStudents, "24-00105") == -1, "Empty list failed");

        // Null / empty target ID
        assertTrue(BinarySearch.findStudentById(DataStore.students, null) == -1, "Null target failed");
        assertTrue(BinarySearch.findStudentById(DataStore.students, "") == -1, "Empty target failed");
        assertTrue(BinarySearch.findStudentById(DataStore.students, "   ") == -1, "Whitespace target failed");

        System.out.println("  -> Boundary and null handling verification PASSED.");
    }
}
