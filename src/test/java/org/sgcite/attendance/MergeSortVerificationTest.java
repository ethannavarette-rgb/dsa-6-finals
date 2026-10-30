package org.sgcite.attendance;

import org.sgcite.attendance.data.DataStore;
import org.sgcite.attendance.dsa.MergeSort;
import org.sgcite.attendance.models.Officer;
import org.sgcite.attendance.models.Student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Automated Verification Test for Issue #2: DSA Engine - MergeSort (O(n log n) Stable Sort).
 */
public class MergeSortVerificationTest {

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            System.err.println("[FAIL] " + message);
            throw new AssertionError(message);
        }
    }

    public static void main(String[] args) {
        System.out.println(">>> RUNNING MERGESORT VERIFICATION TESTS <<<\n");

        testGenericMergeSort();
        testSortAlreadySortedAndReverseSorted();
        testSortDuplicateKeys();
        testSort1500StudentsById();
        testSortOfficersById();
        testSortStabilityBySection();
        testBoundaryAndNullHandling();

        System.out.println("\n[SUCCESS] ALL MERGESORT VERIFICATION TESTS PASSED SUCCESSFULLY! (Issue #2 Verified)");
    }

    private static void testSortAlreadySortedAndReverseSorted() {
        System.out.println("[TEST] Verifying MergeSort on already-sorted and reverse-sorted lists...");

        // Already sorted
        List<Integer> alreadySorted = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        MergeSort.sort(alreadySorted, Integer::compareTo);
        assertTrue(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8).equals(alreadySorted), "Already sorted list failed");

        // Reverse sorted
        List<Integer> reverseSorted = new ArrayList<>(Arrays.asList(8, 7, 6, 5, 4, 3, 2, 1));
        MergeSort.sort(reverseSorted, Integer::compareTo);
        assertTrue(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8).equals(reverseSorted), "Reverse sorted list failed");

        System.out.println("  -> Already-sorted and reverse-sorted MergeSort verification PASSED.");
    }

    private static void testSortDuplicateKeys() {
        System.out.println("[TEST] Verifying MergeSort with duplicate keys...");

        List<Integer> duplicates = new ArrayList<>(Arrays.asList(5, 1, 3, 5, 2, 1, 3, 5, 2));
        MergeSort.sort(duplicates, Integer::compareTo);
        assertTrue(Arrays.asList(1, 1, 2, 2, 3, 3, 5, 5, 5).equals(duplicates), "Duplicate keys sorting failed: " + duplicates);

        System.out.println("  -> Duplicate keys MergeSort verification PASSED.");
    }

    private static void testGenericMergeSort() {
        System.out.println("[TEST 1] Verifying Generic MergeSort on primitive wrappers...");

        List<Integer> numbers = new ArrayList<>(Arrays.asList(42, 17, 89, 5, 23, 1, 99, 12));
        MergeSort.sort(numbers, Integer::compareTo);
        List<Integer> expected = Arrays.asList(1, 5, 12, 17, 23, 42, 89, 99);
        assertTrue(expected.equals(numbers), "Generic Integer sorting failed: " + numbers);

        List<String> words = new ArrayList<>(Arrays.asList("Zebra", "Apple", "Mango", "Banana", "Cherry"));
        MergeSort.sort(words, String::compareToIgnoreCase);
        List<String> expectedWords = Arrays.asList("Apple", "Banana", "Cherry", "Mango", "Zebra");
        assertTrue(expectedWords.equals(words), "Generic String sorting failed: " + words);

        System.out.println("  -> Generic MergeSort verification PASSED.");
    }

    private static void testSort1500StudentsById() {
        System.out.println("[TEST 2] Verifying MergeSort on 1,500 DataStore Students by Student ID...");
        DataStore.seedData(false);

        // Verify dataset is initially unsorted
        boolean initiallySorted = true;
        for (int i = 0; i < DataStore.students.size() - 1; i++) {
            if (DataStore.students.get(i).getStudentId().compareTo(DataStore.students.get(i + 1).getStudentId()) > 0) {
                initiallySorted = false;
                break;
            }
        }
        assertTrue(!initiallySorted, "Students list should be unsorted initially");

        // Benchmark MergeSort execution time
        long startTime = System.nanoTime();
        MergeSort.sort(DataStore.students, MergeSort.COMPARE_STUDENT_BY_ID);
        long durationNs = System.nanoTime() - startTime;
        double durationMs = durationNs / 1_000_000.0;

        // Print visual ASCII terminal banner matching project specifications
        MergeSort.printExecutionSummary();
        System.out.printf("Diagnostic Metric: Sorted 1,500 records in %.2f ms (%d ns)%n", durationMs, durationNs);

        // Verify full ascending order for all 1,500 items
        assertTrue(DataStore.students.size() == 1500, "Student list count corrupted");
        for (int i = 0; i < DataStore.students.size() - 1; i++) {
            String currentId = DataStore.students.get(i).getStudentId();
            String nextId = DataStore.students.get(i + 1).getStudentId();
            assertTrue(currentId.compareToIgnoreCase(nextId) <= 0,
                    String.format("Sort violation at index %d: %s > %s", i, currentId, nextId));
        }

        // Verify boundary records
        assertTrue("24-00001".equals(DataStore.students.get(0).getStudentId()), "First student ID should be 24-00001");
        assertTrue("24-01500".equals(DataStore.students.get(1499).getStudentId()), "Last student ID should be 24-01500");

        System.out.println("  -> 1,500-Student MergeSort verification PASSED.");
    }

    private static void testSortOfficersById() {
        System.out.println("[TEST 3] Verifying MergeSort on 12 SG CITE Officers by Student ID...");
        DataStore.seedData(false);

        MergeSort.sort(DataStore.officers, MergeSort.COMPARE_OFFICER_BY_ID);

        assertTrue(DataStore.officers.size() == 12, "Officer list count corrupted");
        for (int i = 0; i < DataStore.officers.size() - 1; i++) {
            String currentId = DataStore.officers.get(i).getStudentId();
            String nextId = DataStore.officers.get(i + 1).getStudentId();
            assertTrue(currentId.compareToIgnoreCase(nextId) <= 0,
                    String.format("Officer sort violation at index %d: %s > %s", i, currentId, nextId));
        }

        // 24-00105 (President) is lowest student ID among seeded officers
        assertTrue("24-00105".equals(DataStore.officers.get(0).getStudentId()), "First officer should be 24-00105");

        System.out.println("  -> Officer MergeSort verification PASSED.");
    }

    private static void testSortStabilityBySection() {
        System.out.println("[TEST 4] Verifying MergeSort Algorithmic Stability (FIFO preservation)...");

        // Create a list with interleaved sections and explicit arrival sequence numbers
        List<Student> attendees = new ArrayList<>();
        attendees.add(new Student("24-00010", "ATTENDEE_A1", "BSIT-2101")); // Section 2101, Seq 1
        attendees.add(new Student("24-00020", "ATTENDEE_B1", "BSIT-1101")); // Section 1101, Seq 1
        attendees.add(new Student("24-00030", "ATTENDEE_A2", "BSIT-2101")); // Section 2101, Seq 2
        attendees.add(new Student("24-00040", "ATTENDEE_C1", "BSIT-3101")); // Section 3101, Seq 1
        attendees.add(new Student("24-00050", "ATTENDEE_B2", "BSIT-1101")); // Section 1101, Seq 2
        attendees.add(new Student("24-00060", "ATTENDEE_A3", "BSIT-2101")); // Section 2101, Seq 3
        attendees.add(new Student("24-00070", "ATTENDEE_C2", "BSIT-3101")); // Section 3101, Seq 2

        // Sort by section only
        MergeSort.sort(attendees, MergeSort.COMPARE_STUDENT_BY_SECTION);

        // Verify sections are grouped alphabetically: BSIT-1101, BSIT-2101, BSIT-3101
        assertTrue("BSIT-1101".equals(attendees.get(0).getSection()), "Section order violation");
        assertTrue("BSIT-1101".equals(attendees.get(1).getSection()), "Section order violation");
        assertTrue("BSIT-2101".equals(attendees.get(2).getSection()), "Section order violation");
        assertTrue("BSIT-2101".equals(attendees.get(3).getSection()), "Section order violation");
        assertTrue("BSIT-2101".equals(attendees.get(4).getSection()), "Section order violation");
        assertTrue("BSIT-3101".equals(attendees.get(5).getSection()), "Section order violation");
        assertTrue("BSIT-3101".equals(attendees.get(6).getSection()), "Section order violation");

        // Verify STABILITY: relative order of elements within the same section is strictly preserved
        assertTrue("ATTENDEE_B1".equals(attendees.get(0).getFullName()), "Stability failed for BSIT-1101 seq 1");
        assertTrue("ATTENDEE_B2".equals(attendees.get(1).getFullName()), "Stability failed for BSIT-1101 seq 2");

        assertTrue("ATTENDEE_A1".equals(attendees.get(2).getFullName()), "Stability failed for BSIT-2101 seq 1");
        assertTrue("ATTENDEE_A2".equals(attendees.get(3).getFullName()), "Stability failed for BSIT-2101 seq 2");
        assertTrue("ATTENDEE_A3".equals(attendees.get(4).getFullName()), "Stability failed for BSIT-2101 seq 3");

        assertTrue("ATTENDEE_C1".equals(attendees.get(5).getFullName()), "Stability failed for BSIT-3101 seq 1");
        assertTrue("ATTENDEE_C2".equals(attendees.get(6).getFullName()), "Stability failed for BSIT-3101 seq 2");

        System.out.println("  -> Algorithmic stability verification PASSED.");
    }

    private static void testBoundaryAndNullHandling() {
        System.out.println("[TEST 5] Verifying Defensive Null and Boundary Handling...");

        // 1. Null list -> no crash
        MergeSort.sort(null, Integer::compareTo);

        // 2. Empty list -> no crash
        List<String> emptyList = new ArrayList<>();
        MergeSort.sort(emptyList, String::compareTo);
        assertTrue(emptyList.isEmpty(), "Empty list should remain empty");

        // 3. Single element list -> no crash, unchanged
        List<String> singleList = new ArrayList<>(Arrays.asList("Solo"));
        MergeSort.sort(singleList, String::compareTo);
        assertTrue(singleList.size() == 1 && "Solo".equals(singleList.get(0)), "Single element list modified");

        // 4. Null comparator -> throws IllegalArgumentException defensively
        boolean caughtException = false;
        try {
            List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));
            MergeSort.sort(list, null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        }
        assertTrue(caughtException, "Expected IllegalArgumentException for null comparator");

        // 5. Comparators null safety for elements
        assertTrue(MergeSort.COMPARE_STUDENT_BY_ID.compare(null, null) == 0, "Null comparison failed");
        assertTrue(MergeSort.COMPARE_STUDENT_BY_SECTION.compare(null, null) == 0, "Null comparison failed");
        assertTrue(MergeSort.COMPARE_OFFICER_BY_ID.compare(null, null) == 0, "Null comparison failed");

        System.out.println("  -> Boundary and null handling verification PASSED.");
    }
}
