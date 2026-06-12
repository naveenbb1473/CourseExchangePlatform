package com.courseexchange.servlets;

import com.courseexchange.utils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/NotificationsServlet")
public class NotificationsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT id, message, is_read, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", rs.getInt("id"));
                        map.put("message", rs.getString("message"));
                        map.put("isRead", rs.getBoolean("is_read"));
                        map.put("createdAt", rs.getTimestamp("created_at"));
                        list.add(map);
                    }
                }
            }
            request.setAttribute("notifications", list);
            request.getRequestDispatcher("notifications.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");

        try (Connection conn = DBUtil.getConnection()) {
            if ("clear".equalsIgnoreCase(action)) {
                String sql = "DELETE FROM notifications WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
                session.setAttribute("message", "All notifications cleared.");
            } else {
                // Default: mark all as read
                String sql = "UPDATE notifications SET is_read = true WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
                session.setAttribute("message", "All notifications marked as read.");
            }
            response.sendRedirect("NotificationsServlet");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
