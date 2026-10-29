package org.sgcite.attendance;

import org.sgcite.attendance.data.DataStore;
import org.sgcite.attendance.dsa.CheckInQueue;
import org.sgcite.attendance.dsa.OfficerActionStack;
import org.sgcite.attendance.models.Event;
import org.sgcite.attendance.models.Student;
import org.sgcite.attendance.portals.OfficerPortal;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Scanner;

public class OfficerPortalVerificationTest {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(">>> RUNNING OFFICER PORTAL VERIFICATION TEST (Issue #8)");
        System.out.println("================================================================================");

        testMethodSignatures();
        testManageEvents();
        testAttendanceReport();
        testEndToEndOfficerFlow();

        System.out.println("================================================================================");
        System.out.println("[SUCCESS] All OfficerPortal Issue #8 verification tests passed successfully!");
        System.out.println("================================================================================");
    }

    private static void testMethodSignatures() {
        try {
            Method m1 = OfficerPortal.class.getDeclaredMethod("handleManageEvents", Scanner.class, OfficerActionStack.class);
            assert Modifier.isPrivate(m1.getModifiers()) : "handleManageEvents must be private";
            assert Modifier.isStatic(m1.getModifiers()) : "handleManageEvents must be static";

            Method m2 = OfficerPortal.class.getDeclaredMethod("handleAttendanceReport", CheckInQueue.class);
            assert Modifier.isPrivate(m2.getModifiers()) : "handleAttendanceReport must be private";
            assert Modifier.isStatic(m2.getModifiers()) : "handleAttendanceReport must be static";

            System.out.println("[✓] Exact method signatures verified via reflection.");
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Required method signature not found: " + e.getMessage(), e);
        }
    }

    private static void testManageEvents() {
        DataStore.seedData(false);
        int initialEventCount = DataStore.events.size();
        assert initialEventCount == 1 : "Expected 1 initial default event";

        OfficerActionStack auditStack = new OfficerActionStack();
        String simulatedInput = String.join("\n",
                "SG CITE Hackathon 2026",
                "BSIT and BSCS Students",
                "November 15, 2026 | 09:00 AM - 06:00 PM",
                "CBEAM Computer Labs",
                "Foster algorithmic thinking and problem solving",
                "Form teams of 3 and register via portal"
        ) + "\n";

        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        OfficerPortal.runManageEvents(scanner, auditStack);

        assert DataStore.events.size() == initialEventCount + 1 : "Event count should increment by 1";
        Event createdEvent = DataStore.events.get(DataStore.events.size() - 1);
        assert "SG CITE Hackathon 2026".equals(createdEvent.getWhat()) : "Event 'what' mismatch";
        assert "BSIT and BSCS Students".equals(createdEvent.getWho()) : "Event 'who' mismatch";
        assert "November 15, 2026 | 09:00 AM - 06:00 PM".equals(createdEvent.getWhen()) : "Event 'when' mismatch";
        assert "CBEAM Computer Labs".equals(createdEvent.getWhere()) : "Event 'where' mismatch";
        assert "Foster algorithmic thinking and problem solving".equals(createdEvent.getWhy()) : "Event 'why' mismatch";
        assert "Form teams of 3 and register via portal".equals(createdEvent.getHow()) : "Event 'how' mismatch";

        String lastAction = auditStack.popLastAction();
        assert lastAction.contains("SG CITE Hackathon 2026") : "Audit stack must record event creation: " + lastAction;

        System.out.println("[✓] testManageEvents passed.");
    }

    private static void testAttendanceReport() {
        DataStore.seedData(false);

        CheckInQueue queue = new CheckInQueue();

        Student s1 = null, s2 = null, s3 = null;
        for (Student s : DataStore.students) {
            if ("BSIT-1101".equals(s.getSection()) && s1 == null) {
                s1 = s;
            } else if ("BSIT-2101".equals(s.getSection()) && s2 == null) {
                s2 = s;
            } else if ("BSIT-3102".equals(s.getSection()) && s3 == null) {
                s3 = s;
            }
            if (s1 != null && s2 != null && s3 != null) break;
        }

        assert s1 != null && s2 != null && s3 != null : "Could not find students for benchmark sections";

        s1.setRequirementSubmitted(true);
        s2.setRequirementSubmitted(true);
        s3.setRequirementSubmitted(false);

        queue.enqueueCheckIn(s1);
        queue.enqueueCheckIn(s2);
        queue.enqueueCheckIn(s3);

        assert queue.getPendingCount() == 3 : "Queue should have 3 pending students";

        OfficerPortal.runAttendanceReport(queue);

        assert queue.getPendingCount() == 0 : "Queue must be 0 after report flush";
        assert s1.isCheckedIn() : "Student 1 must be checked in";
        assert s2.isCheckedIn() : "Student 2 must be checked in";
        assert s3.isCheckedIn() : "Student 3 must be checked in";
        assert DataStore.sectionAttendanceMap.get("BSIT-1101") == 1 : "BSIT-1101 count must be 1";
        assert DataStore.sectionAttendanceMap.get("BSIT-2101") == 1 : "BSIT-2101 count must be 1";
        assert DataStore.sectionAttendanceMap.get("BSIT-3102") == 1 : "BSIT-3102 count must be 1";

        System.out.println("[✓] testAttendanceReport passed.");
    }

    private static void testEndToEndOfficerFlow() {
        DataStore.seedData(false);

        CheckInQueue queue = new CheckInQueue();
        OfficerActionStack auditStack = new OfficerActionStack();

        String simulatedInput = String.join("\n",
                "24-00105",
                "sgcite2026",
                "2",
                "NO",
                "3",
                "YES"
        ) + "\n";

        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        OfficerPortal.startOfficerFlow(scanner, queue, auditStack);

        String recentAction = auditStack.popLastAction();
        assert recentAction.contains("24-00105") : "Audit stack must record login: " + recentAction;

        System.out.println("[✓] testEndToEndOfficerFlow passed.");
    }
}
