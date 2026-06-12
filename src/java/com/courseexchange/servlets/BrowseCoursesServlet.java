package com.courseexchange.servlets;

import com.courseexchange.models.Course;
import com.courseexchange.utils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/BrowseCoursesServlet")
public class BrowseCoursesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession s = request.getSession(false);
        if (s == null || s.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        List<String> distinctCodes = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT DISTINCT course_code FROM courses WHERE type = 'have' AND user_id <> ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, (Integer) s.getAttribute("userId"));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        distinctCodes.add(rs.getString("course_code"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        request.setAttribute("distinctCodes", distinctCodes);
        request.getRequestDispatcher("selectCourse.jsp").forward(request, response);
    }
}
