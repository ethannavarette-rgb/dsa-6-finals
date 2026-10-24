package org.sgcite.attendance.portals;

import java.util.Scanner;
import org.sgcite.attendance.data.DataStore;
import org.sgcite.attendance.dsa.BinarySearch;
import org.sgcite.attendance.models.Student;

public class StudentPortal {

    public static void startStudentFlow(Scanner scanner) {
        boolean retryInput = true;

        while (retryInput) {
            System.out.print("Enter Student ID: ");
            String inputId = scanner.nextLine().trim();

            int index = BinarySearch.findStudentById(
                DataStore.students, inputId
            );

            if (index >= 0) {
                Student matchedStudent = DataStore.students.get(index);

                System.out.println("[✓] Validated: "
                    + matchedStudent.getStudentId()
                    + " | " + matchedStudent.getFullName()
                    + " | Section: " + matchedStudent.getSection());

                System.out.println("Routing to Student Dashboard...");
                retryInput = false;

            } else {
                System.out.println("[!] ERROR: ID not found");

                boolean validChoice = false;

                while (!validChoice) {
                    System.out.print(
                        "Exit to Main Role Menu? (YES/NO): "
                    );

                    String choice = scanner.nextLine().trim();

                    if (choice.equalsIgnoreCase("NO")) {
                        validChoice = true;
                    } else if (choice.equalsIgnoreCase("YES")) {
                        validChoice = true;
                        retryInput = false;
                    } else {
                        System.out.println(
                            "Invalid choice. Please enter YES or NO."
                        );
                    }
                }
            }
        }
    }
}
