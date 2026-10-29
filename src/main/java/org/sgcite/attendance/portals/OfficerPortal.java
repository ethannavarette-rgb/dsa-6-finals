package org.sgcite.attendance.portals;

import org.sgcite.attendance.data.DataStore;
import org.sgcite.attendance.dsa.BinarySearch;
import org.sgcite.attendance.dsa.CheckInQueue;
import org.sgcite.attendance.dsa.MergeSort;
import org.sgcite.attendance.dsa.OfficerActionStack;
import org.sgcite.attendance.models.Event;
import org.sgcite.attendance.models.Officer;
import org.sgcite.attendance.models.Student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class OfficerPortal {

    public static void startOfficerFlow(Scanner scanner, CheckInQueue queue, OfficerActionStack auditStack) {
        MergeSort.sortOfficersById(DataStore.officers);

        System.out.println("================================================================================");
        System.out.println(">>> SG CITE OFFICER AUTHENTICATION");
        System.out.println("================================================================================");

        boolean retryLogin = true;
        while (retryLogin) {
            System.out.print("Enter Student ID: ");
            String inputId = scanner.nextLine().trim();

            System.out.print("Enter Password: ");
            String inputPassword = scanner.nextLine();

            int officerIndex = BinarySearch.findOfficerById(
                    DataStore.officers, inputId);
            if (officerIndex >= 0) {
                Officer officer = DataStore.officers.get(officerIndex);
                if (officer.getPassword().equals(inputPassword)) {
                    auditStack.pushAction("Officer login: "
                            + officer.getStudentId() + " (" + officer.getPosition() + ")");
                    showOfficerDashboard(scanner, officer, queue, auditStack);
                    return;
                }
            }

            System.out.println("[!] ERROR: Access Denied");
            if (confirmExit(scanner, "Exit to Main Role Menu? (YES/NO): ")) {
                retryLogin = false;
            }
        }
    }

    private static boolean confirmExit(Scanner scanner) {
        return confirmExit(scanner, "Exit to Main Role Menu? (YES/NO): ");
    }

    private static boolean confirmExit(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("YES")) {
                return true;
            } else if (answer.equalsIgnoreCase("NO")) {
                return false;
            } else {
                System.out.println("Invalid choice. Please enter YES or NO.");
            }
        }
    }

    private static void showOfficerDashboard(Officer officer, CheckInQueue queue) {
        showOfficerDashboard(new Scanner(System.in), officer, queue, new OfficerActionStack());
    }

    private static void showOfficerDashboard(Scanner scanner, Officer officer, CheckInQueue queue, OfficerActionStack auditStack) {
        System.out.println("[✓] Whitelist Verified: Officer "
                + officer.getFullName() + " (" + officer.getPosition() + ")");

        boolean inDashboard = true;
        while (inDashboard) {
            System.out.println("================================================================================");
            System.out.println(">>> SG CITE OFFICER DASHBOARD");
            System.out.println("Welcome, " + officer.getFullName() + ".");
            System.out.println("Pending Check-Ins: " + (queue != null ? queue.getPendingCount() : 0));
            System.out.println("================================================================================");
            System.out.println("[1] Manage Events");
            System.out.println("[2] Attendance Report");
            System.out.println("[3] Exit");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.print("Select transaction [1-3]: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleManageEvents(scanner, auditStack);
                    if (confirmExit(scanner, "Exit Officer Dashboard? (YES/NO): ")) {
                        inDashboard = false;
                    }
                    break;

                case "2":
                    handleAttendanceReport(queue);
                    if (confirmExit(scanner, "Exit Officer Dashboard? (YES/NO): ")) {
                        inDashboard = false;
                    }
                    break;

                case "3":
                    if (confirmExit(scanner, "Exit Officer Dashboard? (YES/NO): ")) {
                        inDashboard = false;
                    }
                    break;

                default:
                    System.out.println("[!] Invalid option. Please select 1, 2, or 3.");
                    break;
            }
        }
    }

    public static void runManageEvents(Scanner scanner, OfficerActionStack auditStack) {
        handleManageEvents(scanner, auditStack);
    }

    public static void runAttendanceReport(CheckInQueue queue) {
        handleAttendanceReport(queue);
    }

    private static void handleManageEvents(Scanner scanner, OfficerActionStack auditStack) {
        System.out.println("================================================================================");
        System.out.println(">>> ACTIVE EVENTS (" + DataStore.events.size() + ")");
        System.out.println("================================================================================");
        if (DataStore.events.isEmpty()) {
            System.out.println("No active events currently registered.");
        } else {
            for (int i = 0; i < DataStore.events.size(); i++) {
                Event event = DataStore.events.get(i);
                System.out.println("[" + (i + 1) + "] " + event.getWhat());
                System.out.println(event.toFormatted5W1H());
            }
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(">>> ADD NEW EVENT (5W1H CAPTURE)");
        System.out.println("--------------------------------------------------------------------------------");

        System.out.print("Enter WHAT (Event Name/Title)       : ");
        String what = scanner.nextLine().trim();

        System.out.print("Enter WHO (Target Participants)     : ");
        String who = scanner.nextLine().trim();

        System.out.print("Enter WHEN (Date & Time Schedule)   : ");
        String when = scanner.nextLine().trim();

        System.out.print("Enter WHERE (Venue/Location)        : ");
        String where = scanner.nextLine().trim();

        System.out.print("Enter WHY (Objective/Purpose)       : ");
        String why = scanner.nextLine().trim();

        System.out.print("Enter HOW (Mechanics/Execution)     : ");
        String how = scanner.nextLine().trim();

        Event newEvent = new Event(what, who, when, where, why, how);
        DataStore.events.add(newEvent);

        if (auditStack != null) {
            auditStack.pushAction("Event created: " + newEvent.getWhat());
        }

        System.out.println("[✓] Event successfully added to system!");
        System.out.println(newEvent.toFormatted5W1H());
    }

    private static void handleAttendanceReport(CheckInQueue queue) {
        int flushed = 0;
        if (queue != null) {
            flushed = queue.flushQueueToAttendance(DataStore.sectionAttendanceMap);
        }

        System.out.println("================================================================================");
        System.out.println(">>> SG CITE ATTENDANCE REPORT (PER SECTION / BLOCK)");
        System.out.println("================================================================================");
        System.out.printf("Queue Flushed   : %d pending FIFO check-ins processed.%n", flushed);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-24s%-16s%s%n", "SECTION / BLOCK", "ATTENDEES", "REQUIREMENTS SUBMITTED");
        System.out.println("--------------------------------------------------------------------------------");

        // 1. Filter checked-in students
        List<Student> attendees = new ArrayList<>();
        if (DataStore.students != null) {
            for (Student student : DataStore.students) {
                if (student != null && student.isCheckedIn()) {
                    attendees.add(student);
                }
            }
        }

        // 2. Uses MergeSort to sort checked-in students by Section/Block & Name
        Comparator<Student> bySectionAndName = (s1, s2) -> {
            if (s1 == null && s2 == null) return 0;
            if (s1 == null) return -1;
            if (s2 == null) return 1;
            String sec1 = s1.getSection() != null ? s1.getSection() : "";
            String sec2 = s2.getSection() != null ? s2.getSection() : "";
            int secComp = sec1.compareToIgnoreCase(sec2);
            if (secComp != 0) {
                return secComp;
            }
            String name1 = s1.getFullName() != null ? s1.getFullName() : "";
            String name2 = s2.getFullName() != null ? s2.getFullName() : "";
            return name1.compareToIgnoreCase(name2);
        };
        MergeSort.sort(attendees, bySectionAndName);

        // 3. Count attendees and submitted requirements per section
        Map<String, Integer> attendeesPerSection = new LinkedHashMap<>();
        Map<String, Integer> requirementsPerSection = new LinkedHashMap<>();

        for (Student student : attendees) {
            String section = (student.getSection() != null && !student.getSection().trim().isEmpty())
                    ? student.getSection().trim()
                    : "UNKNOWN";
            attendeesPerSection.put(section, attendeesPerSection.getOrDefault(section, 0) + 1);
            if (student.isRequirementSubmitted()) {
                requirementsPerSection.put(section, requirementsPerSection.getOrDefault(section, 0) + 1);
            }
        }

        if (DataStore.sectionAttendanceMap != null) {
            for (Map.Entry<String, Integer> entry : DataStore.sectionAttendanceMap.entrySet()) {
                if (entry.getValue() > 0 && !attendeesPerSection.containsKey(entry.getKey())) {
                    attendeesPerSection.put(entry.getKey(), entry.getValue());
                }
            }
        }

        int totalAttendees = 0;
        for (Map.Entry<String, Integer> entry : attendeesPerSection.entrySet()) {
            String section = entry.getKey();
            int attendeeCount = entry.getValue();
            int reqCount = requirementsPerSection.getOrDefault(section, 0);
            System.out.printf("%-24s%-16d%d%n", section, attendeeCount, reqCount);
            totalAttendees += attendeeCount;
        }

        int totalStudents = (DataStore.students != null && !DataStore.students.isEmpty())
                ? DataStore.students.size()
                : 1500;

        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("TOTAL ATTENDEES : %,d / %,d Students%n", totalAttendees, totalStudents);
        System.out.println("================================================================================");
    }
}
