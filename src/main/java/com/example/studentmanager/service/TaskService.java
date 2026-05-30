package com.example.studentmanager.service;

import com.example.studentmanager.model.Task;
import com.example.studentmanager.repository.TaskRepository;

import java.util.List;

public class TaskService {
    private final TaskRepository repo = new TaskRepository();

    public List<Task> getTasksForUser(String username) {
        return repo.findByUsername(username);
    }

    public void addTask(Task task, String username) {
        repo.save(task, username);
    }

    public void removeTask(int id) {
        repo.deleteById(id);
    }

    public List<Task> getUpcomingReminders() {
        return repo.findUpcomingReminders();
    }
}
