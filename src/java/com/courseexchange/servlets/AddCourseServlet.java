package com.courseexchange.servlets;

import com.courseexchange.utils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/AddCourseServlet")
public class AddCourseServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String type = request.getParameter("type"); // "have" or "want"
        String courseCode = request.getParameter("courseCode");
        String courseName = request.getParameter("courseName");
        String slot = request.getParameter("slot");

        if (type == null || (!"have".equalsIgnoreCase(type) && !"want".equalsIgnoreCase(type))) {
            session.setAttribute("message", "Invalid course type.");
            response.sendRedirect("addCourse.jsp");
            return;
        }

        if (courseCode == null || courseName == null || slot == null ||
                courseCode.trim().isEmpty() || courseName.trim().isEmpty() || slot.trim().isEmpty()) {
            session.setAttribute("message", "All fields are required.");
            response.sendRedirect("addCourse.jsp");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO courses (user_id, type, course_code, course_name, slot) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setString(2, type.toLowerCase());
                ps.setString(3, courseCode.trim().toUpperCase());
                ps.setString(4, courseName.trim());
                ps.setString(5, slot.trim());
                ps.executeUpdate();
            }

            session.setAttribute("message", "Course added successfully!");
            response.sendRedirect("MyCoursesServlet");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error: " + e.getMessage());
        }
    }
}
