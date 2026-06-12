<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.courseexchange.utils.DBUtil" %>
<%
    String currentURI = request.getRequestURI();
    boolean isHome = currentURI.contains("home.jsp");
    boolean isAddCourse = currentURI.contains("addCourse.jsp");
    boolean isMyCourses = currentURI.contains("MyCoursesServlet") || currentURI.contains("myCourses.jsp") || currentURI.contains("DeleteCourseServlet");
    boolean isSelectCourse = currentURI.contains("SelectCourseServlet") || currentURI.contains("selectCourse.jsp") || currentURI.contains("ExchangeRequestServlet");
    boolean isStatus = currentURI.contains("StatusServlet") || currentURI.contains("status.jsp") || currentURI.contains("UpdateRequestServlet") || currentURI.contains("chat.jsp") || currentURI.contains("ChatServlet");
    boolean isNotifications = currentURI.contains("NotificationsServlet") || currentURI.contains("notifications.jsp");

    int unreadCount = 0;
    Integer loggedInUserId = (Integer) session.getAttribute("userId");
    if (loggedInUserId != null) {
        try (Connection conn = DBUtil.getConnection()) {
            String countSql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = false";
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                ps.setInt(1, loggedInUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        unreadCount = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            // Log or ignore connection failure quietly to prevent page break
        }
    }
%>
<header class="app-header">
    <div class="header-container">
        <div class="brand">
            <span>🔄</span> Course Exchange
        </div>
        <nav class="navbar">
            <a href="home.jsp" class="nav-link <%= isHome ? "active" : "" %>">Dashboard</a>
            <a href="addCourse.jsp" class="nav-link <%= isAddCourse ? "active" : "" %>">Add Course</a>
            <a href="MyCoursesServlet" class="nav-link <%= isMyCourses ? "active" : "" %>">My Courses</a>
            <a href="SelectCourseServlet" class="nav-link <%= isSelectCourse ? "active" : "" %>">Select Courses</a>
            <a href="StatusServlet" class="nav-link <%= isStatus ? "active" : "" %>">Status</a>
            <a href="NotificationsServlet" class="nav-link <%= isNotifications ? "active" : "" %>">
                Notifications 
                <% if (unreadCount > 0) { %>
                    <span class="badge badge-rejected" style="padding: 2px 6px; font-size: 11px; margin-left: 4px;"><%= unreadCount %></span>
                <% } %>
            </a>
            <a href="LogoutServlet" class="nav-link logout">Logout</a>
        </nav>
    </div>
</header>
