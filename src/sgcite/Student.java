package sgcite;

/**
 * Encapsulates student information and attendance record details for SG-CITE events.
 * Applies standard OOP encapsulation principles with private fields and public accessors.
 */
public class Student {
    private String studentId;
    private String fullName;
    private String program;
    private int yearLevel;
    private String timeIn;
    private String timeOut;
    private AttendanceStatus status;

    public Student(String studentId, String fullName, String program, int yearLevel) {
        this.studentId = studentId != null ? studentId.trim() : "";
        this.fullName = fullName != null ? fullName.trim() : "";
        this.program = program != null ? program.trim().toUpperCase() : "BSIT";
        this.yearLevel = (yearLevel >= 1 && yearLevel <= 4) ? yearLevel : 1;
        this.timeIn = "N/A";
        this.timeOut = "N/A";
        this.status = AttendanceStatus.QUEUED;
    }

    public Student(String studentId, String fullName, String program, int yearLevel, String timeIn) {
        this(studentId, fullName, program, yearLevel);
        this.timeIn = timeIn != null ? timeIn : "N/A";
        this.status = AttendanceStatus.CHECKED_IN;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        if (studentId != null && !studentId.trim().isEmpty()) {
            this.studentId = studentId.trim();
        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName != null && !fullName.trim().isEmpty()) {
            this.fullName = fullName.trim();
        }
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        if (program != null && !program.trim().isEmpty()) {
            this.program = program.trim().toUpperCase();
        }
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        if (yearLevel >= 1 && yearLevel <= 4) {
            this.yearLevel = yearLevel;
        }
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = (timeIn != null) ? timeIn : "N/A";
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = (timeOut != null) ? timeOut : "N/A";
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        if (status != null) {
            this.status = status;
        }
    }

    /**
     * Records check-in details and updates the attendance status.
     */
    public void markCheckIn(String timestamp) {
        this.timeIn = (timestamp != null && !timestamp.isEmpty()) ? timestamp : "N/A";
        this.status = AttendanceStatus.CHECKED_IN;
    }

    /**
     * Records departure timestamp and marks the student as checked out.
     */
    public void markCheckOut(String timestamp) {
        this.timeOut = (timestamp != null && !timestamp.isEmpty()) ? timestamp : "N/A";
        this.status = AttendanceStatus.CHECKED_OUT;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s - Year %d | Time-In: %s | Time-Out: %s | Status: %s",
                studentId, fullName, program, yearLevel, timeIn, timeOut, status);
    }
}
