package org.sgcite.attendance.data;

import org.sgcite.attendance.models.Event;
import org.sgcite.attendance.models.Officer;
import org.sgcite.attendance.models.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Centralized in-memory data store for the SG CITE Attendance Management System.
 * Maintains static collections for students, officers, events, and section attendance tracking.
 */
public class DataStore {

    public static final List<Student> students = new ArrayList<>(1500);
    public static final List<Officer> officers = new ArrayList<>();
    public static final List<Event> events = new ArrayList<>();
    public static final Map<String, Integer> sectionAttendanceMap = new HashMap<>();

    /**
     * Standard 15 academic sections / blocks across year levels 1 to 4.
     */
    public static final String[] SECTIONS = {
        "BSIT-1101", "BSIT-1102", "BSIT-1103", "BSIT-1104",
        "BSIT-2101", "BSIT-2102", "BSIT-2103", "BSIT-2104",
        "BSIT-3101", "BSIT-3102", "BSIT-3103", "BSIT-3104",
        "BSIT-4101", "BSIT-4102", "BSIT-4103"
    };

    private static final String[] FIRST_NAMES = {
        "AARON", "ALTHEA", "ANGEL", "ANGELO", "BEATRICE", "CARLO", "CHRISTIAN", "DANIEL",
        "DAVID", "DIANNE", "ELIJAH", "ETHAN", "GABRIEL", "HANNAH", "IRISH", "ISABELLA",
        "JACOB", "JAMES", "JASMINE", "JEROME", "JOHN", "JOSEPH", "JOSHUA", "JUAN",
        "JUSTIN", "KATRINA", "KIMBERLY", "LANCE", "LEO", "MARC", "MARIA", "MARK",
        "MATTHEW", "MIGUEL", "NATHAN", "NICOLE", "PAOLO", "PATRICIA", "RAFAEL", "SAMANTHA",
        "SEBASTIAN", "SOPHIA", "TRISTAN", "VALERIE", "VINCENT"
    };

    private static final String[] LAST_NAMES = {
        "AGUILAR", "ALCANTARA", "AQUINO", "BAUTISTA", "CASTILLO", "CASTRO", "CORPUZ",
        "CRUZ", "DE GUZMAN", "DE LEON", "DELA CRUZ", "DELA ROSA", "DELOS REYES",
        "DOMINGO", "ESPINOSA", "FERNANDEZ", "FLORES", "GARCIA", "GONZALES", "HERNANDEZ",
        "ILAGAN", "JAVIER", "LIM", "LOPEZ", "MAGNO", "MANALO", "MARQUEZ", "MEDINA",
        "MENDOZA", "MORALES", "NAVARETTE", "OCAMPO", "PASCUAL", "PEREZ", "PUREZA",
        "RAMOS", "REYES", "RIVERA", "SALAZAR", "SANTOS", "SORIANO", "TAN", "TOLENTINO",
        "VALDEZ", "VILLANUEVA", "VILLAR"
    };

    /**
     * Seeds the system with 1,500 deterministic unsorted student records,
     * the initial 12 SG CITE officer whitelist, and 1 default 5W1H event.
     * Prints the system initialization banner to standard output.
     */
    public static void seedData() {
        seedData(true);
    }

