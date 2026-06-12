package com.courseexchange.servlets;

import com.courseexchange.utils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String requestIdStr = request.getParameter("requestId");
        if (requestIdStr == null || requestIdStr.trim().isEmpty()) {
            response.sendRedirect("StatusServlet");
            return;
        }

        int requestId = Integer.parseInt(requestIdStr);

        try (Connection conn = DBUtil.getConnection()) {
            // Verify access: user must be requester or receiver of this request, and status must be accepted or completed
            String verifySql = "SELECT r.id, r.requester_id, r.receiver_id, r.have_course_code, r.want_course_code, r.status, " +
                    "u1.username AS requester_name, u2.username AS receiver_name " +
                    "FROM exchange_requests r " +
                    "JOIN users u1 ON r.requester_id = u1.id " +
                    "JOIN users u2 ON r.receiver_id = u2.id " +
                    "WHERE r.id = ? AND (r.requester_id = ? OR r.receiver_id = ?) " +
                    "AND r.status IN ('accepted', 'completed')";
            
            Map<String, Object> details = null;
            try (PreparedStatement ps = conn.prepareStatement(verifySql)) {
                ps.setInt(1, requestId);
                ps.setInt(2, userId);
                ps.setInt(3, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        details = new HashMap<>();
                        details.put("requestId", rs.getInt("id"));
                        details.put("requesterId", rs.getInt("requester_id"));
                        details.put("receiverId", rs.getInt("receiver_id"));
                        details.put("haveCourseCode", rs.getString("have_course_code"));
                        details.put("wantCourseCode", rs.getString("want_course_code"));
                        details.put("status", rs.getString("status"));
                        details.put("requesterName", rs.getString("requester_name"));
                        details.put("receiverName", rs.getString("receiver_name"));
                    }
                }
            }

            if (details == null) {
                // Not authorized or invalid request
                session.setAttribute("msg", "❌ Access Denied: You can only chat after a request is accepted.");
                response.sendRedirect("StatusServlet");
                return;
            }

            // Fetch chat messages
            List<Map<String, Object>> messages = new ArrayList<>();
            String msgSql = "SELECT m.sender_id, m.message_text, m.created_at, u.username AS sender_name " +
                    "FROM messages m " +
                    "JOIN users u ON m.sender_id = u.id " +
                    "WHERE m.request_id = ? " +
                    "ORDER BY m.created_at ASC";
            try (PreparedStatement ps = conn.prepareStatement(msgSql)) {
                ps.setInt(1, requestId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> msg = new HashMap<>();
                        msg.put("senderId", rs.getInt("sender_id"));
                        msg.put("senderName", rs.getString("sender_name"));
                        msg.put("messageText", rs.getString("message_text"));
                        msg.put("createdAt", rs.getTimestamp("created_at"));
                        messages.add(msg);
                    }
                }
            }

            request.setAttribute("chatDetails", details);
            request.setAttribute("messagesList", messages);
            request.getRequestDispatcher("chat.jsp").forward(request, response);

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

        int senderId = (int) session.getAttribute("userId");
        String requestIdStr = request.getParameter("requestId");
        String messageText = request.getParameter("messageText");

        if (requestIdStr == null || messageText == null || messageText.trim().isEmpty()) {
            response.sendRedirect("StatusServlet");
            return;
        }

        int requestId = Integer.parseInt(requestIdStr);

        try (Connection conn = DBUtil.getConnection()) {
            // Verify details and find the message receiver ID
            int requesterId = 0;
            int receiverId = 0;
            String courseCode = "";
            String checkSql = "SELECT requester_id, receiver_id, have_course_code FROM exchange_requests WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, requestId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        requesterId = rs.getInt("requester_id");
                        receiverId = rs.getInt("receiver_id");
                        courseCode = rs.getString("have_course_code");
                    }
                }
            }

            if (senderId != requesterId && senderId != receiverId) {
                response.sendRedirect("StatusServlet");
                return;
            }

            int messageReceiverId = (senderId == requesterId) ? receiverId : requesterId;

            // Insert message
            String insertSql = "INSERT INTO messages (request_id, sender_id, receiver_id, message_text) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, requestId);
                ps.setInt(2, senderId);
                ps.setInt(3, messageReceiverId);
                ps.setString(4, messageText.trim());
                ps.executeUpdate();
            }

            // Create notification for receiver
            String senderUsername = (String) session.getAttribute("username");
            String notifySql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
            try (PreparedStatement psNotify = conn.prepareStatement(notifySql)) {
                psNotify.setInt(1, messageReceiverId);
                psNotify.setString(2, "💬 New message from " + senderUsername + " regarding slot " + courseCode + ".");
                psNotify.executeUpdate();
            }

            response.sendRedirect("ChatServlet?requestId=" + requestId);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
