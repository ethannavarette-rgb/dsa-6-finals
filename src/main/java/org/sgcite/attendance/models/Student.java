package org.sgcite.attendance.models;

import java.util.Objects;

/**
 * Encapsulates a student record within the SG CITE Attendance Management System.
 * Adheres strictly to OOP encapsulation with defensive accessors and state tracking.
 */
public class Student {
    private String studentId;
    private String fullName;
    private String section;
    private boolean checkedIn;
    private boolean requirementSubmitted;

    /**
     * Constructs a new Student record.
     * Default booleans for checkedIn and requirementSubmitted are initialized to false.
     *
     * @param studentId unique student identification number (e.g., 24-00105)
     * @param fullName student full name (e.g., PUREZA, IRISH MAY)
     * @param section enrolled section or block (e.g., BSIT-2101)
     */
    public Student(String studentId, String fullName, String section) {
        this.studentId = (studentId != null) ? studentId.trim() : "";
        this.fullName = (fullName != null) ? fullName.trim() : "";
        this.section = (section != null) ? section.trim() : "";
        this.checkedIn = false;
        this.requirementSubmitted = false;
    }

    // Getters and Setters

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = (studentId != null) ? studentId.trim() : "";
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = (fullName != null) ? fullName.trim() : "";
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = (section != null) ? section.trim() : "";
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public boolean isRequirementSubmitted() {
        return requirementSubmitted;
    }

    public void setRequirementSubmitted(boolean requirementSubmitted) {
        this.requirementSubmitted = requirementSubmitted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId.toLowerCase(), student.studentId.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", studentId, fullName, section);
    }
}
