package org.sgcite.attendance.dsa;

import org.sgcite.attendance.models.Officer;
import org.sgcite.attendance.models.Student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Generic, crash-proof implementation of the Divide-and-Conquer MergeSort algorithm.
 * Operates in guaranteed O(n log n) worst-case time complexity.
 * Implements a strictly stable merge (<= 0) to preserve FIFO chronological check-in
 * ordering for attendees with identical keys (e.g., within the same academic section).
 */
public class MergeSort {

    // =========================================================================
    // HELPER COMPARATORS
    // =========================================================================

    /**
     * Comparator for sorting Student records by Student ID in ascending lexicographical order.
     * Includes null-safety guards.
     */
    public static final Comparator<Student> COMPARE_STUDENT_BY_ID = (s1, s2) -> {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        String id1 = s1.getStudentId();
        String id2 = s2.getStudentId();
        if (id1 == null && id2 == null) return 0;
        if (id1 == null) return -1;
        if (id2 == null) return 1;
        return id1.compareToIgnoreCase(id2);
    };

    /**
     * Comparator for sorting Student records by Section / Block in ascending alphabetical order.
     * Includes null-safety guards.
     */
    public static final Comparator<Student> COMPARE_STUDENT_BY_SECTION = (s1, s2) -> {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        String sec1 = s1.getSection();
        String sec2 = s2.getSection();
        if (sec1 == null && sec2 == null) return 0;
        if (sec1 == null) return -1;
        if (sec2 == null) return 1;
        return sec1.compareToIgnoreCase(sec2);
    };

    /**
     * Comparator for sorting Officer records by Student ID in ascending lexicographical order.
     * Includes null-safety guards.
     */
    public static final Comparator<Officer> COMPARE_OFFICER_BY_ID = (o1, o2) -> {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;
        String id1 = o1.getStudentId();
        String id2 = o2.getStudentId();
        if (id1 == null && id2 == null) return 0;
        if (id1 == null) return -1;
        if (id2 == null) return 1;
        return id1.compareToIgnoreCase(id2);
    };

    // Named aliases for flexibility across project conventions
    public static final Comparator<Student> STUDENT_BY_ID = COMPARE_STUDENT_BY_ID;
    public static final Comparator<Student> STUDENT_BY_SECTION = COMPARE_STUDENT_BY_SECTION;
    public static final Comparator<Officer> OFFICER_BY_ID = COMPARE_OFFICER_BY_ID;
    public static final Comparator<Officer> OFFICER_BY_STUDENT_ID = COMPARE_OFFICER_BY_ID;
    public static final Comparator<Student> BY_STUDENT_ID = COMPARE_STUDENT_BY_ID;
    public static final Comparator<Student> BY_SECTION = COMPARE_STUDENT_BY_SECTION;

    public static Comparator<Student> studentById() {
        return COMPARE_STUDENT_BY_ID;
    }

    public static Comparator<Student> studentBySection() {
        return COMPARE_STUDENT_BY_SECTION;
    }

    public static Comparator<Officer> officerById() {
        return COMPARE_OFFICER_BY_ID;
    }

    public static Comparator<Officer> officerByStudentId() {
        return COMPARE_OFFICER_BY_ID;
    }

    // =========================================================================
    // CORE ALGORITHM
    // =========================================================================

    /**
     * Sorts the provided list in-place using Divide-and-Conquer MergeSort.
     * Time Complexity: O(n log n) best, average, and worst-case.
     * Space Complexity: O(n) auxiliary sublist buffers during recursive merging.
     *
     * @param list       the list of elements to sort
     * @param comparator the comparator determining relative order
     * @param <T>        the element type
     */
    public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
        // Base case: null or single element is already sorted
        if (list == null || list.size() <= 1) {
            return;
        }
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator cannot be null");
        }

        // Divide into left and right sublists
        int mid = list.size() / 2;
        List<T> left = new ArrayList<>(list.subList(0, mid));
        List<T> right = new ArrayList<>(list.subList(mid, list.size()));

        // Recursively sort both sublists
        sort(left, comparator);
        sort(right, comparator);

        // Invoke merge to combine back into the result list
        merge(list, left, right, comparator);
    }

    /**
     * Merges two sorted sublists into the result list.
     * Enforces strict stability by using `<= 0` comparison, preserving FIFO
     * check-in order for elements with equivalent keys.
     *
     * @param result the destination list receiving merged elements in sorted order
     * @param left   sorted left sublist
     * @param right  sorted right sublist
     * @param comp   the comparator determining order
     * @param <T>    the element type
     */
    private static <T> void merge(List<T> result, List<T> left, List<T> right, Comparator<? super T> comp) {
        if (result == null || comp == null) {
            return;
        }
        if (left == null && right == null) {
            return;
        }

        int i = 0;
        int j = 0;
        int k = 0;

        int leftSize = (left != null) ? left.size() : 0;
        int rightSize = (right != null) ? right.size() : 0;

        // Stable O(n log n) merge operation preserving chronological order on equal keys
        while (i < leftSize && j < rightSize) {
            // Note: '<= 0' is essential for algorithmic stability
            if (comp.compare(left.get(i), right.get(j)) <= 0) {
                setOrAdd(result, k, left.get(i));
                i++;
            } else {
                setOrAdd(result, k, right.get(j));
                j++;
            }
            k++;
        }

        // Drain remaining elements from left sublist
        while (i < leftSize) {
            setOrAdd(result, k, left.get(i));
            i++;
            k++;
        }

        // Drain remaining elements from right sublist
        while (j < rightSize) {
            setOrAdd(result, k, right.get(j));
            j++;
            k++;
        }
    }

    /**
     * Defensively sets or appends an element into the destination list.
     * Modifies in-place if the list already has capacity at the target index.
     */
    private static <T> void setOrAdd(List<T> list, int index, T element) {
        if (index < list.size()) {
            list.set(index, element);
        } else {
            list.add(element);
        }
    }

    // =========================================================================
    // CONVENIENCE SORT METHODS
    // =========================================================================

    public static void sortStudentsById(List<Student> students) {
        sort(students, COMPARE_STUDENT_BY_ID);
    }

    public static void sortStudentsBySection(List<Student> students) {
        sort(students, COMPARE_STUDENT_BY_SECTION);
    }

    public static void sortOfficersById(List<Officer> officers) {
        sort(officers, COMPARE_OFFICER_BY_ID);
    }

    // =========================================================================
    // VISUAL ASCII TERMINAL SUMMARY
    // =========================================================================

    /**
     * Prints the visual ASCII terminal banner matching the project specification.
     *
     * @param targetDataset dataset name (e.g., "DataStore.students")
     * @param recordCount   number of records processed
     * @param sortComparator description of comparator applied
     */
    public static void printExecutionSummary(String targetDataset, int recordCount, String sortComparator) {
        System.out.println("================================================================================");
        System.out.println(">>> MERGE SORT ENGINE EXECUTION (O(n log n))");
        System.out.println("================================================================================");
        System.out.printf("Target Dataset  : %s (%,d records)%n", targetDataset, recordCount);
        System.out.printf("Sort Comparator : %s%n", sortComparator);
        System.out.println("Algorithm       : Stable Divide-and-Conquer Merge Sort");
        System.out.println("Status          : Indexed -> Ready for O(log n) Binary Search");
        System.out.println("================================================================================");
    }

    /**
     * Prints the default execution summary for 1,500 DataStore students.
     */
    public static void printExecutionSummary() {
        printExecutionSummary("DataStore.students", 1500, "Student::getStudentId (Ascending)");
    }
}
