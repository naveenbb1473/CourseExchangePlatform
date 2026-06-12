package com.courseexchange.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/UpdateRequestServlet")
public class UpdateRequestServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/course_exchange";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "naveen";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int requestId = Integer.parseInt(request.getParameter("requestId"));
        String action = request.getParameter("action");

        String status = "pending";
        boolean setAcceptedTime = false;

        switch (action.toLowerCase()) {
            case "accept":
                status = "accepted";
                setAcceptedTime = true;
                break;
            case "reject":
                status = "rejected";
                break;
            case "complete":
                status = "completed";
                break;
        }

        try (Connection conn = com.courseexchange.utils.DBUtil.getConnection()) {
            // Retrieve requester details before updating
            int requesterId = 0;
            String courseCode = "";
            String findReqSql = "SELECT requester_id, have_course_code FROM exchange_requests WHERE id = ?";
            try (PreparedStatement psReq = conn.prepareStatement(findReqSql)) {
                psReq.setInt(1, requestId);
                try (ResultSet rs = psReq.executeQuery()) {
                    if (rs.next()) {
                        requesterId = rs.getInt("requester_id");
                        courseCode = rs.getString("have_course_code");
                    }
                }
            }

            String sql;
            if (setAcceptedTime) {
                sql = "UPDATE exchange_requests SET status = ?, accepted_at = NOW() WHERE id = ?";
            } else {
                sql = "UPDATE exchange_requests SET status = ? WHERE id = ?";
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setInt(2, requestId);
                ps.executeUpdate();
            }

            // Create notification for the requester
            if (requesterId > 0) {
                String notificationMsg = "";
                if ("accepted".equals(status)) {
                    notificationMsg = "Your swap request for slot " + courseCode + " was accepted! Contact details unlocked.";
                } else if ("rejected".equals(status)) {
                    notificationMsg = "Your swap request for slot " + courseCode + " was rejected.";
                } else if ("completed".equals(status)) {
                    notificationMsg = "Your swap request for slot " + courseCode + " has been marked as completed.";
                }

                if (!notificationMsg.isEmpty()) {
                    String notifySql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
                    try (PreparedStatement psNotify = conn.prepareStatement(notifySql)) {
                        psNotify.setInt(1, requesterId);
                        psNotify.setString(2, notificationMsg);
                        psNotify.executeUpdate();
                    }
                }
            }

            HttpSession session = request.getSession();
            String message;
            if ("accepted".equals(status)) {
                message = "✅ Exchange accepted. Contact details unlocked for private chat!";
            } else if ("rejected".equals(status)) {
                message = "❌ Exchange request rejected.";
            } else if ("completed".equals(status)) {
                message = "🎉 Exchange completed successfully.";
            } else {
                message = "Request updated.";
            }
            session.setAttribute("msg", message);

            response.sendRedirect("StatusServlet");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
