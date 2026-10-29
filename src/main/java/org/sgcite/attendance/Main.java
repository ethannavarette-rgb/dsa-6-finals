package org.sgcite.attendance;

import org.sgcite.attendance.data.DataStore;
import org.sgcite.attendance.dsa.CheckInQueue;
import org.sgcite.attendance.dsa.MergeSort;
import org.sgcite.attendance.dsa.OfficerActionStack;
import org.sgcite.attendance.models.Officer;
import org.sgcite.attendance.models.Student;
import org.sgcite.attendance.portals.OfficerPortal;
import org.sgcite.attendance.portals.StudentPortal;

import java.util.Comparator;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in));
    }

    public static void run(Scanner scanner) {
        // 1. DataStore.seedData() -> 1,500 students & officer whitelist
        DataStore.seedData(true);

        // 2. Pre-sort students & officers by ID using MergeSort.sort()
        MergeSort.sort(DataStore.students, MergeSort.COMPARE_STUDENT_BY_ID);
        MergeSort.sort(DataStore.officers, Comparator.comparing(Officer::getStudentId));
        MergeSort.printExecutionSummary("DataStore.students", DataStore.students.size(), "Student::getStudentId (Ascending)");

        // 3. Initialize CheckInQueue & OfficerActionStack
        CheckInQueue checkInQueue = new CheckInQueue();
        OfficerActionStack officerActionStack = new OfficerActionStack();

        // 4. Run main ROLE loop: [1] SG CITE OFFICER | [2] STUDENT | [0] SHUTDOWN
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("================================================================================");
            System.out.println("               SG CITE EVENT ATTENDANCE MANAGEMENT SYSTEM");
            System.out.println("================================================================================");
            System.out.println("[System] 1,500 Student Records & Officer Whitelist Indexed via Merge Sort.\n");
            System.out.println("SELECT ROLE:");
            System.out.println("[1] SG CITE OFFICER");
            System.out.println("[2] STUDENT");
            System.out.println("[0] SHUTDOWN SYSTEM");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    OfficerPortal.startOfficerFlow(scanner, checkInQueue, officerActionStack);
                    break;

                case "2":
                    StudentPortal.startStudentFlow(scanner, checkInQueue);
                    break;

                case "0":
                case "EXIT":
                case "SHUTDOWN":
                    System.out.println("\n[!] Shutting down SG CITE System. Goodbye!");
                    isRunning = false;
                    break;

                default:
                    System.out.println("\n[!] Invalid selection. Please enter 1, 2, or 0.\n");
                    break;
            }
        }
    }
}
