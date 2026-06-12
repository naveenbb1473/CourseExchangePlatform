package com.courseexchange.servlets;

import com.courseexchange.utils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/ExchangeRequestServlet")
public class ExchangeRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int requesterId = (int) session.getAttribute("userId");
        String receiverIdStr = request.getParameter("receiverId");
        String haveCourseCode = request.getParameter("haveCourseCode");
        String wantCourseCode = request.getParameter("wantCourseCode");
        String courseIdStr = request.getParameter("courseId");

        try {
            if (receiverIdStr == null || courseIdStr == null) {
                session.setAttribute("message", "Invalid data.");
                response.sendRedirect("SelectCourseServlet");
                return;
            }

            int receiverId = Integer.parseInt(receiverIdStr);
            int courseId = Integer.parseInt(courseIdStr);

            try (Connection conn = DBUtil.getConnection()) {

                // Check if a pending or accepted request already exists
                String checkSql = "SELECT COUNT(*) FROM exchange_requests " +
                        "WHERE requester_id = ? AND receiver_id = ? AND have_course_code = ? AND want_course_code = ? " +
                        "AND status IN ('pending', 'accepted')";
                try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                    ps.setInt(1, requesterId);
                    ps.setInt(2, receiverId);
                    ps.setString(3, haveCourseCode);
                    ps.setString(4, wantCourseCode);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        session.setAttribute("message", "Request already sent.");
                        response.sendRedirect("SelectCourseServlet");
                        return;
                    }
                }

                // Insert new exchange request
                String insertSql = "INSERT INTO exchange_requests " +
                        "(requester_id, receiver_id, have_course_code, want_course_code, course_id, status) " +
                        "VALUES (?, ?, ?, ?, ?, 'pending')";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setInt(1, requesterId);
                    ps.setInt(2, receiverId);
                    ps.setString(3, haveCourseCode);
                    ps.setString(4, wantCourseCode);
                    ps.setInt(5, courseId);
                    ps.executeUpdate();
                }

                // Create notification for receiver
                String requesterUsername = (String) session.getAttribute("username");
                String notifySql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
                try (PreparedStatement psNotify = conn.prepareStatement(notifySql)) {
                    psNotify.setInt(1, receiverId);
                    psNotify.setString(2, "New swap request received from " + requesterUsername + " for slot " + haveCourseCode + ".");
                    psNotify.executeUpdate();
                }

                session.setAttribute("message", "Request sent successfully.");
                response.sendRedirect("StatusServlet");

            } catch (SQLException e) {
                e.printStackTrace();
                session.setAttribute("message", "Database error: " + e.getMessage());
                response.sendRedirect("SelectCourseServlet");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
