package com.courseexchange.models;

public class Request {
    private int requestId;
    private int courseId;
    private int requesterId;
    private String courseCode;
    private String courseName;
    private String slot;
    private String school;
    private String requesterUsername;
    private String status;

    // Getters and Setters
    public int getRequestId() {
        return requestId;
    }
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getCourseId() {
        return courseId;
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getRequesterId() {
        return requesterId;
    }
    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public String getCourseCode() {
        return courseCode;
    }
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSlot() {
        return slot;
    }
    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getSchool() {
        return school;
    }
    public void setSchool(String school) {
        this.school = school;
    }

    public String getRequesterUsername() {
        return requesterUsername;
    }
    public void setRequesterUsername(String requesterUsername) {
        this.requesterUsername = requesterUsername;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
