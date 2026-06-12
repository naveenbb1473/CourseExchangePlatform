package com.courseexchange.servlets;

import com.courseexchange.utils.DBUtil;
import com.courseexchange.utils.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final String ALLOWED_DOMAIN = "@vitstudent.ac.in";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String regNumber = request.getParameter("reg_number");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm_password");

        HttpSession session = request.getSession();

        // Basic server-side validation
        if (username == null || regNumber == null || email == null || password == null || confirm == null ||
                username.trim().isEmpty() || regNumber.trim().isEmpty() || email.trim().isEmpty() ||
                password.isEmpty() || confirm.isEmpty()) {
            session.setAttribute("message", "All fields are required");
            response.sendRedirect("register.jsp");
            return;
        }

        if (!email.toLowerCase().endsWith(ALLOWED_DOMAIN)) {
            session.setAttribute("message", "Please register with your VIT student email ending with " + ALLOWED_DOMAIN);
            response.sendRedirect("register.jsp");
            return;
        }
        
        if (phone == null || phone.trim().isEmpty()) {
        session.setAttribute("message", "Phone number is required");
        response.sendRedirect("register.jsp");
           return;
        }
        
        if (!password.equals(confirm)) {
            session.setAttribute("message", "Passwords do not match");
            response.sendRedirect("register.jsp");
            return;
        }

        if (password.length() < 6) {
            session.setAttribute("message", "Password must be at least 6 characters");
            response.sendRedirect("register.jsp");
            return;
        }

        String hashed = PasswordUtil.hash(password);

        try (Connection conn = DBUtil.getConnection()) {
            // Check reg_number uniqueness
            String checkReg = "SELECT id FROM users WHERE reg_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkReg)) {
                ps.setString(1, regNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        session.setAttribute("message", "Registration number already registered");
                        response.sendRedirect("register.jsp");
                        return;
                    }
                }
            }

            // Check email uniqueness
            String checkEmail = "SELECT id FROM users WHERE email = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkEmail)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        session.setAttribute("message", "Email already registered. Please login or use password reset.");
                        response.sendRedirect("register.jsp");
                        return;
                    }
                }
            }
            
            // Optional: check if phone already exists
            String checkPhone = "SELECT id FROM users WHERE phone = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkPhone)) {
                ps.setString(1, phone.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        session.setAttribute("message", "Phone number already registered.");
                       response.sendRedirect("register.jsp");
                        return;
                    }
                }
            }

            // Insert user
            String insert = "INSERT INTO users (username, reg_number, email, password, phone) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, username);
                ps.setString(2, regNumber);
                ps.setString(3, email);
                ps.setString(4, hashed);
                ps.setString(5, phone.trim());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int newUserId = rs.getInt(1);
                        // Optionally auto-login user after registration
                        // HttpSession s = request.getSession();
                        // s.setAttribute("userId", newUserId);
                        // s.setAttribute("username", username);
                    }
                }
            }

            session.setAttribute("message", "Registration successful. Please log in.");
            response.sendRedirect("index.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error: " + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("register.jsp");
    }
}
