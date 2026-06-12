package com.courseexchange.servlets;

import com.courseexchange.utils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/DeleteCourseServlet")
public class DeleteCourseServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String courseIdStr = request.getParameter("courseId");

        if (courseIdStr == null || courseIdStr.isEmpty()) {
            response.sendRedirect("MyCoursesServlet");
            return;
        }

        int courseId = Integer.parseInt(courseIdStr);

        try (Connection conn = DBUtil.getConnection()) {

            // Check if any exchange requests exist for this course
            String checkSql = "SELECT COUNT(*) FROM exchange_requests WHERE course_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, courseId);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    session.setAttribute("message", "Cannot delete course: pending exchange requests exist.");
                    response.sendRedirect("MyCoursesServlet");
                    return;
                }
            }

            // Delete the course if no requests exist
            String deleteSql = "DELETE FROM courses WHERE id = ? AND user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, courseId);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            session.setAttribute("message", "Course deleted successfully.");
            response.sendRedirect("MyCoursesServlet");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error: " + e.getMessage());
        }
    }
}
