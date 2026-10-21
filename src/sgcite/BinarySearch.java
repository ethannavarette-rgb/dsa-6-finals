package sgcite;

import java.util.List;

/**
 * Custom implementation of the Binary Search algorithm.
 * Operates in O(log n) logarithmic time on a list pre-sorted by Student ID.
 * For a population of 300 to 500 attendees, it finds any record in at most 9 comparisons.
 */
public class BinarySearch {

    /**
     * Encapsulates the outcome of a binary search operation, including performance diagnostics.
     */
    public static class SearchResult {
        private final int index;
        private final Student student;
        private final int comparisons;

        public SearchResult(int index, Student student, int comparisons) {
            this.index = index;
            this.student = student;
            this.comparisons = comparisons;
        }

        public boolean isFound() {
            return index >= 0 && student != null;
        }

        public int getIndex() {
            return index;
        }

        public Student getStudent() {
            return student;
        }

        public int getComparisons() {
            return comparisons;
        }
    }

    /**
     * Performs a binary search for a student matching the target Student ID.
     * Assumes the input list is pre-sorted by Student ID in ascending order.
     *
     * @param sortedList The sorted list of attendees.
     * @param targetId   The Student ID to search for.
     * @return SearchResult containing the found student and total comparisons performed.
     */
    public static SearchResult searchById(List<Student> sortedList, String targetId) {
        if (sortedList == null || sortedList.isEmpty() || targetId == null || targetId.trim().isEmpty()) {
            return new SearchResult(-1, null, 0);
        }

        String cleanedTarget = targetId.trim();
        int low = 0;
        int high = sortedList.size() - 1;
        int comparisons = 0;

        boolean isSearching = (low <= high);
        while (isSearching) {
            int mid = low + (high - low) / 2;
            Student midStudent = sortedList.get(mid);
            String midId = midStudent.getStudentId();

            comparisons++;
            int cmp = cleanedTarget.compareToIgnoreCase(midId);

            if (cmp == 0) {
                return new SearchResult(mid, midStudent, comparisons);
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }

            isSearching = (low <= high);
        }

        return new SearchResult(-1, null, comparisons);
    }
}
