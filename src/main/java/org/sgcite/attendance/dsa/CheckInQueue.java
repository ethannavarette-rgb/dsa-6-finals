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

        if (student.isCheckedIn()) {
            return false;
        }

        String studentId = student.getStudentId();
        if (studentId == null || studentId.trim().isEmpty()) {
            return false;
        }

        String normalizedId = studentId.trim().toUpperCase();
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

            boolean wasAlreadyCheckedIn = student.isCheckedIn();
            student.setCheckedIn(true);

            String section = student.getSection();
            if (section == null || section.trim().isEmpty()) {
                section = "UNKNOWN";
            }

            String normalizedSection = section.trim();
            if (!wasAlreadyCheckedIn) {
                int currentCount = sectionMap.getOrDefault(normalizedSection, 0);
                sectionMap.put(normalizedSection, currentCount + 1);
                processed++;
            }
        }

        return processed;
    }

    public int getPendingCount() {
        return fifoQueue.size();
    }

    public boolean isEmpty() {
        return fifoQueue.isEmpty();
    }

    public boolean isPending(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return false;
        }
        String normalized = studentId.trim().toUpperCase();
        for (Student s : fifoQueue) {
            if (s != null && s.getStudentId() != null && s.getStudentId().trim().equalsIgnoreCase(normalized)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return false;
        }
        return checkedInIds.contains(studentId.trim().toUpperCase());
    }

    public void clear() {
        fifoQueue.clear();
        checkedInIds.clear();
    }
}
