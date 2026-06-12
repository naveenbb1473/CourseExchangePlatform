<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register - Course Exchange (VIT)</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="auth-wrapper">
    <div class="card auth-card">
        <div class="auth-header">
            <h1>Create Account</h1>
            <p>VIT Students Course Exchange Platform</p>
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
        
        <form action="RegisterServlet" method="post">
            <div class="form-group">
                <label>Username</label>
                <input type="text" id="username" name="username" placeholder="Full name" required>
            </div>

            <div class="form-group">
                <label>Registration Number</label>
                <input type="text" id="regNumber" name="reg_number" placeholder="e.g., 22BCE0123" required pattern="[0-9]{2}[A-Z]{3}[0-9]{4}" title="Enter valid registration number like 22BCE0123">
            </div>

            <div class="form-group">
                <label>VIT Email</label>
                <input type="email" id="email" name="email" placeholder="yourid@vitstudent.ac.in" required pattern="[a-zA-Z0-9._%+-]+@vitstudent\.ac\.in" title="Email must end with @vitstudent.ac.in">
            </div>
            
            <div class="form-group">
                <label for="phone">Phone Number</label>
                <input type="text" id="phone" name="phone" placeholder="10 digit phone number" required pattern="[0-9]{10}" title="Enter 10 digit phone number">
            </div>

            <div class="form-group">
                <label>Password</label>
                <input type="password" id="password" name="password" placeholder="Password (min 6 characters)" required minlength="6">
            </div>

            <div class="form-group">
                <label>Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirm_password" placeholder="Confirm Password" required minlength="6">
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">Register</button>
        </form>
        <div style="text-align: center; margin-top: 20px; font-size: 14px; color: var(--text-muted);">
            Already registered? <a href="index.jsp">Login here</a>
        </div>
    </div>
</div>
</body>
</html>
