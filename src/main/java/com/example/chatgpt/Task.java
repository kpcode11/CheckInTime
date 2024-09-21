package com.example.chatgpt;

public class Task {
    private String taskName;
    private String category;
    private String taskDate;

    // Constructor
    public Task(String taskName, String category, String taskDate) {
        this.taskName = taskName;
        this.category = category;
        this.taskDate = taskDate;
    }

    // Getters
    public String getTaskName() {
        return taskName;
    }

    public String getCategory() {
        return category;
    }

    public String getTaskDate() {
        return taskDate;
    }
}
