package org.sgcite.attendance.portals;

import java.util.Scanner;
import org.sgcite.attendance.data.DataStore;
import org.sgcite.attendance.dsa.BinarySearch;
import org.sgcite.attendance.dsa.CheckInQueue;
import org.sgcite.attendance.models.Student;

public class StudentPortal {

    public static void startStudentFlow(
            Scanner scanner, CheckInQueue checkInQueue) {

        while (true) {
            System.out.print("Enter Student ID: ");
            if (!scanner.hasNextLine()) {
                return;
            }
            String inputId = scanner.nextLine().trim();

            int index = BinarySearch.findStudentById(
                    DataStore.students, inputId);

            if (index >= 0) {
                Student student = DataStore.students.get(index);
                showStudentDashboard(scanner, student, checkInQueue);
                return;
            } else {
                System.out.println("ID not found");

                if (confirmExit(scanner,
                        "Exit to Main Role Menu? (YES/NO): ")) {
                    return;
                }
            }
        }
    }

    private static void showStudentDashboard(
            Scanner scanner, Student student, CheckInQueue checkInQueue) {

        while (true) {
            System.out.println("\n==============================================");
            System.out.println(">>> STUDENT DASHBOARD | Welcome, "
                    + student.getFullName() + " ("
                    + student.getSection() + ")");
            System.out.println("==============================================");
            System.out.println("[1] Event Check-In");
            System.out.println("[2] Submit Attendance Requirement(s)");
            System.out.println("[3] Exit");
            System.out.println("----------------------------------------------");
            System.out.print("Select transaction [1-3]: ");

            if (!scanner.hasNextLine()) {
                return;
            }
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (checkInQueue == null) {
                        System.out.println("[!] Check-in service is currently unavailable.");
                    } else if (student.isCheckedIn()) {
                        System.out.println(
                                "[!] You are already checked in.");
                    } else if (checkInQueue.enqueueCheckIn(student)) {
                        System.out.println(
                                "[✓] Check-in request received! Please wait for officer confirmation before submitting requirements.");
                    } else {
                        System.out.println(
                                "[!] Your check-in request has already been submitted and is pending officer confirmation.");
                    }

                    if (confirmExit(scanner,
                            "Exit Student Dashboard? (YES/NO): ")) {
                        return;
                    }
                    break;

                case "2":
                    if (!student.isCheckedIn()) {
                        if (checkInQueue != null && checkInQueue.isPending(student.getStudentId())) {
                            System.out.println(
                                    "[!] Your check-in is currently pending officer confirmation. Please wait for confirmation before submitting attendance requirements.");
                        } else {
                            System.out.println(
                                    "[!] You must submit a check-in request and receive officer confirmation before submitting attendance requirements.");
                        }
                    } else if (student.isRequirementSubmitted()) {
                        System.out.println(
                                "[!] Your attendance requirement has already been submitted.");
                    } else {
                        student.setRequirementSubmitted(true);
                        System.out.println(
                                "[✓] Attendance requirement submitted successfully.");
                    }

                    if (confirmExit(scanner,
                            "Exit Student Dashboard? (YES/NO): ")) {
                        return;
                    }
                    break;

                case "3":
                case "EXIT":
                    if (confirmExit(scanner,
                            "Exit Student Dashboard? (YES/NO): ")) {
                        return;
                    }
                    break;

                default:
                    System.out.println(
                            "[!] Invalid option. Please select 1, 2, or 3.");
            }
        }
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
                System.out.println(
                        "Invalid choice. Please enter YES or NO.");
            }
        }
    }
}
