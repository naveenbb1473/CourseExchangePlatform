package com.courseexchange.servlets;

import com.courseexchange.utils.DBUtil;
import com.courseexchange.utils.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String identifier = request.getParameter("username"); // can be email or reg_number or username
        String password = request.getParameter("password");

        if (identifier == null || password == null || identifier.trim().isEmpty() || password.isEmpty()) {
            request.getSession().setAttribute("message", "Please provide credentials");
            response.sendRedirect("index.jsp");
            return;
        }

        String hashed = PasswordUtil.hash(password);

        try (Connection conn = DBUtil.getConnection()) {
            // Try lookup by email, then reg_number, then username (in that order)
            String sql = "SELECT id, username FROM users WHERE (email = ? OR reg_number = ? OR username = ?) AND password = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, identifier);
                ps.setString(2, identifier);
                ps.setString(3, identifier);
                ps.setString(4, hashed);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("id");
                        HttpSession session = request.getSession();
                        session.setAttribute("userId", userId);
                        session.setAttribute("username", rs.getString("username"));
                        response.sendRedirect("home.jsp");
                        return;
                    }
                }
            }
            request.getSession().setAttribute("message", "Invalid credentials");
            response.sendRedirect("index.jsp");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
