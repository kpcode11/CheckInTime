package com.example.studentmanager.repository;

import com.example.studentmanager.config.DatabaseConfig;
import com.example.studentmanager.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    public List<Task> findByUsername(String username) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT task_name, category, task_date, priority FROM tasks WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(new Task(rs.getString("task_name"), rs.getString("category"), 
                                   rs.getString("task_date"), rs.getString("priority")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void save(Task task, String username) {
        String query = "INSERT INTO tasks (username, task_name, category, task_date, task_time, reminder, reminder_time, priority) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, task.getTaskName());
            stmt.setString(3, task.getCategory());
            stmt.setString(4, task.getTaskDate());
            stmt.setString(5, task.getTaskTime());
            stmt.setString(6, task.getReminder());
            if (task.getReminderTime() != null) {
                stmt.setTimestamp(7, Timestamp.valueOf(task.getReminderTime()));
            } else {
                stmt.setNull(7, java.sql.Types.TIMESTAMP);
            }
            stmt.setString(8, task.getPriority());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(int id) {
        String query = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> findUpcomingReminders() {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT task_name, category, task_date, task_time, reminder, priority " +
                       "FROM tasks WHERE reminder_time <= ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(new Task(rs.getString("task_name"), rs.getString("category"), 
                                   rs.getDate("task_date").toLocalDate().toString(), rs.getString("priority")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}
