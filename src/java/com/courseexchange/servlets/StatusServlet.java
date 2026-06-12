package com.courseexchange.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/StatusServlet")
public class StatusServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/course_exchange";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "naveen";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        try (Connection conn = com.courseexchange.utils.DBUtil.getConnection()) {

            // ===== Requests RECEIVED by the current user =====
            String receivedSql = """
                SELECT DISTINCT er.id, er.status, er.have_course_code, er.want_course_code,
                       ur.username AS requester, ur.email AS requesterEmail, ur.phone AS requesterPhone,
                       c.course_name AS course_name,
                       er.created_at
                FROM exchange_requests er
                JOIN users ur ON er.requester_id = ur.id
                JOIN courses c ON c.id = er.course_id
                WHERE er.receiver_id = ?
                ORDER BY er.created_at DESC
            """;

            PreparedStatement psReceived = conn.prepareStatement(receivedSql);
            psReceived.setInt(1, userId);
            ResultSet rsReceived = psReceived.executeQuery();

            List<Map<String, Object>> receivedRequests = new ArrayList<>();

            while (rsReceived.next()) {
                Map<String, Object> r = new HashMap<>();
                r.put("requestId", rsReceived.getInt("id"));
                r.put("courseCode", rsReceived.getString("have_course_code"));
                r.put("courseName", rsReceived.getString("course_name"));
                r.put("requester", rsReceived.getString("requester"));
                r.put("status", rsReceived.getString("status"));

                if ("accepted".equalsIgnoreCase(rsReceived.getString("status"))) {
                    r.put("email", rsReceived.getString("requesterEmail"));
                    r.put("phone", rsReceived.getString("requesterPhone"));
                } else {
                    r.put("email", null);
                    r.put("phone", null);
                }
                receivedRequests.add(r);
            }

            rsReceived.close();
            psReceived.close();

            // ===== Requests SENT by the current user =====
            String sentSql = """
                SELECT DISTINCT er.id, er.status, er.have_course_code, er.want_course_code,
                       u.username AS owner, u.email AS ownerEmail, u.phone AS ownerPhone,
                       c.course_name AS course_name,
                       er.created_at
                FROM exchange_requests er
                JOIN users u ON er.receiver_id = u.id
                JOIN courses c ON c.id = er.course_id
                WHERE er.requester_id = ?
                ORDER BY er.created_at DESC
            """;


            PreparedStatement psSent = conn.prepareStatement(sentSql);
            psSent.setInt(1, userId);
            ResultSet rsSent = psSent.executeQuery();

            List<Map<String, Object>> sentRequests = new ArrayList<>();

            while (rsSent.next()) {
                Map<String, Object> s = new HashMap<>();
                s.put("requestId", rsSent.getInt("id"));
                s.put("courseCode", rsSent.getString("have_course_code"));
                s.put("courseName", rsSent.getString("course_name"));
                s.put("owner", rsSent.getString("owner"));
                s.put("status", rsSent.getString("status"));

                if ("accepted".equalsIgnoreCase(rsSent.getString("status"))) {
                    s.put("email", rsSent.getString("ownerEmail"));
                    s.put("phone", rsSent.getString("ownerPhone"));
                } else {
                    s.put("email", null);
                    s.put("phone", null);
                }
                sentRequests.add(s);
            }

            rsSent.close();
            psSent.close();
            conn.close();

            // Attach both lists to request
            request.setAttribute("receivedRequests", receivedRequests);
            request.setAttribute("sentRequests", sentRequests);

            // Show optional message
            String msg = (String) session.getAttribute("msg");
            if (msg != null) {
                request.setAttribute("msg", msg);
                session.removeAttribute("msg");
            }

            request.getRequestDispatcher("status.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
