package com.example.studentmanager.repository;

import com.example.studentmanager.config.DatabaseConfig;
import com.example.studentmanager.model.AttendanceRecord;
import com.example.studentmanager.model.UserSubject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceRepository {

    public List<AttendanceRecord> findByUsername(String username) {
        List<AttendanceRecord> records = new ArrayList<>();
        String query = "SELECT id, username, subject_name, attended, date FROM attendance_records WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(new AttendanceRecord(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("subject_name"),
                        rs.getBoolean("attended"),
                        rs.getDate("date").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public List<UserSubject> findSubjectsByUsername(String username) {
        List<UserSubject> subjects = new ArrayList<>();
        String query = "SELECT id, username, subject_name, min_percentage FROM attendance_subjects WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                subjects.add(new UserSubject(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("subject_name"),
                        rs.getInt("min_percentage")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    public void saveRecord(AttendanceRecord record) {
        String query = "INSERT INTO attendance_records (username, subject_name, attended, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, record.getUsername());
            stmt.setString(2, record.getSubjectName());
            stmt.setBoolean(3, record.isAttended());
            stmt.setDate(4, Date.valueOf(record.getDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveSubject(UserSubject subject) {
        String query = "INSERT INTO attendance_subjects (username, subject_name, min_percentage) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, subject.getUsername());
            stmt.setString(2, subject.getSubjectName());
            stmt.setInt(3, subject.getMinPercentage());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSubjectById(int id) {
        String query = "DELETE FROM attendance_subjects WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
