package org.sgcite.attendance.models;

import java.util.Objects;

/**
 * Encapsulates an SG CITE Officer credential and role for whitelist authentication.
 */
public class Officer {
    private String studentId;
    private String fullName;
    private String password;
    private String position;

    /**
     * Constructs a new SG CITE Officer record.
     *
     * @param studentId unique student ID (e.g., 24-00105)
     * @param fullName full name of the officer
     * @param password secret credential for portal authentication
     * @param position organizational role (e.g., President, Secretary)
     */
    public Officer(String studentId, String fullName, String password, String position) {
        this.studentId = (studentId != null) ? studentId.trim() : "";
        this.fullName = (fullName != null) ? fullName.trim() : "";
        this.password = (password != null) ? password.trim() : "";
        this.position = (position != null) ? position.trim() : "";
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = (password != null) ? password.trim() : "";
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = (position != null) ? position.trim() : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Officer officer = (Officer) o;
        return Objects.equals(studentId.toLowerCase(), officer.studentId.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s", studentId, fullName, position);
    }
}
