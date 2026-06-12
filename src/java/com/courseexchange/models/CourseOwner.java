package com.courseexchange.models;

public class CourseOwner {
    private int courseId;
    private String courseName;
    private String slot;
    private int userId;
    private String username;
    private String regNumber;
    private String wantCourse; // what this user wants

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRegNumber() { return regNumber; }
    public void setRegNumber(String regNumber) { this.regNumber = regNumber; }

    public String getWantCourse() { return wantCourse; }
    public void setWantCourse(String wantCourse) { this.wantCourse = wantCourse; }
}