    /**
     * Seeds the system data with an option to print or suppress the initialization summary.
     *
     * @param printSummary true to print the ASCII initialization banner
     */
    public static void seedData(boolean printSummary) {
        // Reset collections for idempotency
        students.clear();
        officers.clear();
        events.clear();
        sectionAttendanceMap.clear();

        // 1. Initialize section attendance counters to 0 for all 15 sections
        for (String section : SECTIONS) {
            sectionAttendanceMap.put(section, 0);
        }

        // 2. Generate exactly 1,500 deterministic student records across the 15 sections
        // Exactly 100 students per section (1,500 total)
        List<Student> generatedStudents = new ArrayList<>(1500);
        for (int i = 1; i <= 1500; i++) {
            String studentId = String.format("24-%05d", i);

            // Assign section: distribute evenly so each of the 15 sections has 100 students
            int sectionIndex = (i - 1) % SECTIONS.length;
            String section = SECTIONS[sectionIndex];

            // Deterministic name generation
            String lastName = LAST_NAMES[(i * 7 + 3) % LAST_NAMES.length];
            String firstName = FIRST_NAMES[(i * 13 + 5) % FIRST_NAMES.length];
            String fullName = lastName + ", " + firstName;

            // Specific benchmark student overrides to align with test specifications
            if (i == 105) {
                fullName = "PUREZA, IRISH MAY";
                section = "BSIT-2101";
            } else if (i == 1492) {
                fullName = "DELA CRUZ, JUAN";
                section = "BSIT-2101";
            }

            generatedStudents.add(new Student(studentId, fullName, section));
        }

        // Re-balance section assignments so that every section maintains exactly 100 students
        // Count students per section and adjust if needed
        Map<String, Integer> counts = new HashMap<>();
        for (Student s : generatedStudents) {
            counts.put(s.getSection(), counts.getOrDefault(s.getSection(), 0) + 1);
        }

        // Deterministic rebalancing:
        // Benchmark overrides (i=105 and i=1492) added surplus to BSIT-2101 and caused deficits
        // in BSIT-4103 and BSIT-2103. Reassign non-benchmark students from surplus to deficit sections.
        for (String deficitSection : SECTIONS) {
            while (counts.getOrDefault(deficitSection, 0) < 100) {
                String surplusSection = null;
                for (String sec : SECTIONS) {
                    if (counts.getOrDefault(sec, 0) > 100) {
                        surplusSection = sec;
                        break;
                    }
                }
                if (surplusSection == null) {
                    break;
                }

                for (Student s : generatedStudents) {
                    if (s.getSection().equals(surplusSection)
                            && !"24-00105".equals(s.getStudentId())
                            && !"24-01492".equals(s.getStudentId())) {
                        s.setSection(deficitSection);
                        counts.put(surplusSection, counts.get(surplusSection) - 1);
                        counts.put(deficitSection, counts.get(deficitSection) + 1);
                        break;
                    }
                }
            }
        }

        // Validate 100 students per section invariant
        for (String sec : SECTIONS) {
            int c = counts.getOrDefault(sec, 0);
            if (c != 100) {
                throw new IllegalStateException("Section " + sec + " does not have exactly 100 students: " + c);
            }
        }

        // Deterministically shuffle students so records are unsorted for MergeSort to index
        // Using a fixed PRNG seed guarantees identical ordering across runs
        Random rng = new Random(20261021L);
        Collections.shuffle(generatedStudents, rng);
        students.addAll(generatedStudents);

        // 3. Seed initial 12 SG CITE Officer Whitelist
        // Note: Intentionally placed in non-sorted order so MergeSort indexes them
        officers.add(new Officer("24-00105", "PUREZA, IRISH MAY", "sgcite2026", "President"));
        officers.add(new Officer("24-00312", "SANTOS, KATRINA", "sgcite2026", "Secretary"));
        officers.add(new Officer("24-00110", "BAUTISTA, MARK ANGELO", "sgcite2026", "VP for External Affairs"));
        officers.add(new Officer("24-00204", "MENDOZA, CHRISTIAN", "sgcite2026", "Treasurer"));
        officers.add(new Officer("24-00255", "DELOS REYES, SOPHIA", "sgcite2026", "Auditor"));
        officers.add(new Officer("24-00340", "GARCIA, JOHN PAUL", "sgcite2026", "Public Relations Officer"));
        officers.add(new Officer("24-00412", "RAMOS, ALTHEA JOY", "sgcite2026", "Academic Committee Head"));
        officers.add(new Officer("24-00489", "CRUZ, GABRIEL", "sgcite2026", "Creatives & Logistics Head"));
        officers.add(new Officer("24-00531", "VILLANUEVA, DANIEL", "sgcite2026", "Technical Committee Head"));
        officers.add(new Officer("24-00615", "AQUINO, BEATRICE", "sgcite2026", "Sports & Socio-Cultural Head"));
        officers.add(new Officer("24-00720", "REYES, JOSHUA", "sgcite2026", "First Year Representative"));
        officers.add(new Officer("24-00780", "LIM, PATRICIA", "sgcite2026", "Second Year Representative"));

        // 4. Seed 1 default SG CITE event (5W1H structure)
        events.add(new Event(
            "SG CITE General Assembly & Tech Symposium 2026",
            "All CITE / CS / IT Students & Faculty",
            "October 24, 2026 | 08:00 AM - 05:00 PM",
            "DLSL Sentrum / CBEAM Hall",
            "Orientation on Departmental Policies, Project Showcases, and DSA Benchmarking",
            "Tap RFID / Enter Student ID at Entrance Scanner Queue and Submit Requirements"
        ));

        if (printSummary) {
            printInitializationSummary();
        }
    }

    public static Map<String, Integer> getSectionCounts() {
        Map<String, Integer> sectionCounts = new HashMap<>();
        for (Student s : students) {
            sectionCounts.put(s.getSection(), sectionCounts.getOrDefault(s.getSection(), 0) + 1);
        }
        return sectionCounts;
    }

    /**
     * Prints the visual ASCII system initialization summary matching project specification.
     */
    public static void printInitializationSummary() {
        System.out.println("================================================================================");
        System.out.println(">>> SG CITE SYSTEM INITIALIZATION");
        System.out.println("================================================================================");
        System.out.printf("Loading Student Database    : %,d Student records loaded across %d sections.%n",
                students.size(), SECTIONS.length);
        System.out.printf("Loading Officer Whitelist   : %d SG CITE Officers registered.%n",
                officers.size());
        System.out.printf("Loading Active Events       : %d Event(s) initialized.%n",
                events.size());
        System.out.println("Status                      : READY -> Proceeding to Startup Indexing");
        System.out.println("================================================================================");
    }
}
