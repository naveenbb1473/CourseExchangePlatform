package com.courseexchange.servlets;

import com.courseexchange.utils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/SelectCourseServlet")
public class SelectCourseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        try (Connection conn = DBUtil.getConnection()) {

            // Step 1: Get all distinct HAVE course codes (from other users)
            List<String> courseCodes = new ArrayList<>();
            String codeSql = "SELECT DISTINCT TRIM(course_code) AS course_code FROM courses WHERE LOWER(TRIM(type)) = 'have' AND user_id != ?";
            try (PreparedStatement ps = conn.prepareStatement(codeSql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        courseCodes.add(rs.getString("course_code"));
                    }
                }
            }

            request.setAttribute("courseCodes", courseCodes);

            // Step 2: If user selected a code, fetch matching users and their 'want' courses
            String selectedCode = request.getParameter("courseCode");
            if (selectedCode != null && !selectedCode.trim().isEmpty()) {
                selectedCode = selectedCode.trim().toUpperCase();

                List<Map<String, Object>> usersWithCourse = new ArrayList<>();

                String haveSql = "SELECT c.id AS courseId, u.id AS userId, u.username, u.reg_number, " +
                        "c.course_code, c.course_name, c.slot " +
                        "FROM courses c JOIN users u ON c.user_id = u.id " +
                        "WHERE LOWER(TRIM(c.type)) = 'have' AND c.user_id != ? AND c.course_code = ?";

                try (PreparedStatement ps = conn.prepareStatement(haveSql)) {
                    ps.setInt(1, userId);
                    ps.setString(2, selectedCode);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Map<String, Object> row = new HashMap<>();
                            int otherUserId = rs.getInt("userId");

                            row.put("userId", otherUserId);
                            row.put("courseId", rs.getInt("courseId"));
                            row.put("username", rs.getString("username"));
                            row.put("regNo", rs.getString("reg_number"));
                            row.put("courseCode", rs.getString("course_code"));
                            row.put("courseName", rs.getString("course_name"));
                            row.put("slot", rs.getString("slot"));

                            // Fetch one 'want' course of that user
                            String wantSql = "SELECT course_code, course_name FROM courses WHERE user_id = ? AND LOWER(TRIM(type)) = 'want' ORDER BY created_at DESC LIMIT 1";
                            try (PreparedStatement ps2 = conn.prepareStatement(wantSql)) {
                                ps2.setInt(1, otherUserId);
                                try (ResultSet rs2 = ps2.executeQuery()) {
                                    if (rs2.next() && rs2.getString("course_code") != null) {
                                        row.put("wantCourse", rs2.getString("course_code") + " - " + rs2.getString("course_name"));
                                    } else {
                                        row.put("wantCourse", "Not specified");
                                    }
                                }
                            }
                            usersWithCourse.add(row);
                        }
                    }
                }

                request.setAttribute("selectedCode", selectedCode);
                request.setAttribute("usersWithCourse", usersWithCourse);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error: " + e.getMessage());
        }

        request.getRequestDispatcher("selectCourse.jsp").forward(request, response);
    }
}
