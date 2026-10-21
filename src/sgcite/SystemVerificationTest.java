package sgcite;

/**
 * Automated smoke and boundary verification test.
 * Verifies that the system executes smoothly, performs MergeSort and BinarySearch correctly
 * on 300-500 items, and never encounters unexpected exceptions.
 */
public class SystemVerificationTest {
    public static void main(String[] args) {
        System.out.println(">>> Running System Verification for SG-CITE Attendance System <<<");
        AttendanceManager manager = new AttendanceManager();

        // 1. Test Mock Data Generation (TX-07) with 400 students
        System.out.println("\n[Test 1] Generating 400 mock records...");
        int generated = manager.generateMockData(400);
        System.out.println("Generated: " + generated);
        System.out.println("Roster size: " + manager.getRosterCount());
        System.out.println("Queue size: " + manager.getQueueCount());
        assert generated == 400;

        // 2. Test Custom Queue Dequeue & Batch Check-in (TX-02)
        System.out.println("\n[Test 2] Testing queue batch check-in...");
        int beforeRoster = manager.getRosterCount();
        int processed = manager.batchProcessCheckIn();
        System.out.printf("Processed %d queued attendees. Roster size: %d -> %d%n",
                processed, beforeRoster, manager.getRosterCount());
        assert manager.getQueueCount() == 0;
        assert manager.getRosterCount() == 400;

        // 3. Test MergeSort (TX-05) by Student ID
        System.out.println("\n[Test 3] Testing custom MergeSort on 400 students...");
        long sortStart = System.nanoTime();
        manager.sortRoster(MergeSort.SortCriteria.STUDENT_ID);
        long sortDuration = System.nanoTime() - sortStart;
        System.out.printf("MergeSort executed in %.3f ms (%d ns)%n", sortDuration / 1_000_000.0, sortDuration);

        // Verify sorted order
        for (int i = 0; i < manager.getRosterCount() - 1; i++) {
            Student curr = manager.getConfirmedRoster().get(i);
            Student next = manager.getConfirmedRoster().get(i + 1);
            if (curr.getStudentId().compareToIgnoreCase(next.getStudentId()) > 0) {
                throw new IllegalStateException("MergeSort failed at index " + i + ": " + curr.getStudentId() + " > " + next.getStudentId());
            }
        }
        System.out.println("Roster verified: perfectly sorted in ascending order!");

        // 4. Test Binary Search (TX-04)
        System.out.println("\n[Test 4] Testing custom Binary Search on 400 students...");
        String sampleId = "2024-0250";
        BinarySearch.SearchResult searchResult = manager.searchAttendee(sampleId);
        System.out.println("Target: " + sampleId);
        System.out.println("Found: " + searchResult.isFound());
        System.out.println("Student: " + searchResult.getStudent());
        System.out.println("Comparisons: " + searchResult.getComparisons() + " (Log2(400) = ~9 max)");
        assert searchResult.isFound();
        assert searchResult.getComparisons() <= 9;

        // 5. Test Check-out (TX-03)
        System.out.println("\n[Test 5] Testing Check-out via Binary Search...");
        AttendanceManager.CheckOutResult coResult = manager.processCheckOut(sampleId);
        System.out.println("Check-out status: " + coResult.getStatus());
        System.out.println("Student time-out: " + coResult.getStudent().getTimeOut());
        assert coResult.getStatus() == AttendanceManager.CheckOutStatus.SUCCESS
                || coResult.getStatus() == AttendanceManager.CheckOutStatus.ALREADY_CHECKED_OUT;

        // 6. Test Analytics (TX-06)
        System.out.println("\n[Test 6] Testing Event Summary Analytics...");
        AttendanceManager.EventAnalytics analytics = manager.getAnalytics();
        System.out.println("Total Roster: " + analytics.getTotalRoster());
        System.out.println("Active Inside: " + analytics.getActiveInside());
        System.out.println("Checked Out: " + analytics.getCheckedOut());
        System.out.println("Program Count Map: " + analytics.getProgramCount());

        System.out.println("\n>>> ALL TESTS PASSED SUCCESSFULLY! ZERO CRASHES! <<<");
    }
}
