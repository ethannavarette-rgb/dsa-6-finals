package org.sgcite.attendance.dsa;

import org.sgcite.attendance.models.Officer;
import org.sgcite.attendance.models.Student;

import java.util.List;

/**
 * Crash-proof implementation of the logarithmic Binary Search algorithm.
 * Operates in guaranteed O(log n) time complexity on pre-sorted collections.
 * For 1,500 student records, locates any attendee in at most 11 comparisons (ceil(log2(1500)) = 11).
 */
public class BinarySearch {

    /**
     * Encapsulates the diagnostic outcome of a binary search operation.
     * Tracks the matched element, its resolved index, and the exact count of comparisons performed.
     *
     * @param <T> the type of item searched
     */
    public static class SearchResult<T> {
        private final int index;
        private final T item;
        private final int comparisons;

        public SearchResult(int index, T item, int comparisons) {
            this.index = index;
            this.item = item;
            this.comparisons = comparisons;
        }

        public boolean isFound() {
            return index >= 0 && item != null;
        }

        public int getIndex() {
            return index;
        }

        public T getItem() {
            return item;
        }

        public Student getStudent() {
            return (item instanceof Student) ? (Student) item : null;
        }

        public Officer getOfficer() {
            return (item instanceof Officer) ? (Officer) item : null;
        }

        public int getComparisons() {
            return comparisons;
        }
    }

    /**
     * Performs an iterative binary search on a pre-sorted list of Students by target Student ID.
     * Case-insensitive matching is applied.
     *
     * @param sortedStudents list of students pre-sorted by studentId in ascending order
     * @param targetId       unique student ID to locate (e.g., "24-01492")
     * @return 0-based index if found; -1 if not found or if parameters are null/empty
     */
    public static int findStudentById(List<Student> sortedStudents, String targetId) {
        if (sortedStudents == null || sortedStudents.isEmpty() || targetId == null) {
            return -1;
        }

        String cleanedTarget = targetId.trim();
        if (cleanedTarget.isEmpty()) {
            return -1;
        }

        int low = 0;
        int high = sortedStudents.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Student midStudent = sortedStudents.get(mid);
            if (midStudent == null || midStudent.getStudentId() == null) {
                return -1;
            }

            int cmp = cleanedTarget.compareToIgnoreCase(midStudent.getStudentId());
            if (cmp == 0) {
                return mid;
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return -1;
    }

    /**
     * Performs an iterative binary search on a pre-sorted list of SG CITE Officers by target Student ID.
     * Case-insensitive matching is applied.
     *
     * @param sortedOfficers list of officers pre-sorted by studentId in ascending order
     * @param targetId       unique student ID to locate (e.g., "24-00105")
     * @return 0-based index if found; -1 if not found or if parameters are null/empty
     */
    public static int findOfficerById(List<Officer> sortedOfficers, String targetId) {
        if (sortedOfficers == null || sortedOfficers.isEmpty() || targetId == null) {
            return -1;
        }

        String cleanedTarget = targetId.trim();
        if (cleanedTarget.isEmpty()) {
            return -1;
        }

        int low = 0;
        int high = sortedOfficers.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Officer midOfficer = sortedOfficers.get(mid);
            if (midOfficer == null || midOfficer.getStudentId() == null) {
                return -1;
            }

            int cmp = cleanedTarget.compareToIgnoreCase(midOfficer.getStudentId());
            if (cmp == 0) {
                return mid;
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return -1;
    }

    /**
     * Performs binary search on students with full diagnostic comparison tracking.
     *
     * @param sortedStudents list of students pre-sorted by studentId
     * @param targetId       student ID to locate
     * @return SearchResult containing the found student, resolved index, and comparison iterations
     */
    public static SearchResult<Student> findStudentWithDiagnostics(List<Student> sortedStudents, String targetId) {
        if (sortedStudents == null || sortedStudents.isEmpty() || targetId == null) {
            return new SearchResult<>(-1, null, 0);
        }

        String cleanedTarget = targetId.trim();
        if (cleanedTarget.isEmpty()) {
            return new SearchResult<>(-1, null, 0);
        }

        int low = 0;
        int high = sortedStudents.size() - 1;
        int comparisons = 0;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Student midStudent = sortedStudents.get(mid);
            if (midStudent == null || midStudent.getStudentId() == null) {
                return new SearchResult<>(-1, null, comparisons);
            }

            comparisons++;
            int cmp = cleanedTarget.compareToIgnoreCase(midStudent.getStudentId());
            if (cmp == 0) {
                return new SearchResult<>(mid, midStudent, comparisons);
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return new SearchResult<>(-1, null, comparisons);
    }

    /**
     * Performs binary search on officers with full diagnostic comparison tracking.
     *
     * @param sortedOfficers list of officers pre-sorted by studentId
     * @param targetId       officer student ID to locate
     * @return SearchResult containing the found officer, resolved index, and comparison iterations
     */
    public static SearchResult<Officer> findOfficerWithDiagnostics(List<Officer> sortedOfficers, String targetId) {
        if (sortedOfficers == null || sortedOfficers.isEmpty() || targetId == null) {
            return new SearchResult<>(-1, null, 0);
        }

        String cleanedTarget = targetId.trim();
        if (cleanedTarget.isEmpty()) {
            return new SearchResult<>(-1, null, 0);
        }

        int low = 0;
        int high = sortedOfficers.size() - 1;
        int comparisons = 0;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Officer midOfficer = sortedOfficers.get(mid);
            if (midOfficer == null || midOfficer.getStudentId() == null) {
                return new SearchResult<>(-1, null, comparisons);
            }

            comparisons++;
            int cmp = cleanedTarget.compareToIgnoreCase(midOfficer.getStudentId());
            if (cmp == 0) {
                return new SearchResult<>(mid, midOfficer, comparisons);
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return new SearchResult<>(-1, null, comparisons);
    }

    /**
     * Executes binary search for a student and displays the visual ASCII validation summary.
     *
     * @param sortedStudents list of students pre-sorted by studentId
     * @param targetId       target student ID to search
     * @return the diagnostic SearchResult
     */
    public static SearchResult<Student> printSearchValidation(List<Student> sortedStudents, String targetId) {
        SearchResult<Student> result = findStudentWithDiagnostics(sortedStudents, targetId);
        int totalRecords = (sortedStudents != null) ? sortedStudents.size() : 0;
        int maxTheoretical = (totalRecords > 0) ? (int) Math.ceil(Math.log(totalRecords) / Math.log(2)) : 0;

        System.out.println("================================================================================");
        System.out.println(">>> BINARY SEARCH VALIDATION (O(log n))");
        System.out.println("================================================================================");
        System.out.printf("Search Space    : %,d Pre-Sorted Student Records%n", totalRecords);
        System.out.printf("Target ID       : %s%n", (targetId != null ? targetId.trim() : "null"));
        System.out.printf("Iterations      : %d comparisons (Max Theoretical: %d)%n",
                result.getComparisons(), maxTheoretical);
        if (result.isFound()) {
            Student s = result.getItem();
            System.out.printf("Result          : FOUND -> [%s] %s (%s)%n",
                    s.getStudentId(), s.getFullName(), s.getSection());
        } else {
            System.out.println("Result          : NOT FOUND -> Record does not exist in search space");
        }
        System.out.println("================================================================================");

        return result;
    }
}
