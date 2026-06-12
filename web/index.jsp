<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login - Course Exchange</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="card auth-card">
            <div class="auth-header">
                <h1>Course Exchange</h1>
                <p>Login to swap and manage your VIT courses</p>
            </div>
            <%
                String msg = (String) session.getAttribute("message");
                if (msg != null && !msg.trim().isEmpty()) {
            %>
                <div class="alert alert-error"><%= msg %></div>
            <%
                    session.removeAttribute("message");
                }
            %>
            <form action="LoginServlet" method="post">
                <div class="form-group">
                    <label>Username / Email / Reg No</label>
                    <input type="text" name="username" placeholder="Enter your credentials" required>
                </div>
                <div class="form-group">
                    <label>Password</label>
                    <input type="password" name="password" placeholder="Enter your password" required>
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">Login</button>
            </form>
            <div style="text-align: center; margin-top: 20px; font-size: 14px; color: var(--text-muted);">
                Don't have an account? <a href="register.jsp">Register here</a>
            </div>
        </div>
    </div>
</body>
</html>
