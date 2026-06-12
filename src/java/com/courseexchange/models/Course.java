package com.courseexchange.models;

public class Course {
    private int id;
    private int userId;
    private String courseCode;
    private String courseName;
    private String slot;
    private String type; // "have" or "want"

    // Default constructor
    public Course() {}

    // Full constructor
    public Course(int id, int userId, String courseCode, String courseName, String slot, String type) {
        this.id = id;
        this.userId = userId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.slot = slot;
        this.type = type;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}