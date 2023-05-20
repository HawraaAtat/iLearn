package com.example.elearningapp;

import java.util.List;

public class ModelCourse {
    String id, category, title, totalLessons, tutor, timestamp;
    private List<String> enrolledStudents;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(String totalLessons) {
        this.totalLessons = totalLessons;
    }

    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public ModelCourse() {
    }

    public ModelCourse(String category, String title, String totalLessons, String tutor, String timestamp) {
        this.category = category;
        this.title = title;
        this.totalLessons = totalLessons;
        this.tutor = tutor;
        this.timestamp = timestamp;
    }
}
