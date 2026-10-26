package org.sgcite.attendance.portals;

import org.sgcite.attendance.data.DataStore;
import org.sgcite.attendance.dsa.BinarySearch;
import org.sgcite.attendance.dsa.CheckInQueue;
import org.sgcite.attendance.dsa.MergeSort;
import org.sgcite.attendance.dsa.OfficerActionStack;
import org.sgcite.attendance.models.Officer;

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
                    showOfficerDashboard(officer, queue);
                    return;
                }
            }

            System.out.println("[!] ERROR: Access Denied");
            if (confirmExit(scanner)) {
                retryLogin = false;
            }
        }
    }

    private static boolean confirmExit(Scanner scanner) {
        boolean validAnswer = false;
        boolean exit = false;

        while (!validAnswer) {
            System.out.print("Exit to Main Role Menu? (YES/NO): ");
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("YES")) {
                exit = true;
                validAnswer = true;
            } else if (answer.equalsIgnoreCase("NO")) {
                validAnswer = true;
            } else {
                System.out.println("Invalid choice. Please enter YES or NO.");
            }
        }

        return exit;
    }

    private static void showOfficerDashboard(Officer officer, CheckInQueue queue) {
        System.out.println("[✓] Whitelist Verified: Officer "
                + officer.getFullName() + " (" + officer.getPosition() + ")");
        System.out.println("================================================================================");
        System.out.println(">>> SG CITE OFFICER DASHBOARD");
        System.out.println("Welcome, " + officer.getFullName() + ".");
        System.out.println("Pending Check-Ins: " + queue.getPendingCount());
        System.out.println("================================================================================");
    }
}