# 🔄 VIT Course Exchange Platform

A premium, modern web application designed for VIT students to swap and manage their course slots seamlessly. The project features a responsive dashboard, a secure in-app private chat room, and a database-backed notification tracker, constructed entirely using **HTML5 and CSS3 (with zero client-side JavaScript)**.

---

## 🎨 Core Features

1. **Clean, Modern Dashboard:** Card-based landing page with dynamic hover effects, layout scaling, and unified typography powered by Google Fonts (Inter).
2. **Strictly JavaScript-Free (JS-Free) Flow:** Form entries validate natively using HTML5 validation attributes (`pattern`, `required`, `minlength`).
3. **Double-Sided Listings:** Separate panels to list slots you currently hold (**I HAVE / Offer**) and slots you want to exchange into (**I WANT / Request**).
4. **Search & Match Engine:** Filter search results by the course slot codes you desire and send exchange requests directly.
5. **Status Tracker:** Monitor sent and received exchange requests. Displays unread alerts and reveals swap partner contact details (email/phone) only after a request is accepted.
6. **💬 Private Secure Chat Room:** Once a request is accepted, a private chat room is unlocked. It includes message bubble bubbles, manual refresh controls, and a `<meta http-equiv="refresh" content="15">` autorefresh tag to fetch new messages automatically without client-side scripts.
7. **🔔 Live Notification System:** Real-time database-backed notification log and alert badges that update when a request is sent, accepted/rejected, or when a new chat message arrives.

---

## 🛠️ Technology Stack

* **Frontend:** Vanilla HTML5, CSS3 (Custom Variables, CSS Grid, Flexbox)
* **Backend:** Java Servlet (Jakarta EE 10), JavaServer Pages (JSP)
* **Database:** MySQL 8.0+
* **Server:** Apache Tomcat 10.1.x

---

## 🗄️ Database Setup & Schema

1. Log in to your MySQL command line or client:
   ```bash
   mysql -u root -p
   ```
2. Run the database configuration and table creation script:
   ```sql
   CREATE DATABASE IF NOT EXISTS course_exchange;
   USE course_exchange;

   -- 1. Users Table
   CREATE TABLE IF NOT EXISTS users (
       id INT AUTO_INCREMENT PRIMARY KEY,
       username VARCHAR(100) NOT NULL UNIQUE,
       reg_number VARCHAR(100) NOT NULL UNIQUE,
       email VARCHAR(100) NOT NULL UNIQUE,
       password VARCHAR(255) NOT NULL,
       phone VARCHAR(20) NOT NULL
   );

   -- 2. Courses Table
   CREATE TABLE IF NOT EXISTS courses (
       id INT AUTO_INCREMENT PRIMARY KEY,
       user_id INT NOT NULL,
       type VARCHAR(10) NOT NULL, -- 'have' or 'want'
       course_code VARCHAR(50) NOT NULL,
       course_name VARCHAR(150) NOT NULL,
       slot VARCHAR(50) NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
   );

   -- 3. Exchange Requests Table
   CREATE TABLE IF NOT EXISTS exchange_requests (
       id INT AUTO_INCREMENT PRIMARY KEY,
       requester_id INT NOT NULL,
       receiver_id INT NOT NULL,
       have_course_code VARCHAR(50) NOT NULL,
       want_course_code VARCHAR(50) NOT NULL,
       course_id INT NOT NULL,
       status VARCHAR(20) DEFAULT 'pending', -- 'pending', 'accepted', 'rejected', 'completed'
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       accepted_at TIMESTAMP NULL DEFAULT NULL,
       FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
       FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
       FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
   );

   -- 4. Notifications Table
   CREATE TABLE IF NOT EXISTS notifications (
       id INT AUTO_INCREMENT PRIMARY KEY,
       user_id INT NOT NULL,
       message VARCHAR(255) NOT NULL,
       is_read BOOLEAN DEFAULT FALSE,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
   );

   -- 5. Messages Table (Chat)
   CREATE TABLE IF NOT EXISTS messages (
       id INT AUTO_INCREMENT PRIMARY KEY,
       request_id INT NOT NULL,
       sender_id INT NOT NULL,
       receiver_id INT NOT NULL,
       message_text TEXT NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       FOREIGN KEY (request_id) REFERENCES exchange_requests(id) ON DELETE CASCADE,
       FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
       FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
   );
   ```

---

## ⚙️ Installation & Running the Project

### 1. Pre-requisites
* Ensure JDK 17 or higher is installed.
* Download and configure [Apache Tomcat 10](https://tomcat.apache.org/).
* Start the MySQL database service (`MySQL80`).

### 2. Compilation
Compile the Java servlet classes to the build directory. Replace paths below with your local paths if different:
```powershell
# Set Tomcat classpath environment variables and compile:
javac -d build/web/WEB-INF/classes -sourcepath src/java -cp "C:\tomcat\apache-tomcat-10.1.44-windows-x64\apache-tomcat-10.1.44\lib\servlet-api.jar;C:\tomcat\apache-tomcat-10.1.44-windows-x64\apache-tomcat-10.1.44\lib\jsp-api.jar;web/WEB-INF/lib/*" src/java/com/courseexchange/utils/*.java src/java/com/courseexchange/servlets/*.java
```

### 3. Deployment
Copy the updated JSP pages, includes, assets, and libraries to the Tomcat build web output:
```powershell
Copy-Item -Path web\* -Destination build/web/ -Recurse -Force
```

### 4. Running the Tomcat Server
Set `CATALINA_HOME` environment variables and start the server:
```powershell
$env:CATALINA_HOME="C:\tomcat\apache-tomcat-10.1.44-windows-x64\apache-tomcat-10.1.44"
& "C:\tomcat\apache-tomcat-10.1.44-windows-x64\apache-tomcat-10.1.44\bin\catalina.bat" run
```
Open your browser and navigate to:
👉 `http://localhost:8080/CourseExchangePlatform/index.jsp`

---

## 🔒 Security Measures

* **Output Sanitization:** All output strings are sanitized via `com.courseexchange.utils.SecurityUtil.escapeHtml(str)` before rendering in the browser to guarantee protection against Cross-Site Scripting (XSS) attacks.
* **Password Hashing:** Passwords are fully hashed via standard **SHA-256** inside `com.courseexchange.utils.PasswordUtil.java` before they are persisted.
