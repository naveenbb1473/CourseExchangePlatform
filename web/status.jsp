<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<html>
<head>
    <title>Status Page - Course Exchange</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <main class="container">
        <div style="text-align: left; margin-bottom: 30px;">
            <h1 style="font-size: 28px; font-weight: 700; color: var(--text);">Exchange Status</h1>
            <p style="color: var(--text-muted); font-size: 16px; margin-top: 4px;">Track and manage exchange requests that you have received from others or sent out yourself.</p>
        </div>

        <%
            String msg = (String) request.getAttribute("msg");
            if (msg != null && !msg.trim().isEmpty()) {
        %>
            <div class="alert alert-info"><%= com.courseexchange.utils.SecurityUtil.escapeHtml(msg) %></div>
        <%
            }
        %>

        <!-- RECEIVED REQUESTS -->
        <div class="card" style="margin-bottom: 40px;">
            <h3 class="card-title" style="color: var(--primary); display: flex; align-items: center; gap: 8px; border-bottom: 2px solid var(--border); padding-bottom: 12px;">
                <span>📩</span> Requests You Received (as Owner)
            </h3>
            
            <%
                List<Map<String, Object>> receivedRequests = (List<Map<String, Object>>) request.getAttribute("receivedRequests");
                if (receivedRequests != null && !receivedRequests.isEmpty()) {
            %>
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>Course Code</th>
                            <th>Course Name</th>
                            <th>Requester</th>
                            <th>Status</th>
                            <th style="text-align: right;">Action / Contact</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            for (Map<String, Object> r : receivedRequests) {
                                String status = r.get("status").toString().trim().toLowerCase();
                        %>
                        <tr>
                            <td><strong><%= com.courseexchange.utils.SecurityUtil.escapeHtml((String)r.get("courseCode")) %></strong></td>
                            <td><%= com.courseexchange.utils.SecurityUtil.escapeHtml((String)r.get("courseName")) %></td>
                            <td><%= com.courseexchange.utils.SecurityUtil.escapeHtml((String)r.get("requester")) %></td>
                            <td><span class="badge badge-<%= status %>"><%= com.courseexchange.utils.SecurityUtil.escapeHtml((String)r.get("status")) %></span></td>
                            <td style="text-align: right;">
                                <% if ("pending".equals(status)) { %>
                                    <form action="UpdateRequestServlet" method="post" style="display:inline-block; margin: 0;">
                                        <input type="hidden" name="requestId" value="<%= r.get("requestId") %>">
                                        <button type="submit" name="action" value="accept" class="btn btn-success" style="padding: 6px 12px; font-size: 12px; margin-right: 4px;">Accept</button>
                                        <button type="submit" name="action" value="reject" class="btn btn-danger" style="padding: 6px 12px; font-size: 12px;">Reject</button>
                                    </form>
                                <% } else if ("accepted".equals(status)) { %>
                                    <div style="text-align: left; background-color: #f8fafc; border: 1px solid var(--border); border-radius: var(--radius-sm); padding: 10px; display: inline-block;">
                                        <div style="font-weight: 600; color: var(--secondary); margin-bottom: 4px;">✅ Accepted</div>
                                        <div style="font-size: 12px; color: var(--text-muted); margin-bottom: 8px;">
                                            <strong>Contact Info:</strong><br>
                                            Email: <%= (r.get("email") != null) ? r.get("email") : "N/A" %><br>
                                            Phone: <%= (r.get("phone") != null) ? r.get("phone") : "N/A" %>
                                        </div>
                                        <a href="ChatServlet?requestId=<%= r.get("requestId") %>" class="btn btn-success" style="padding: 6px 10px; font-size: 11px; width: 100%; margin-bottom: 6px; display: block; text-align: center;">💬 Open Chat</a>
                                        <form action="UpdateRequestServlet" method="post" style="margin: 0; display: block; text-align: left;">
                                            <input type="hidden" name="requestId" value="<%= r.get("requestId") %>">
                                            <button type="submit" name="action" value="complete" class="btn btn-primary" style="padding: 6px 10px; font-size: 11px; width: 100%;">Mark as Completed</button>
                                        </form>
                                    </div>
                                <% } else if ("completed".equals(status)) { %>
                                    <div style="display: flex; flex-direction: column; align-items: flex-end; gap: 6px;">
                                        <span style="color: var(--secondary); font-weight: 600;">🎉 Completed</span>
                                        <a href="ChatServlet?requestId=<%= r.get("requestId") %>" class="btn btn-secondary" style="padding: 4px 8px; font-size: 11px; text-align: center;">💬 Chat History</a>
                                    </div>
                                <% } else { %>
                                    <span style="color: var(--danger); font-weight: 600;">❌ Rejected</span>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } else { %>
                <div class="no-data">No swap requests received yet.</div>
            <% } %>
        </div>

        <!-- SENT REQUESTS -->
        <div class="card">
            <h3 class="card-title" style="color: var(--secondary); display: flex; align-items: center; gap: 8px; border-bottom: 2px solid var(--border); padding-bottom: 12px;">
                <span>📤</span> Requests You Sent (as Requester)
            </h3>
            
            <%
                List<Map<String, Object>> sentRequests = (List<Map<String, Object>>) request.getAttribute("sentRequests");
                if (sentRequests != null && !sentRequests.isEmpty()) {
            %>
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>Course Code</th>
                            <th>Course Name</th>
                            <th>Owner</th>
                            <th>Status</th>
                            <th style="text-align: right;">Contact Info</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            for (Map<String, Object> s : sentRequests) {
                                String status = s.get("status").toString().trim().toLowerCase();
                        %>
                        <tr>
                            <td><strong><%= com.courseexchange.utils.SecurityUtil.escapeHtml((String)s.get("courseCode")) %></strong></td>
                            <td><%= com.courseexchange.utils.SecurityUtil.escapeHtml((String)s.get("courseName")) %></td>
                            <td><%= com.courseexchange.utils.SecurityUtil.escapeHtml((String)s.get("owner")) %></td>
                            <td><span class="badge badge-<%= status %>"><%= com.courseexchange.utils.SecurityUtil.escapeHtml((String)s.get("status")) %></span></td>
                            <td style="text-align: right;">
                                <% if ("accepted".equals(status)) { %>
                                    <div style="text-align: left; background-color: #f8fafc; border: 1px solid var(--border); border-radius: var(--radius-sm); padding: 10px; display: inline-block;">
                                        <div style="font-weight: 600; color: var(--secondary); margin-bottom: 4px;">✅ Accepted</div>
                                        <div style="font-size: 12px; color: var(--text-muted); margin-bottom: 8px;">
                                            <strong>Contact Info:</strong><br>
                                            Email: <%= (s.get("email") != null) ? s.get("email") : "N/A" %><br>
                                            Phone: <%= (s.get("phone") != null) ? s.get("phone") : "N/A" %>
                                        </div>
                                        <a href="ChatServlet?requestId=<%= s.get("requestId") %>" class="btn btn-success" style="padding: 6px 10px; font-size: 11px; width: 100%; display: block; text-align: center;">💬 Open Chat</a>
                                    </div>
                                <% } else if ("completed".equals(status)) { %>
                                    <div style="display: flex; flex-direction: column; align-items: flex-end; gap: 6px;">
                                        <span style="color: var(--secondary); font-weight: 600;">🎉 Completed</span>
                                        <a href="ChatServlet?requestId=<%= s.get("requestId") %>" class="btn btn-secondary" style="padding: 4px 8px; font-size: 11px; text-align: center;">💬 Chat History</a>
                                    </div>
                                <% } else { %>
                                    <span style="color: var(--text-muted); font-style: italic; font-size: 13px;">Hidden until accepted</span>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } else { %>
                <div class="no-data">You haven’t sent any requests yet.</div>
            <% } %>
        </div>
    </main>
</body>
</html>
