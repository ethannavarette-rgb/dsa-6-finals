package sgcite;

import java.util.Comparator;
import java.util.List;

/**
 * Custom implementation of the MergeSort algorithm.
 * Operates in guaranteed O(n log n) time and is stable, ensuring that attendees
 * with identical sort keys maintain their relative insertion order.
 */
public class MergeSort {

    /**
     * Sorter criteria enumeration.
     */
    public enum SortCriteria {
        STUDENT_ID,
        FULL_NAME,
        TIME_IN
    }

    /**
     * Sorts the given list of students using MergeSort based on the chosen criteria.
     *
     * @param list     List of students to be sorted in place.
     * @param criteria The field by which to sort.
     */
    public static void sort(List<Student> list, SortCriteria criteria) {
        if (list == null || list.size() <= 1) {
            return;
        }

        Comparator<Student> comparator = getComparator(criteria);
        mergeSort(list, 0, list.size() - 1, comparator);
    }

    private static Comparator<Student> getComparator(SortCriteria criteria) {
        if (criteria == SortCriteria.FULL_NAME) {
            return (a, b) -> safeCompare(a.getFullName(), b.getFullName());
        } else if (criteria == SortCriteria.TIME_IN) {
            return (a, b) -> safeCompare(a.getTimeIn(), b.getTimeIn());
        }
        // Default: Sort by Student ID
        return (a, b) -> safeCompare(a.getStudentId(), b.getStudentId());
    }

    private static int safeCompare(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareToIgnoreCase(s2);
    }

    private static void mergeSort(List<Student> list, int left, int right, Comparator<Student> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            mergeSort(list, left, mid, comparator);
            mergeSort(list, mid + 1, right, comparator);

            merge(list, left, mid, right, comparator);
        }
    }

    private static void merge(List<Student> list, int left, int mid, int right, Comparator<Student> comparator) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Student[] leftArray = new Student[n1];
        Student[] rightArray = new Student[n2];

        for (int i = 0; i < n1; i++) {
            leftArray[i] = list.get(left + i);
        }
        for (int j = 0; j < n2; j++) {
            rightArray[j] = list.get(mid + 1 + j);
        }

        int i = 0;
        int j = 0;
        int k = left;

        // Merge the temporary arrays
        boolean hasRemainingElements = (i < n1 && j < n2);
        while (hasRemainingElements) {
            if (comparator.compare(leftArray[i], rightArray[j]) <= 0) {
                list.set(k, leftArray[i]);
                i++;
            } else {
                list.set(k, rightArray[j]);
                j++;
            }
            k++;
            hasRemainingElements = (i < n1 && j < n2);
        }

        // Copy remaining elements of leftArray, if any
        boolean hasLeftRemaining = (i < n1);
        while (hasLeftRemaining) {
            list.set(k, leftArray[i]);
            i++;
            k++;
            hasLeftRemaining = (i < n1);
        }

        // Copy remaining elements of rightArray, if any
        boolean hasRightRemaining = (j < n2);
        while (hasRightRemaining) {
            list.set(k, rightArray[j]);
            j++;
            k++;
            hasRightRemaining = (j < n2);
        }
    }
}
