<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("username") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    String username = (String) session.getAttribute("username");
%>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setDateHeader("Expires", 0); // Proxies

    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<html>
<head>
    <title>Home Dashboard - Course Exchange</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <main class="container">
        <div style="text-align: left; margin-bottom: 40px;">
            <h1 style="font-size: 28px; font-weight: 700; color: var(--text);">Dashboard</h1>
            <p style="color: var(--text-muted); font-size: 16px; margin-top: 4px;">Welcome back, <strong><%= com.courseexchange.utils.SecurityUtil.escapeHtml(username) %></strong>! Manage and swap your courses below.</p>
        </div>

        <div class="dashboard-grid">
            <div class="dashboard-card">
                <div class="icon">➕</div>
                <h3>Add Course</h3>
                <p>Register the courses you currently have or the ones you want to get.</p>
                <a href="addCourse.jsp" class="btn btn-primary" style="width: 100%;">Add Course</a>
            </div>

            <div class="dashboard-card">
                <div class="icon">📚</div>
                <h3>My Courses</h3>
                <p>View, manage, or delete your registered slot preferences.</p>
                <a href="MyCoursesServlet" class="btn btn-secondary" style="width: 100%;">View My Courses</a>
            </div>

            <div class="dashboard-card">
                <div class="icon">🔍</div>
                <h3>Select Courses</h3>
                <p>Browse course slots offered by other students and request swaps.</p>
                <a href="SelectCourseServlet" class="btn btn-secondary" style="width: 100%;">Browse Offers</a>
            </div>

            <div class="dashboard-card">
                <div class="icon">✉️</div>
                <h3>Status Tracker</h3>
                <p>Track progress of your sent exchange requests and review received requests.</p>
                <a href="StatusServlet" class="btn btn-secondary" style="width: 100%;">Track Status</a>
            </div>
        </div>
    </main>
</body>
</html>
