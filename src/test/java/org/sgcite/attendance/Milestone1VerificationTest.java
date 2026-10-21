package org.sgcite.attendance;

import org.sgcite.attendance.data.DataStore;
import org.sgcite.attendance.models.Event;
import org.sgcite.attendance.models.Officer;
import org.sgcite.attendance.models.Student;

import java.util.HashSet;
import java.util.Set;

/**
 * Automated Verification Test for Issue #1: Base Models & 1,500-Student Data Generator.
 */
public class Milestone1VerificationTest {

    public static void main(String[] args) {
        System.out.println(">>> RUNNING MILESTONE 1 VERIFICATION TESTS <<<\n");

        testStudentModel();
        testOfficerModel();
        testEventModel();
        testDataStoreSeeding();

        System.out.println("\n[SUCCESS] ALL VERIFICATION TESTS PASSED SUCCESSFULLY! (Issue #1 Verified)");
    }

    private static void testStudentModel() {
        System.out.println("[TEST] Verifying Student Model...");
        Student s = new Student("24-00105", "PUREZA, IRISH MAY", "BSIT-2101");
        assert "24-00105".equals(s.getStudentId()) : "Student ID mismatch";
        assert "PUREZA, IRISH MAY".equals(s.getFullName()) : "Student Name mismatch";
        assert "BSIT-2101".equals(s.getSection()) : "Student Section mismatch";
        assert !s.isCheckedIn() : "Default checkedIn must be false";
        assert !s.isRequirementSubmitted() : "Default requirementSubmitted must be false";

        s.setCheckedIn(true);
        s.setRequirementSubmitted(true);
        assert s.isCheckedIn() : "checkedIn should be true after update";
        assert s.isRequirementSubmitted() : "requirementSubmitted should be true after update";

        assert s.toString().contains("24-00105") : "toString should contain student ID";
        System.out.println("  -> Student model verification PASSED.");
    }

    private static void testOfficerModel() {
        System.out.println("[TEST] Verifying Officer Model...");
        Officer o = new Officer("24-00105", "PUREZA, IRISH MAY", "sgcite2026", "President");
        assert "24-00105".equals(o.getStudentId()) : "Officer ID mismatch";
        assert "PUREZA, IRISH MAY".equals(o.getFullName()) : "Officer Name mismatch";
        assert "sgcite2026".equals(o.getPassword()) : "Officer Password mismatch";
        assert "President".equals(o.getPosition()) : "Officer Position mismatch";

        assert o.toString().contains("President") : "Officer toString should include position";
        System.out.println("  -> Officer model verification PASSED.");
    }

    private static void testEventModel() {
        System.out.println("[TEST] Verifying Event Model (5W1H)...");
        Event e = new Event(
            "SG CITE General Assembly",
            "CITE Students",
            "October 24, 2026",
            "Sentrum",
            "Annual Assembly",
            "RFID Tap"
        );
        assert "SG CITE General Assembly".equals(e.getWhat()) : "What mismatch";
        assert "CITE Students".equals(e.getWho()) : "Who mismatch";
        assert "October 24, 2026".equals(e.getWhen()) : "When mismatch";
        assert "Sentrum".equals(e.getWhere()) : "Where mismatch";
        assert "Annual Assembly".equals(e.getWhy()) : "Why mismatch";
        assert "RFID Tap".equals(e.getHow()) : "How mismatch";

        String formatted = e.toFormatted5W1H();
        assert formatted.contains("WHAT") && formatted.contains("HOW") : "5W1H formatted output incomplete";
        System.out.println("  -> Event model verification PASSED.");
    }

    private static void testDataStoreSeeding() {
        System.out.println("[TEST] Verifying DataStore.seedData()...");
        DataStore.seedData(true);

        // 1. Verify exact counts
        assert DataStore.students.size() == 1500 : "Expected exactly 1,500 students, found " + DataStore.students.size();
        assert DataStore.officers.size() == 12 : "Expected exactly 12 officers, found " + DataStore.officers.size();
        assert DataStore.events.size() == 1 : "Expected exactly 1 event, found " + DataStore.events.size();
        assert DataStore.sectionAttendanceMap.size() == 15 : "Expected 15 sections in sectionAttendanceMap, found " + DataStore.sectionAttendanceMap.size();

        // 2. Verify all 1,500 students have unique IDs
        Set<String> uniqueIds = new HashSet<>();
        boolean foundIrish = false;
        boolean foundJuan = false;
        for (Student s : DataStore.students) {
            uniqueIds.add(s.getStudentId());
            if ("24-00105".equals(s.getStudentId())) {
                foundIrish = true;
                assert "PUREZA, IRISH MAY".equals(s.getFullName()) : "Expected PUREZA, IRISH MAY for 24-00105";
                assert "BSIT-2101".equals(s.getSection()) : "Expected BSIT-2101 for 24-00105";
            }
            if ("24-01492".equals(s.getStudentId())) {
                foundJuan = true;
                assert "DELA CRUZ, JUAN".equals(s.getFullName()) : "Expected DELA CRUZ, JUAN for 24-01492";
                assert "BSIT-2101".equals(s.getSection()) : "Expected BSIT-2101 for 24-01492";
            }
        }
        assert uniqueIds.size() == 1500 : "Duplicate student IDs detected!";
        assert foundIrish : "Benchmark student 24-00105 (PUREZA, IRISH MAY) not found in DataStore";
        assert foundJuan : "Benchmark student 24-01492 (DELA CRUZ, JUAN) not found in DataStore";

        // 3. Verify students list is unsorted initially
        boolean isSorted = true;
        for (int i = 0; i < DataStore.students.size() - 1; i++) {
            if (DataStore.students.get(i).getStudentId().compareTo(DataStore.students.get(i + 1).getStudentId()) > 0) {
                isSorted = false;
                break;
            }
        }
        assert !isSorted : "DataStore.students should be unsorted so MergeSort can index at startup";

        // 4. Verify Officer Whitelist
        boolean foundPresident = false;
        boolean foundSecretary = false;
        for (Officer o : DataStore.officers) {
            if ("24-00105".equals(o.getStudentId()) && "President".equals(o.getPosition())) {
                foundPresident = true;
                assert "sgcite2026".equals(o.getPassword()) : "President password should be sgcite2026";
            }
            if ("24-00312".equals(o.getStudentId()) && "Secretary".equals(o.getPosition())) {
                foundSecretary = true;
            }
        }
        assert foundPresident : "President [24-00105] PUREZA, IRISH MAY not found in officer whitelist";
        assert foundSecretary : "Secretary [24-00312] SANTOS, KATRINA not found in officer whitelist";

        // 5. Verify Event
        Event defaultEvent = DataStore.events.get(0);
        assert defaultEvent.getWhat().contains("SG CITE") : "Default event title mismatch";

        // 6. Verify Determinism / Idempotency
        String firstStudentIdFirstRun = DataStore.students.get(0).getStudentId();
        DataStore.seedData(false);
        String firstStudentIdSecondRun = DataStore.students.get(0).getStudentId();
        assert firstStudentIdFirstRun.equals(firstStudentIdSecondRun) : "seedData() must be 100% deterministic!";

        System.out.println("  -> DataStore seeding and verification PASSED.");
    }
}
