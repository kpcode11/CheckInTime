package com.example.studentmanager.model;

import java.time.LocalDateTime;

public class Task {
    private String taskName;
    private String category;
    private String taskDate;
    private String taskTime;
    private String reminder;
    private LocalDateTime reminderTime;
    private String priority;

    // Constructors
    public Task() {}

    public Task(String taskName, String category, String taskDate, String priority) {
        this.taskName = taskName;
        this.category = category;
        this.taskDate = taskDate;
        this.priority = priority;
    }

    public Task(String taskName, String category, String taskDate, String taskTime, String reminder, LocalDateTime reminderTime, String priority) {
        this.taskName = taskName;
        this.category = category;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.reminder = reminder;
        this.reminderTime = reminderTime;
        this.priority = priority;
    }

    // Getters and Setters
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTaskDate() { return taskDate; }
    public void setTaskDate(String taskDate) { this.taskDate = taskDate; }

    public String getTaskTime() { return taskTime; }
    public void setTaskTime(String taskTime) { this.taskTime = taskTime; }

    public String getReminder() { return reminder; }
    public void setReminder(String reminder) { this.reminder = reminder; }

    public LocalDateTime getReminderTime() { return reminderTime; }
    public void setReminderTime(LocalDateTime reminderTime) { this.reminderTime = reminderTime; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
