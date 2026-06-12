package com.courseexchange.servlets;

import com.courseexchange.utils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/BorrowCourseServlet")
public class BorrowCourseServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int requesterId = (Integer) session.getAttribute("userId");
        String courseIdStr = request.getParameter("courseId");       // owner's course id
        String offerCourseIdStr = request.getParameter("offerCourseId"); // requester's have-course id

        if (courseIdStr == null || offerCourseIdStr == null) {
            session.setAttribute("msg", "Invalid request parameters.");
            response.sendRedirect("StatusServlet");
            return;
        }

        int courseId = Integer.parseInt(courseIdStr);
        int offerCourseId = Integer.parseInt(offerCourseIdStr);

        try (Connection conn = DBUtil.getConnection()) {

            // fetch owner id and owner's course code
            String ownerSql = "SELECT user_id AS owner_id, course_code AS owner_code FROM courses WHERE id = ?";
            int receiverId = -1;
            String ownerCode = null;
            try (PreparedStatement ps = conn.prepareStatement(ownerSql)) {
                ps.setInt(1, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        receiverId = rs.getInt("owner_id");
                        ownerCode = rs.getString("owner_code");
                    } else {
                        session.setAttribute("msg", "Requested course not found.");
                        response.sendRedirect("StatusServlet");
                        return;
                    }
                }
            }

            // ensure requester cannot request their own course
            if (receiverId == requesterId) {
                session.setAttribute("msg", "You cannot request your own course.");
                response.sendRedirect("StatusServlet");
                return;
            }

            // fetch requester's offered course code
            String offerSql = "SELECT course_code FROM courses WHERE id = ? AND user_id = ? AND type = 'have'";
            String offeredCode = null;
            try (PreparedStatement ps = conn.prepareStatement(offerSql)) {
                ps.setInt(1, offerCourseId);
                ps.setInt(2, requesterId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        offeredCode = rs.getString("course_code");
                    } else {
                        session.setAttribute("msg", "Your offered course not found or not a HAVE course.");
                        response.sendRedirect("StatusServlet");
                        return;
                    }
                }
            }

            // Check for duplicate request: requester -> same course owner/course
            String checkSql = "SELECT id FROM exchange_requests WHERE requester_id = ? AND course_id = ? AND status IN ('pending','accepted')";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, requesterId);
                ps.setInt(2, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        session.setAttribute("msg", "You already requested this course.");
                        response.sendRedirect("StatusServlet");
                        return;
                    }
                }
            }

            // Insert new borrow request with receiver and both codes
            String insertSql = "INSERT INTO exchange_requests (requester_id, receiver_id, have_course_code, want_course_code, course_id, status) VALUES (?, ?, ?, ?, ?, 'pending')";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, requesterId);
                ps.setInt(2, receiverId);
                ps.setString(3, offeredCode);
                ps.setString(4, ownerCode);
                ps.setInt(5, courseId);
                ps.executeUpdate();
            }

            session.setAttribute("msg", "Request sent successfully.");
            response.sendRedirect("StatusServlet");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}
