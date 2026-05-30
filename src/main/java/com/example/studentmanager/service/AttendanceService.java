package com.example.studentmanager.service;

import com.example.studentmanager.model.AttendanceRecord;
import com.example.studentmanager.model.UserSubject;
import com.example.studentmanager.repository.AttendanceRepository;

import java.util.List;
import java.util.stream.Collectors;

public class AttendanceService {
    private final AttendanceRepository repo = new AttendanceRepository();

    public List<UserSubject> getSubjectsForUser(String username) {
        return repo.findSubjectsByUsername(username);
    }

    public void addSubject(UserSubject subject) {
        repo.saveSubject(subject);
    }

    public void removeSubject(int id) {
        repo.deleteSubjectById(id);
    }

    public void recordAttendance(AttendanceRecord record) {
        repo.saveRecord(record);
    }

    public double calculateAttendancePercentage(String username, String subjectName) {
        List<AttendanceRecord> allRecords = repo.findByUsername(username);
        List<AttendanceRecord> subjectRecords = allRecords.stream()
                .filter(r -> r.getSubjectName().equals(subjectName))
                .collect(Collectors.toList());

        if (subjectRecords.isEmpty()) {
            return 0.0;
        }

        long attendedCount = subjectRecords.stream().filter(AttendanceRecord::isAttended).count();
        return (attendedCount / (double) subjectRecords.size()) * 100;
    }
}
