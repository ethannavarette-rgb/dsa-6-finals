package org.sgcite.attendance.models;

/**
 * Encapsulates an SG CITE Event following the 5W1H architectural structure:
 * What, Who, When, Where, Why, and How.
 */
public class Event {
    private String what;   // Event Name / Title
    private String who;    // Target Participants / Organizers
    private String when;   // Date & Time
    private String where;  // Venue
    private String why;    // Purpose / Objective
    private String how;    // Execution / Registration Mechanics

    /**
     * Constructs a new Event encapsulating the 5W1H attributes.
     *
     * @param what  Event Name or Title
     * @param who   Target Participants or Organizing Committee
     * @param when  Date and Time schedule
     * @param where Venue / Location
     * @param why   Purpose or Learning Objective
     * @param how   Execution or Registration Mechanics
     */
    public Event(String what, String who, String when, String where, String why, String how) {
        this.what = (what != null) ? what.trim() : "";
        this.who = (who != null) ? who.trim() : "";
        this.when = (when != null) ? when.trim() : "";
        this.where = (where != null) ? where.trim() : "";
        this.why = (why != null) ? why.trim() : "";
        this.how = (how != null) ? how.trim() : "";
    }

    // Getters and Setters

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = (what != null) ? what.trim() : "";
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = (who != null) ? who.trim() : "";
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = (when != null) ? when.trim() : "";
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = (where != null) ? where.trim() : "";
    }

    public String getWhy() {
        return why;
    }

    public void setWhy(String why) {
        this.why = (why != null) ? why.trim() : "";
    }

    public String getHow() {
        return how;
    }

    public void setHow(String how) {
        this.how = (how != null) ? how.trim() : "";
    }

    /**
     * Returns a structured 5W1H display string suitable for console reporting.
     *
     * @return formatted 5W1H string
     */
    public String toFormatted5W1H() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append("EVENT DETAILS (5W1H STRUCTURE)\n");
        sb.append("================================================================================\n");
        sb.append(String.format("  WHAT  : %s\n", what));
        sb.append(String.format("  WHO   : %s\n", who));
        sb.append(String.format("  WHEN  : %s\n", when));
        sb.append(String.format("  WHERE : %s\n", where));
        sb.append(String.format("  WHY   : %s\n", why));
        sb.append(String.format("  HOW   : %s\n", how));
        sb.append("================================================================================");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toFormatted5W1H();
    }
}
