package org.sgcite.attendance.dsa;

import org.sgcite.attendance.models.Student;

import java.util.HashMap;
import java.util.Map;

public class DsaQueueStackVerificationTest {
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            System.err.println("[FAIL] " + message);
            throw new AssertionError(message);
        }
    }

    public static void main(String[] args) {
        testQueueFifoAndDuplicates();
        testQueueEmptyAndRepeatedFlush();
        testStackLifoAndHistory();

        System.out.println("[SUCCESS] Queue & Stack verification passed.");
    }

    private static void testQueueFifoAndDuplicates() {
        Student first = new Student("24-00105", "PUREZA, IRISH MAY", "BSIT-2101");
        Student second = new Student("24-00106", "Dela Cruz, Juan", "BSIT-2101");
        Student duplicate = new Student("24-00105", "PUREZA, IRISH MAY", "BSIT-2101");

        CheckInQueue queue = new CheckInQueue();
        assertTrue(queue.enqueueCheckIn(first), "First check-in should succeed");
        assertTrue(queue.enqueueCheckIn(second), "Second check-in should succeed");
        assertTrue(!queue.enqueueCheckIn(duplicate), "Duplicate student ID should be rejected");
        assertTrue(queue.getPendingCount() == 2, "Queue should contain 2 pending students");

        Map<String, Integer> sectionMap = new HashMap<>();
        int processed = queue.flushQueueToAttendance(sectionMap);
        assertTrue(processed == 2, "Exactly 2 students should be processed");
        assertTrue(queue.getPendingCount() == 0, "Queue should be empty after flush");
        assertTrue(first.isCheckedIn(), "First student should be marked checked in");
        assertTrue(second.isCheckedIn(), "Second student should be marked checked in");
        assertTrue(sectionMap.get("BSIT-2101") == 2, "Section tally should record both students");

        // Verify that already checked-in student cannot be enqueued again
        assertTrue(!queue.enqueueCheckIn(first), "Already checked-in student must be rejected from enqueueing");
    }

    private static void testQueueEmptyAndRepeatedFlush() {
        CheckInQueue queue = new CheckInQueue();
        Map<String, Integer> sectionMap = new HashMap<>();

        // Flush empty queue
        assertTrue(queue.getPendingCount() == 0, "Initial pending count must be 0");
        int processed = queue.flushQueueToAttendance(sectionMap);
        assertTrue(processed == 0, "Flushing empty queue must return 0");

        // Repeated flush
        int repeated = queue.flushQueueToAttendance(sectionMap);
        assertTrue(repeated == 0, "Repeated flush must return 0");

        // Null section map handling
        assertTrue(queue.flushQueueToAttendance(null) == 0, "Flush with null map must return 0");
    }

    private static void testStackLifoAndHistory() {
        OfficerActionStack stack = new OfficerActionStack();

        // Empty stack verification
        assertTrue(stack.isEmpty(), "Stack must be empty initially");
        assertTrue(stack.size() == 0, "Initial stack size must be 0");
        assertTrue("No recent actions.".equals(stack.popLastAction()), "Empty stack pop should return fallback message");
        assertTrue("No recent actions.".equals(stack.peekLastAction()), "Empty stack peek should return fallback message");
        stack.displayAuditHistory(); // Should gracefully handle empty stack without crashing

        // Push actions
        stack.pushAction("Action 1: System started");
        stack.pushAction("Action 2: Arrival verified at gate 2");
        stack.pushAction("Action 3: Officer roster updated");

        assertTrue(!stack.isEmpty(), "Stack must not be empty after pushes");
        assertTrue(stack.size() == 3, "Stack size must be 3");

        // Verify LIFO order
        assertTrue("Action 3: Officer roster updated".equals(stack.peekLastAction()), "Peek should return newest action");
        assertTrue("Action 3: Officer roster updated".equals(stack.popLastAction()), "First pop must be newest action (LIFO)");
        assertTrue("Action 2: Arrival verified at gate 2".equals(stack.popLastAction()), "Second pop must be second newest");
        assertTrue("Action 1: System started".equals(stack.popLastAction()), "Third pop must be oldest action");
        assertTrue("No recent actions.".equals(stack.popLastAction()), "Stack should be empty again");

        // Display history
        stack.pushAction("Daily assembly started");
        stack.displayAuditHistory();
    }
}
