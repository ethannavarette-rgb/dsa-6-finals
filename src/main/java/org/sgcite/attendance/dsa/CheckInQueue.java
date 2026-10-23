package org.sgcite.attendance.dsa;

import org.sgcite.attendance.models.Student;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * FIFO queue for managing student check-in arrivals while preventing duplicate IDs.
 * Uses a HashSet for O(1) duplicate detection and an ArrayDeque for efficient FIFO operations.
 */
public class CheckInQueue {
    private final Queue<Student> fifoQueue = new ArrayDeque<>();
    private final Set<String> checkedInIds = new HashSet<>();

    public boolean enqueueCheckIn(Student student) {
        if (student == null) {
            return false;
        }

        String studentId = student.getStudentId();
        if (studentId == null || studentId.trim().isEmpty()) {
            return false;
        }

        String normalizedId = studentId.trim();
        if (checkedInIds.contains(normalizedId)) {
            return false;
        }

        checkedInIds.add(normalizedId);
        fifoQueue.offer(student);
        return true;
    }

    public int flushQueueToAttendance(Map<String, Integer> sectionMap) {
        if (sectionMap == null) {
            return 0;
        }

        int processed = 0;
        while (!fifoQueue.isEmpty()) {
            Student student = fifoQueue.poll();
            if (student == null) {
                continue;
            }

            student.setCheckedIn(true);
            String section = student.getSection();
            if (section == null || section.trim().isEmpty()) {
                section = "UNKNOWN";
            }

            String normalizedSection = section.trim();
            int currentCount = sectionMap.getOrDefault(normalizedSection, 0);
            sectionMap.put(normalizedSection, currentCount + 1);
            processed++;
        }

        return processed;
    }

    public int getPendingCount() {
        return fifoQueue.size();
    }
}
