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
            if (!scanner.hasNextLine()) {
                return;
            }
            String inputId = scanner.nextLine().trim();

            System.out.print("Enter Password: ");
            if (!scanner.hasNextLine()) {
                return;
            }
            String inputPassword = scanner.nextLine();

            int officerIndex = BinarySearch.findOfficerById(
                    DataStore.officers, inputId);
            if (officerIndex >= 0) {
                Officer officer = DataStore.officers.get(officerIndex);
                if (officer != null && officer.getPassword() != null && officer.getPassword().equals(inputPassword)) {
                    if (auditStack != null) {
                        auditStack.pushAction("Officer login: "
                                + officer.getStudentId() + " (" + officer.getPosition() + ")");
                    }
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
            if (!scanner.hasNextLine()) {
                return true;
            }
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
            System.out.println("[3] Member Roster");
            System.out.println("[4] View Action History");
            System.out.println("[5] Exit");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.print("Select transaction [1-5]: ");

            if (!scanner.hasNextLine()) {
                return;
            }
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleManageEvents(scanner, auditStack);
                    if (confirmExit(scanner, "Exit Officer Dashboard? (YES/NO): ")) {
                        inDashboard = false;
                    }
                    break;

                case "2":
                    handleAttendanceReport(queue, auditStack);
                    if (confirmExit(scanner, "Exit Officer Dashboard? (YES/NO): ")) {
                        inDashboard = false;
                    }
                    break;

                case "3":
                    handleMemberRoster(scanner, auditStack);
                    if (confirmExit(scanner, "Exit Officer Dashboard? (YES/NO): ")) {
                        inDashboard = false;
                    }
                    break;

                case "4":
                    handleViewActionHistory(auditStack);
                    if (confirmExit(scanner, "Exit Officer Dashboard? (YES/NO): ")) {
                        inDashboard = false;
                    }
                    break;

                case "5":
                case "0":
                case "EXIT":
                    if (confirmExit(scanner, "Exit Officer Dashboard? (YES/NO): ")) {
                        inDashboard = false;
                    }
                    break;

                default:
                    System.out.println("[!] Invalid option. Please select 1, 2, 3, 4, or 5.");
                    break;
            }
        }
    }

    public static void runManageEvents(Scanner scanner, OfficerActionStack auditStack) {
        handleManageEvents(scanner, auditStack);
    }

    public static void runAttendanceReport(CheckInQueue queue) {
        handleAttendanceReport(queue, null);
    }

    public static void runAttendanceReport(CheckInQueue queue, OfficerActionStack auditStack) {
        handleAttendanceReport(queue, auditStack);
    }

    public static void runMemberRoster(Scanner scanner, OfficerActionStack auditStack) {
        handleMemberRoster(scanner, auditStack);
    }

    public static void runViewActionHistory(OfficerActionStack auditStack) {
        handleViewActionHistory(auditStack);
    }

    private static void handleViewActionHistory(OfficerActionStack auditStack) {
        if (auditStack == null) {
            System.out.println("No recent actions.");
            return;
        }
        auditStack.displayAuditHistory();
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
        if (!scanner.hasNextLine()) return;
        String what = scanner.nextLine().trim();

        System.out.print("Enter WHO (Target Participants)     : ");
        if (!scanner.hasNextLine()) return;
        String who = scanner.nextLine().trim();

        System.out.print("Enter WHEN (Date & Time Schedule)   : ");
        if (!scanner.hasNextLine()) return;
        String when = scanner.nextLine().trim();

        System.out.print("Enter WHERE (Venue/Location)        : ");
        if (!scanner.hasNextLine()) return;
        String where = scanner.nextLine().trim();

        System.out.print("Enter WHY (Objective/Purpose)       : ");
        if (!scanner.hasNextLine()) return;
        String why = scanner.nextLine().trim();

        System.out.print("Enter HOW (Mechanics/Execution)     : ");
        if (!scanner.hasNextLine()) return;
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
        handleAttendanceReport(queue, null);
    }

    private static void handleAttendanceReport(CheckInQueue queue, OfficerActionStack auditStack) {
        int flushed = 0;
        if (queue != null) {
            flushed = queue.flushQueueToAttendance(DataStore.sectionAttendanceMap);
        }

        if (auditStack != null) {
            auditStack.pushAction("Attendance report generated: " + flushed + " check-in(s) processed");
        }

        System.out.println("================================================================================");
        System.out.println(">>> SG CITE ATTENDANCE REPORT (PER SECTION / BLOCK)");
        System.out.println("================================================================================");
        System.out.printf("Attendance Synced: %d pending check-in(s) processed.%n", flushed);
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
        MergeSort.sort(attendees, MergeSort.COMPARE_STUDENT_BY_SECTION_AND_NAME);

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

    private static void handleMemberRoster(Scanner scanner, OfficerActionStack auditStack) {
        System.out.println("================================================================================");
        System.out.println(">>> SG CITE MEMBER ROSTER MANAGEMENT");
        System.out.println("================================================================================");
        System.out.println("[1] Add New Officer");
        System.out.println("[2] Remove Officer");
        System.out.println("[3] View Current Roster");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("Select roster action [1-3]: ");

        if (!scanner.hasNextLine()) return;
        String action = scanner.nextLine().trim();

        if (action.equals("2") || action.equalsIgnoreCase("REMOVE") || action.equalsIgnoreCase("DELETE")) {
            System.out.print("Enter Officer Student ID to Remove: ");
            if (!scanner.hasNextLine()) return;
            String idToRemove = scanner.nextLine().trim();
            int index = BinarySearch.findOfficerById(DataStore.officers, idToRemove);
            if (index >= 0) {
                Officer removed = DataStore.officers.remove(index);
                MergeSort.sortOfficersById(DataStore.officers);
                System.out.println("[-] Removed Officer: [" + removed.getStudentId() + "] " + removed.getFullName());
                System.out.println("[*] Re-indexing Officer Whitelist via Merge Sort (O(n log n))... Done.");
                if (auditStack != null) {
                    auditStack.pushAction("Removed officer: " + removed.getStudentId() + " (" + removed.getPosition() + ")");
                }
            } else {
                System.out.println("[!] ERROR: Officer ID not found on whitelist.");
            }
            displayOfficerRoster();
        } else if (action.equals("3") || action.equalsIgnoreCase("VIEW") || action.equalsIgnoreCase("LIST")) {
            displayOfficerRoster();
        } else if (action.equals("1") || action.equalsIgnoreCase("ADD")) {
            System.out.print("Enter Officer Student ID : ");
            if (!scanner.hasNextLine()) return;
            String studentId = scanner.nextLine().trim();

            int existingIndex = BinarySearch.findOfficerById(DataStore.officers, studentId);
            if (existingIndex >= 0) {
                Officer existing = DataStore.officers.get(existingIndex);
                System.out.println("[!] Officer already exists: " + existing.getFullName() + ". Updating details...");
            }

            System.out.print("Enter Officer Full Name  : ");
            if (!scanner.hasNextLine()) return;
            String fullName = scanner.nextLine().trim();

            System.out.print("Enter Officer Password   : ");
            if (!scanner.hasNextLine()) return;
            String password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                password = "sgcite2026";
            }

            System.out.print("Enter Officer Position   : ");
            if (!scanner.hasNextLine()) return;
            String position = scanner.nextLine().trim();

            if (existingIndex >= 0) {
                Officer existing = DataStore.officers.get(existingIndex);
                if (!fullName.isEmpty()) existing.setFullName(fullName);
                existing.setPassword(password);
                if (!position.isEmpty()) existing.setPosition(position);
                MergeSort.sortOfficersById(DataStore.officers);
                System.out.println("[+] Updated Officer: [" + existing.getStudentId() + "] " + existing.getFullName() + " (" + existing.getPosition() + ")");
                System.out.println("[*] Re-indexing Officer Whitelist via Merge Sort (O(n log n))... Done.");
                if (auditStack != null) {
                    auditStack.pushAction("Updated officer: " + existing.getStudentId() + " (" + existing.getPosition() + ")");
                }
            } else {
                Officer newOfficer = new Officer(studentId, fullName, password, position);
                DataStore.officers.add(newOfficer);
                MergeSort.sortOfficersById(DataStore.officers);
                System.out.println("[+] Added New Officer: [" + newOfficer.getStudentId() + "] " + newOfficer.getFullName() + " (" + newOfficer.getPosition() + ")");
                System.out.println("[*] Re-indexing Officer Whitelist via Merge Sort (O(n log n))... Done.");
                if (auditStack != null) {
                    auditStack.pushAction("Added officer: " + newOfficer.getStudentId() + " (" + newOfficer.getPosition() + ")");
                }
            }

            displayOfficerRoster();
        } else {
            System.out.println("[!] Invalid roster action. Please select 1, 2, or 3.");
        }
    }

    private static void displayOfficerRoster() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("UPDATED SG CITE OFFICER ROSTER (%d Members):%n", DataStore.officers.size());
        for (int i = 0; i < DataStore.officers.size(); i++) {
            Officer off = DataStore.officers.get(i);
            String idAndName = "[" + off.getStudentId() + "] " + off.getFullName();
            System.out.printf("%d. %-34s | %s%n", (i + 1), idAndName, off.getPosition());
        }
        System.out.println("================================================================================");
    }
}
