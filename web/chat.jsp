<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.courseexchange.utils.SecurityUtil" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    int currentUserId = (int) session.getAttribute("userId");
    Map<String, Object> details = (Map<String, Object>) request.getAttribute("chatDetails");
    List<Map<String, Object>> messages = (List<Map<String, Object>>) request.getAttribute("messagesList");

    if (details == null) {
        response.sendRedirect("StatusServlet");
        return;
    }

    int requesterId = (int) details.get("requesterId");
    int receiverId = (int) details.get("receiverId");
    String requesterName = (String) details.get("requesterName");
    String receiverName = (String) details.get("receiverName");
    String partnerName = (currentUserId == requesterId) ? receiverName : requesterName;
    String haveCourseCode = (String) details.get("haveCourseCode");
    String wantCourseCode = (String) details.get("wantCourseCode");
    String status = (String) details.get("status");
    int requestId = (int) details.get("requestId");
%>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<html>
<head>
    <title>Chat with <%= SecurityUtil.escapeHtml(partnerName) %> - Course Exchange</title>
    <!-- Auto-refresh every 15 seconds to fetch new messages without JS -->
    <meta http-equiv="refresh" content="15">
    <link rel="stylesheet" href="css/style.css">
    <style>
        .chat-layout {
            max-width: 800px;
            margin: 0 auto;
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            overflow: hidden;
            display: flex;
            flex-direction: column;
            height: 600px;
        }

        .chat-header-bar {
            background-color: #f8fafc;
            border-bottom: 1px solid var(--border);
            padding: 16px 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .partner-title {
            font-size: 16px;
            font-weight: 600;
            color: var(--text);
        }

        .swap-info {
            font-size: 12px;
            color: var(--text-muted);
            margin-top: 2px;
        }

        .chat-history {
            flex-grow: 1;
            padding: 24px;
            overflow-y: auto;
            display: flex;
            flex-direction: column;
            gap: 16px;
            background-color: #fafbfc;
        }

        .message-bubble {
            max-width: 70%;
            padding: 12px 16px;
            border-radius: 16px;
            font-size: 14px;
            line-height: 1.4;
            position: relative;
        }

        .message-incoming {
            background-color: var(--border);
            color: var(--text);
            align-self: flex-start;
            border-bottom-left-radius: 4px;
        }

        .message-outgoing {
            background-color: var(--primary);
            color: #ffffff;
            align-self: flex-end;
            border-bottom-right-radius: 4px;
        }

        .msg-time {
            font-size: 10px;
            color: var(--text-muted);
            margin-top: 4px;
            display: block;
            text-align: right;
        }

        .message-outgoing .msg-time {
            color: #e0e7ff;
        }

        .chat-input-bar {
            padding: 16px 24px;
            border-top: 1px solid var(--border);
            background: var(--surface);
        }

        .chat-form {
            display: flex;
            gap: 12px;
            margin-bottom: 0;
        }

        .chat-input {
            flex-grow: 1;
            border: 1px solid var(--border);
            border-radius: var(--radius-sm);
            padding: 12px;
            font-size: 14px;
            resize: none;
            height: 44px;
            box-sizing: border-box;
        }

        .chat-input:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
        }
    </style>
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <main class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
            <div>
                <a href="StatusServlet" style="font-size: 14px; font-weight: 500; display: inline-flex; align-items: center; gap: 4px;">
                    <span>←</span> Back to Status
                </a>
            </div>
            <div>
                <a href="ChatServlet?requestId=<%= requestId %>" class="btn btn-secondary" style="padding: 6px 12px; font-size: 13px;">
                    🔄 Refresh Chat
                </a>
            </div>
        </div>

        <div class="chat-layout">
            <!-- Header bar -->
            <div class="chat-header-bar">
                <div>
                    <div class="partner-title">💬 Chat with <%= SecurityUtil.escapeHtml(partnerName) %></div>
                    <div class="swap-info">
                        Swapping: <strong><%= SecurityUtil.escapeHtml(haveCourseCode) %></strong> 
                        ➔ <strong><%= SecurityUtil.escapeHtml(wantCourseCode != null ? wantCourseCode : "Any") %></strong>
                    </div>
                </div>
                <div>
                    <span class="badge badge-<%= status.toLowerCase() %>"><%= status %></span>
                </div>
            </div>

            <!-- Messages list -->
            <div class="chat-history">
                <%
                    if (messages != null && !messages.isEmpty()) {
                        for (Map<String, Object> msg : messages) {
                            int senderId = (int) msg.get("senderId");
                            String messageText = (String) msg.get("messageText");
                            java.util.Date date = (java.util.Date) msg.get("createdAt");
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a");
                            String timeStr = sdf.format(date);
                            boolean isOutgoing = (senderId == currentUserId);
                %>
                    <div class="message-bubble <%= isOutgoing ? "message-outgoing" : "message-incoming" %>">
                        <div><%= SecurityUtil.escapeHtml(messageText) %></div>
                        <span class="msg-time"><%= timeStr %></span>
                    </div>
                <%
                        }
                    } else {
                %>
                    <div class="no-data" style="margin: auto;">No messages yet. Send a message below to start the conversation!</div>
                <%
                    }
                %>
            </div>

            <!-- Input area -->
            <div class="chat-input-bar">
                <form action="ChatServlet" method="post" class="chat-form">
                    <input type="hidden" name="requestId" value="<%= requestId %>">
                    <input type="text" name="messageText" class="chat-input" placeholder="Type a message..." required autocomplete="off">
                    <button type="submit" class="btn btn-primary" style="height: 44px; padding: 0 24px;">Send</button>
                </form>
            </div>
        </div>
        
        <div style="text-align: center; margin-top: 12px; font-size: 12px; color: var(--text-muted);">
            This page auto-refreshes every 15 seconds.
        </div>
    </main>
</body>
</html>
