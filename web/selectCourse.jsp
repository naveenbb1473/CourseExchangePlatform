<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>Select Course | Course Exchange</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <main class="container">
        <div style="text-align: left; margin-bottom: 30px;">
            <h1 style="font-size: 28px; font-weight: 700; color: var(--text);">Request Course Exchange</h1>
            <p style="color: var(--text-muted); font-size: 16px; margin-top: 4px;">Find other students offering slots you want, and send them an exchange request.</p>
        </div>

        <%
            String msg = (String) session.getAttribute("message");
            if (msg != null && !msg.trim().isEmpty()) {
        %>
            <div class="alert alert-info"><%= com.courseexchange.utils.SecurityUtil.escapeHtml(msg) %></div>
        <%
                session.removeAttribute("message");
            }
        %>

        <div class="card" style="margin-bottom: 30px; padding: 20px;">
            <form action="SelectCourseServlet" method="get" style="display: flex; gap: 12px; justify-content: center; align-items: center; flex-wrap: wrap; margin-bottom: 0;">
                <label style="margin-bottom: 0; font-size: 14px;">Select Offered Course Code:</label>
                <select name="courseCode" style="width: auto; min-width: 200px;">
                    <option value="">-- Choose a Slot Code --</option>
                    <%
                        List<String> courseCodes = (List<String>) request.getAttribute("courseCodes");
                        String selectedCode = (String) request.getAttribute("selectedCode");
                        if (courseCodes != null) {
                            for (String code : courseCodes) {
                    %>
                    <option value="<%= com.courseexchange.utils.SecurityUtil.escapeHtml(code) %>" <%= (selectedCode != null && selectedCode.equals(code)) ? "selected" : "" %>>
                        <%= com.courseexchange.utils.SecurityUtil.escapeHtml(code) %>
                    </option>
                    <% } } %>
                </select>
                <button type="submit" class="btn btn-primary">Find Swaps</button>
            </form>
        </div>

        <%
            List<Map<String, Object>> usersWithCourse = (List<Map<String, Object>>) request.getAttribute("usersWithCourse");
            if (usersWithCourse != null && !usersWithCourse.isEmpty()) {
        %>
        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>Username</th>
                        <th>Reg No</th>
                        <th>Course Code</th>
                        <th>Course Name</th>
                        <th>Slot</th>
                        <th>They Want</th>
                        <th style="text-align: right;">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (Map<String, Object> row : usersWithCourse) {
                            String username = (String) row.get("username");
                            String regNo = (String) row.get("regNo");
                            String courseCode = (String) row.get("courseCode");
                            String courseName = (String) row.get("courseName");
                            String slot = (String) row.get("slot");
                            String wantCourse = (String) row.get("wantCourse");
                            int receiverId = (int) row.get("userId");
                            int courseId = (int) row.get("courseId");
                    %>
                    <tr>
                        <td><strong><%= com.courseexchange.utils.SecurityUtil.escapeHtml(username) %></strong></td>
                        <td><%= com.courseexchange.utils.SecurityUtil.escapeHtml(regNo) %></td>
                        <td><span class="badge" style="background-color:#eff6ff; color:#1d4ed8;"><%= com.courseexchange.utils.SecurityUtil.escapeHtml(courseCode) %></span></td>
                        <td><%= com.courseexchange.utils.SecurityUtil.escapeHtml(courseName) %></td>
                        <td><span class="badge" style="background-color:#f1f5f9; color:#475569;"><%= com.courseexchange.utils.SecurityUtil.escapeHtml(slot) %></span></td>
                        <td><%= (wantCourse != null && !wantCourse.isEmpty()) ? com.courseexchange.utils.SecurityUtil.escapeHtml(wantCourse) : "-" %></td>
                        <td style="text-align: right;">
                            <form action="ExchangeRequestServlet" method="post" style="display:inline;">
                                <input type="hidden" name="receiverId" value="<%= receiverId %>">
                                <input type="hidden" name="haveCourseCode" value="<%= courseCode %>">
                                <input type="hidden" name="wantCourseCode" value="<%= wantCourse %>">
                                <input type="hidden" name="courseId" value="<%= courseId %>">
                                <button type="submit" class="btn btn-success" style="padding: 8px 16px; font-size: 13px;">Request Exchange</button>
                            </form>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        <% } else if (selectedCode != null) { %>
            <div class="no-data">No students currently offer this course slot. Try another code.</div>
        <% } %>
    </main>
</body>
</html>
