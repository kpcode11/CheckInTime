package com.example.studentmanager.model;

import java.time.LocalDate;

public class AttendanceRecord {
    private int id;
    private String username;
    private String subjectName;
    private boolean attended;
    private LocalDate date;

    public AttendanceRecord() {}

    public AttendanceRecord(int id, String username, String subjectName, boolean attended, LocalDate date) {
        this.id = id;
        this.username = username;
        this.subjectName = subjectName;
        this.attended = attended;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
