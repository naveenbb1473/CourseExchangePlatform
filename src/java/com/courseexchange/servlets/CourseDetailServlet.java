package com.courseexchange.servlets;

import com.courseexchange.models.CourseOwner;
import com.courseexchange.utils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/CourseDetailServlet")
public class CourseDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        int userId = (Integer) session.getAttribute("userId");

        String courseCode = request.getParameter("courseCode");
        if (courseCode == null || courseCode.trim().isEmpty()) {
            session.setAttribute("msg", "Please select a course code");
            response.sendRedirect("BrowseCoursesServlet");
            return;
        }

        List<CourseOwner> owners = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT c.id AS course_id, c.course_name, c.slot, u.id AS user_id, u.username, u.reg_number " +
                         "FROM courses c JOIN users u ON c.user_id = u.id " +
                         "WHERE c.type='have' AND c.course_code=? AND c.user_id<>?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, courseCode.trim().toUpperCase());
                ps.setInt(2, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        CourseOwner owner = new CourseOwner();
                        owner.setCourseId(rs.getInt("course_id"));
                        owner.setCourseName(rs.getString("course_name"));
                        owner.setSlot(rs.getString("slot"));
                        owner.setUserId(rs.getInt("user_id"));
                        owner.setUsername(rs.getString("username"));
                        owner.setRegNumber(rs.getString("reg_number"));

                        // fetch what owner wants (first want course)
                        try (PreparedStatement ps2 = conn.prepareStatement(
                                "SELECT course_code, course_name FROM courses WHERE user_id=? AND type='want' LIMIT 1")) {
                            ps2.setInt(1, owner.getUserId());
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                if (rs2.next()) {
                                    owner.setWantCourse(rs2.getString("course_code") + " - " + rs2.getString("course_name"));
                                }
                            }
                        }
                        owners.add(owner);
                    }
                }
            }

        } catch (SQLException e) {
            throw new ServletException(e);
        }

        request.setAttribute("owners", owners);
        request.setAttribute("courseCode", courseCode);
        request.getRequestDispatcher("selectCourse.jsp").forward(request, response);
    }
}
