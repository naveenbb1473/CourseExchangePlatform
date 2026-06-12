<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
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
    <title>Add Course - Have / Want</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <main class="container">
        <div style="text-align: left; margin-bottom: 30px;">
            <h1 style="font-size: 28px; font-weight: 700; color: var(--text);">Add Courses</h1>
            <p style="color: var(--text-muted); font-size: 16px; margin-top: 4px;">Register the courses you currently hold (Offer) and the courses you want to swap into (Request).</p>
        </div>

        <%
            String msg = (String) session.getAttribute("message");
            if (msg != null && !msg.trim().isEmpty()) {
        %>
            <div class="alert alert-success"><%= com.courseexchange.utils.SecurityUtil.escapeHtml(msg) %></div>
        <%
                session.removeAttribute("message");
            }
        %>

        <div class="split-grid">
            <!-- HAVE form -->
            <div class="card">
                <h3 class="card-title" style="color: var(--primary); display: flex; align-items: center; gap: 8px;">
                    <span>📤</span> I HAVE (Offer)
                </h3>
                <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 20px;">List a course slot you currently have registered that you want to trade away.</p>
                <form action="AddCourseServlet" method="post">
                    <input type="hidden" name="type" value="have">
                    <div class="form-group">
                        <label>Course Code</label>
                        <input type="text" name="courseCode" placeholder="e.g., CSE1007" required>
                    </div>
                    <div class="form-group">
                        <label>Course Name</label>
                        <input type="text" name="courseName" placeholder="e.g., Software Engineering" required>
                    </div>
                    <div class="form-group">
                        <label>Slot</label>
                        <input type="text" name="slot" placeholder="e.g., A1+TA1" required>
                    </div>
                    <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">Add Offered Course</button>
                </form>
            </div>

            <!-- WANT form -->
            <div class="card">
                <h3 class="card-title" style="color: var(--secondary); display: flex; align-items: center; gap: 8px;">
                    <span>📥</span> I WANT (Request)
                </h3>
                <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 20px;">List a course slot you would like to get in exchange for your offered courses.</p>
                <form action="AddCourseServlet" method="post">
                    <input type="hidden" name="type" value="want">
                    <div class="form-group">
                        <label>Course Code</label>
                        <input type="text" name="courseCode" placeholder="e.g., CSE2005" required>
                    </div>
                    <div class="form-group">
                        <label>Course Name</label>
                        <input type="text" name="courseName" placeholder="e.g., Database Management Systems" required>
                    </div>
                    <div class="form-group">
                        <label>Slot</label>
                        <input type="text" name="slot" placeholder="e.g., B2+TB2" required>
                    </div>
                    <button type="submit" class="btn btn-success" style="width: 100%; margin-top: 10px;">Add Requested Course</button>
                </form>
            </div>
        </div>
    </main>
</body>
</html>
