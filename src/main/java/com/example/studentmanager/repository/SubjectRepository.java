package com.example.studentmanager.repository;

import com.example.studentmanager.config.DatabaseConfig;
import com.example.studentmanager.model.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectRepository {

    public List<Subject> findByUsername(String username) {
        List<Subject> subjects = new ArrayList<>();
        String query = "SELECT subject_name, marks FROM subjects WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                subjects.add(new Subject(rs.getString("subject_name"), rs.getInt("marks")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    public void save(Subject subject, String username) {
        String query = "INSERT INTO subjects (username, subject_name, marks) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, subject.getName());
            stmt.setInt(3, subject.getMarks());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(int id) {
        // Implementation left minimal if unused, controller didn't seem to have delete by ID, but interface requires it
        String query = "DELETE FROM subjects WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMarks(int id, int marks) {
        String query = "UPDATE subjects SET marks = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, marks);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
