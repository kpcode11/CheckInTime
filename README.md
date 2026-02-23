# StudentManager

A JavaFX desktop application for tracking student subjects, tasks, and attendance.
Originally developed as a personal education tool, the project provides user
authentication, performance visualization, task management with reminders, and
attendance logging. This repository is built with Maven and targets JDK 25.

---

## Repository Structure

```
Root Directory/                      # workspace root
├─ mvnw*                       # Maven wrapper
├─ pom.xml                     # Maven build descriptor
├─ src/
│   ├─ main/
│   │   ├─ java/com/example/studentmanager/  # application code
│   │   │   AttendanceController.java
│   │   │   DashboardController.java
│   │   │   DatabaseConnection.java
│   │   │   LoginApplication.java
│   │   │   LoginController.java
│   │   │   NotificationManager.java
│   │   │   SessionManager.java
│   │   │   ShowTasksController.java
│   │   │   SignUpController.java
│   │   │   Subject.java
│   │   │   SubjectsController.java
│   │   │   Task.java
│   │   │   TaskManagerController.java
│   │   └─ resources/com/example/studentmanager/  # FXML views
│   │       AttendanceApp.fxml
│   │       Dashboard.fxml
│   │       Login.fxml
│   │       ShowTasks.fxml
│   │       SignUp.fxml
│   │       Subjects.fxml
│   │       TaskManager.fxml
│   │   └─ css/style.css
│   │   └─ Images/
└─ module-info.java            # Java module descriptor
```

---

## Features

- **User authentication**: sign-up and login with MySQL-backed credentials.
- **Dashboard**: graphical overview of subjects and marks.
- **Subjects management**: add subjects, record marks, compute totals & percentage.
- **Task manager**: schedule tasks, set reminders, categorize, assign priorities.
- **Reminder system**: background scheduler triggers JavaFX alerts for upcoming
  tasks.
- **Attendance tracker**: define subjects, mark present/absent, and calculate
  attendance percentages.

---

## Prerequisites

- Java Development Kit (JDK) 25 or later
- Apache Maven 3.9+ (project includes `mvnw` wrapper)
- MySQL Server (or compatible JDBC database)

---

## Database Setup

The application expects a local MySQL database named `userdb`. Example DDL:

```sql
CREATE DATABASE userdb;
USE userdb;

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL
);

CREATE TABLE subjects (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  subject_name VARCHAR(100) NOT NULL,
  marks INT NOT NULL
);

CREATE TABLE tasks (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  task_name VARCHAR(255) NOT NULL,
  category VARCHAR(50),
  task_date DATE,
  task_time VARCHAR(10),
  reminder VARCHAR(50),
  priority VARCHAR(20),
  reminder_time DATETIME
);

CREATE TABLE user_subjects (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  subject_name VARCHAR(100),
  min_percentage INT
);

CREATE TABLE attendance_records (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  subject_name VARCHAR(100),
  attended BOOLEAN,
  date DATE
);
```

Configure database credentials using environment variables rather than hard-coding them.
By default the application reads the following variables:

```text
DB_URL   (e.g. jdbc:mysql://localhost:3306/userdb)
DB_USER  (database username, default "root")
DB_PASS  (database password)
```

You can set them in your shell prior to running, or create a `.env` file in the project root and load it via your preferred mechanism (e.g. using [dotenv-java](https://github.com/cdimascio/dotenv-java) or manually).  The `.gitignore` already excludes `.env` so your secrets stay out of version control.

Credentials and URL are read from environment variables (`DB_URL`, `DB_USER`, `DB_PASS`).

A sample file `.env.sample` is provided; copy it to `.env` and fill in your values. The `.env` file is ignored by Git.

> **Important:** if these variables are not set (or `DB_PASS` is empty), the application will attempt to connect with no password and MySQL will deny access.  You will see an "Access denied for user 'root'@'localhost' (using password: NO)" error at startup or login.  Make sure to set the variables before running (e.g. `setx DB_PASS "yourpwd"` on Windows, or `export DB_PASS=yourpwd` on macOS/Linux) or copy `.env.sample` and edit.

Update the connection string in `DatabaseConnection.java` only if you want to override these defaults.

---

## Building & Running

### Troubleshooting

* **Access denied using password: NO** – means DB_PASS was not provided. See above for setting environment variables.
* **Password hashing not working?** Ensure you sign up new users; existing rows remain plain text.
* **UI fails to start** – check terminal output for FXML or JDBC errors.


Using the system Maven:

```bash
mvn clean compile          # compile sources
mvn javafx:run             # launch the JavaFX application
```

Or using the wrapper (no installation required):

```bash
./mvnw clean javafx:run
```

---

## Dependencies

Managed in `pom.xml`:

- `org.openjfx:javafx-controls` & `javafx-fxml` 17.0.6
- `com.mysql:mysql-connector-j` 8.0.33 (JDBC driver)
- JUnit Jupiter 5.10 for tests (scope=test)

Compiler plugin is set to `<release>25</release>`.

---

## Module Information

`module com.example.studentmanager` exports the application package and
requires transitive JavaFX and JDBC modules. The module system ensures clean
encapsulation and easier packaging.

---

## Notes & Recommendations

- Passwords are currently stored in plaintext; add hashing (BCrypt, Argon2).
- Consider environment variables or a properties file for configuration.
- Add unit/integration tests for controllers and database helpers.
- Update FXML version or upgrade JavaFX to eliminate runtime warnings.
- Packaging into a native image (jlink or jpackage) can simplify distribution.

---

## License & Contribution

This repository is intended for demonstration and learning purposes. Feel free
to fork and adapt. Contributions and improvements are welcome via pull
requests.

---

*StudentManager* © 2026. All rights reserved.