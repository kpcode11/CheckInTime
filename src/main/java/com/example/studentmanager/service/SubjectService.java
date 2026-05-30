package com.example.studentmanager.service;

import com.example.studentmanager.model.Subject;
import com.example.studentmanager.repository.SubjectRepository;

import java.util.List;

public class SubjectService {
    private final SubjectRepository repo = new SubjectRepository();

    public List<Subject> getSubjectsForUser(String username) {
        return repo.findByUsername(username);
    }

    public void addSubject(Subject subject, String username) {
        repo.save(subject, username);
    }

    public void removeSubject(int id) {
        repo.deleteById(id);
    }

    public double calculateTotalPercentage(List<Subject> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return 0.0;
        }
        int totalMarks = 0;
        for (Subject subject : subjects) {
            totalMarks += subject.getMarks();
        }
        return (totalMarks / (double) (subjects.size() * 100)) * 100;
    }
}
