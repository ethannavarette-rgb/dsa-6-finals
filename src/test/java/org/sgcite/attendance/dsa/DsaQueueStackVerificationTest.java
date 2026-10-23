package org.sgcite.attendance.dsa;

import org.sgcite.attendance.models.Student;

import java.util.HashMap;
import java.util.Map;

public class DsaQueueStackVerificationTest {
    public static void main(String[] args) {
        Student first = new Student("24-00105", "PUREZA, IRISH MAY", "BSIT-2101");
        Student second = new Student("24-00106", "Dela Cruz, Juan", "BSIT-2101");
        Student duplicate = new Student("24-00105", "PUREZA, IRISH MAY", "BSIT-2101");

        CheckInQueue queue = new CheckInQueue();
        assert queue.enqueueCheckIn(first) : "First check-in should succeed";
        assert queue.enqueueCheckIn(second) : "Second check-in should succeed";
        assert !queue.enqueueCheckIn(duplicate) : "Duplicate student ID should be rejected";
        assert queue.getPendingCount() == 2 : "Queue should contain 2 pending students";

        Map<String, Integer> sectionMap = new HashMap<>();
        int processed = queue.flushQueueToAttendance(sectionMap);
        assert processed == 2 : "Exactly 2 students should be processed";
        assert queue.getPendingCount() == 0 : "Queue should be empty after flush";
        assert first.isCheckedIn() : "First student should be marked checked in";
        assert second.isCheckedIn() : "Second student should be marked checked in";
        assert sectionMap.get("BSIT-2101") == 2 : "Section tally should record both students";

        OfficerActionStack stack = new OfficerActionStack();
        stack.pushAction("Arrival verified at gate 2");
        stack.pushAction("Officer roster updated");
        assert "Officer roster updated".equals(stack.popLastAction()) : "LIFO behavior failed";
        assert "Arrival verified at gate 2".equals(stack.popLastAction()) : "Second pop should return earlier action";
        assert "No recent actions.".equals(stack.popLastAction()) : "Empty stack should return fallback message";

        stack.pushAction("Daily assembly started");
        stack.displayAuditHistory();

        System.out.println("[SUCCESS] Queue & Stack verification passed.");
    }
}
