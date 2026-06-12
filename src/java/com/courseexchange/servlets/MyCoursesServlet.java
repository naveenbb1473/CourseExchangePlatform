package com.courseexchange.servlets;

import com.courseexchange.utils.DBUtil;
import com.courseexchange.models.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/MyCoursesServlet")
public class MyCoursesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        List<Course> haveList = new ArrayList<>();
        List<Course> wantList = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT id, type, course_code, course_name, slot FROM courses WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Course c = new Course();
                        c.setId(rs.getInt("id"));
                        c.setCourseCode(rs.getString("course_code"));
                        c.setCourseName(rs.getString("course_name"));
                        c.setSlot(rs.getString("slot"));
                        // use Course model's userId if needed
                        String type = rs.getString("type");
                        if ("want".equalsIgnoreCase(type)) wantList.add(c);
                        else haveList.add(c);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error: " + e.getMessage());
        }

        request.setAttribute("haveList", haveList);
        request.setAttribute("wantList", wantList);

        request.getRequestDispatcher("myCourses.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
