package com.courseexchange.models;

public class ExchangeRequest {
    private int id;
    private int requesterId;
    private int courseId;
    private String status;
    private String createdAt;
    private String requesterUsername;
    private String courseCode;
    private String courseName;
    private int courseOwnerId;

    public ExchangeRequest() {}

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getRequesterUsername() { return requesterUsername; }
    public void setRequesterUsername(String requesterUsername) { this.requesterUsername = requesterUsername; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCourseOwnerId() { return courseOwnerId; }
    public void setCourseOwnerId(int courseOwnerId) { this.courseOwnerId = courseOwnerId; }
}
