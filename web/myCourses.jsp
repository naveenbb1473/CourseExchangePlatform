<%@ page import="java.util.List" %>
<%@ page import="com.courseexchange.models.Course" %>
<%
    List<Course> haveList = (List<Course>) request.getAttribute("haveList");
    List<Course> wantList = (List<Course>) request.getAttribute("wantList");
    jakarta.servlet.http.HttpSession s = request.getSession(false);
    String msg = null;
    if (s != null && s.getAttribute("message") != null) {
        msg = (String) s.getAttribute("message");
        s.removeAttribute("message");
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
    <title>My Courses - Course Exchange</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <main class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px;">
            <div>
                <h1 style="font-size: 28px; font-weight: 700; color: var(--text);">My Courses</h1>
                <p style="color: var(--text-muted); font-size: 16px; margin-top: 4px;">Track and manage your registered course slots.</p>
            </div>
            <a href="addCourse.jsp" class="btn btn-primary">➕ Add More Courses</a>
        </div>

        <% if (msg != null) { %>
            <div class="alert alert-success"><%= com.courseexchange.utils.SecurityUtil.escapeHtml(msg) %></div>
        <% } %>

        <div class="split-grid">
            <!-- HAVE COURSES CARD -->
            <div class="card">
                <h3 class="card-title" style="color: var(--primary); display: flex; align-items: center; gap: 8px; border-bottom: 2px solid var(--border); padding-bottom: 12px; margin-bottom: 16px;">
                    <span>📤</span> Offerd (Have)
                </h3>
                <% if (haveList != null && !haveList.isEmpty()) { %>
                    <div class="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th>Code</th>
                                    <th>Name</th>
                                    <th>Slot</th>
                                    <th style="text-align: right;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Course c : haveList) { %>
                                    <tr>
                                        <td><strong><%= com.courseexchange.utils.SecurityUtil.escapeHtml(c.getCourseCode()) %></strong></td>
                                        <td><%= com.courseexchange.utils.SecurityUtil.escapeHtml(c.getCourseName()) %></td>
                                        <td><span class="badge" style="background-color:#f1f5f9; color:#475569;"><%= com.courseexchange.utils.SecurityUtil.escapeHtml(c.getSlot()) %></span></td>
                                        <td style="text-align: right;">
                                            <form action="DeleteCourseServlet" method="post" style="display:inline;">
                                                <input type="hidden" name="courseId" value="<%= c.getId() %>"/>
                                                <button type="submit" class="btn btn-danger" style="padding: 6px 12px; font-size: 12px;" onclick="return confirm('Are you sure you want to delete this offered course?');">Delete</button>
                                            </form>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } else { %>
                    <div class="no-data">You haven't added any offered slots yet.</div>
                <% } %>
            </div>

            <!-- WANT COURSES CARD -->
            <div class="card">
                <h3 class="card-title" style="color: var(--secondary); display: flex; align-items: center; gap: 8px; border-bottom: 2px solid var(--border); padding-bottom: 12px; margin-bottom: 16px;">
                    <span>📥</span> Requested (Want)
                </h3>
                <% if (wantList != null && !wantList.isEmpty()) { %>
                    <div class="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th>Code</th>
                                    <th>Name</th>
                                    <th>Slot</th>
                                    <th style="text-align: right;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Course c : wantList) { %>
                                    <tr>
                                        <td><strong><%= com.courseexchange.utils.SecurityUtil.escapeHtml(c.getCourseCode()) %></strong></td>
                                        <td><%= com.courseexchange.utils.SecurityUtil.escapeHtml(c.getCourseName()) %></td>
                                        <td><span class="badge" style="background-color:#f1f5f9; color:#475569;"><%= com.courseexchange.utils.SecurityUtil.escapeHtml(c.getSlot()) %></span></td>
                                        <td style="text-align: right;">
                                            <form action="DeleteCourseServlet" method="post" style="display:inline;">
                                                <input type="hidden" name="courseId" value="<%= c.getId() %>"/>
                                                <button type="submit" class="btn btn-danger" style="padding: 6px 12px; font-size: 12px;" onclick="return confirm('Are you sure you want to delete this requested course?');">Delete</button>
                                            </form>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } else { %>
                    <div class="no-data">You haven't added any requested slots yet.</div>
                <% } %>
            </div>
        </div>
    </main>
</body>
</html>
