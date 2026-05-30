package com.example.studentmanager.model;

public class UserSubject {
    private int id;
    private String username;
    private String subjectName;
    private int minPercentage;

    public UserSubject() {}

    public UserSubject(int id, String username, String subjectName, int minPercentage) {
        this.id = id;
        this.username = username;
        this.subjectName = subjectName;
        this.minPercentage = minPercentage;
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

    public int getMinPercentage() {
        return minPercentage;
    }

    public void setMinPercentage(int minPercentage) {
        this.minPercentage = minPercentage;
    }
}
