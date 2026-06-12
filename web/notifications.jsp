<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.courseexchange.utils.SecurityUtil" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<html>
<head>
    <title>Notifications - Course Exchange</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .notification-item {
            padding: 16px 20px;
            border-bottom: 1px solid var(--border);
            display: flex;
            justify-content: space-between;
            align-items: center;
            transition: var(--transition);
        }
        .notification-item:last-child {
            border-bottom: none;
        }
        .notification-item.unread {
            background-color: #f8fafc;
            border-left: 4px solid var(--primary);
        }
        .notification-item.read {
            border-left: 4px solid transparent;
        }
        .notification-content {
            font-size: 14px;
            color: var(--text);
        }
        .notification-time {
            font-size: 11px;
            color: var(--text-muted);
            margin-top: 4px;
        }
        .actions-bar {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <main class="container">
        <div style="text-align: left; margin-bottom: 30px;">
            <h1 style="font-size: 28px; font-weight: 700; color: var(--text);">Notifications</h1>
            <p style="color: var(--text-muted); font-size: 16px; margin-top: 4px;">Stay updated on your exchange requests and message activities.</p>
        </div>

        <%
            String msg = (String) session.getAttribute("message");
            if (msg != null && !msg.trim().isEmpty()) {
        %>
            <div class="alert alert-success"><%= SecurityUtil.escapeHtml(msg) %></div>
        <%
                session.removeAttribute("message");
            }
        %>

        <%
            List<Map<String, Object>> notifications = (List<Map<String, Object>>) request.getAttribute("notifications");
            if (notifications != null && !notifications.isEmpty()) {
        %>
            <div class="actions-bar">
                <form action="NotificationsServlet" method="post" style="margin:0;">
                    <input type="hidden" name="action" value="markRead">
                    <button type="submit" class="btn btn-secondary" style="padding: 8px 16px;">Mark All as Read</button>
                </form>
                <form action="NotificationsServlet" method="post" style="margin:0;">
                    <input type="hidden" name="action" value="clear">
                    <button type="submit" class="btn btn-danger" style="padding: 8px 16px;">Clear All</button>
                </form>
            </div>

            <div class="card" style="padding: 0; overflow: hidden;">
                <%
                    for (Map<String, Object> n : notifications) {
                        boolean isUnread = !(boolean) n.get("isRead");
                        String message = (String) n.get("message");
                        java.util.Date date = (java.util.Date) n.get("createdAt");
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy hh:mm a");
                        String formattedDate = sdf.format(date);
                %>
                    <div class="notification-item <%= isUnread ? "unread" : "read" %>">
                        <div>
                            <div class="notification-content">
                                <% if (isUnread) { %>
                                    <strong><%= SecurityUtil.escapeHtml(message) %></strong>
                                <% } else { %>
                                    <%= SecurityUtil.escapeHtml(message) %>
                                <% } %>
                            </div>
                            <div class="notification-time"><%= formattedDate %></div>
                        </div>
                        <% if (isUnread) { %>
                            <span class="badge badge-pending" style="font-size: 10px;">New</span>
                        <% } %>
                    </div>
                <% } %>
            </div>
        <% } else { %>
            <div class="card" style="text-align: center; padding: 40px;">
                <div style="font-size: 48px; margin-bottom: 16px;">🔔</div>
                <h3 style="margin-bottom: 8px;">No Notifications Yet</h3>
                <p style="color: var(--text-muted); font-size: 14px;">We'll let you know when someone requests a swap or updates status.</p>
            </div>
        <% } %>
    </main>
</body>
</html>
