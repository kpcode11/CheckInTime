# StudentManager — Backend/Frontend Refactor Plan & Prompt

> **How to use this file**
> Feed the entire contents of this file to Claude Code (or any AI coding assistant)
> alongside your project. It will execute the refactor step by step.
> Run `./mvnw clean compile` after each phase to verify nothing is broken.

---

## The Problem: Current Architecture

All Java source lives in one flat package with no separation of concerns:

```
com.example.studentmanager/
  AttendanceController.java   ← talks directly to DB
  DashboardController.java    ← talks directly to DB
  DatabaseConnection.java     ← raw JDBC, used everywhere
  LoginController.java        ← auth logic + UI mixed
  LoginApplication.java
  NotificationManager.java
  SessionManager.java
  ShowTasksController.java    ← queries + UI mixed
  SignUpController.java       ← auth logic + UI mixed
  Subject.java                ← model mixed with app code
  SubjectsController.java     ← queries + UI mixed
  Task.java                   ← model mixed with app code
  TaskManagerController.java  ← scheduling + UI mixed
```

**Key issues:**
- Controllers query the database directly — no service or repository layer
- Business logic (password hashing, reminder scheduling, percentage calculations) lives inside UI controllers
- `Subject.java` and `Task.java` are models but sit alongside controllers
- No password hashing (plaintext passwords in DB)
- Hard to test anything — no seams between layers

---

## Target Architecture

```
com.example.studentmanager/
├── StudentManagerApp.java          # entry point (rename LoginApplication)
│
├── config/
│   └── DatabaseConfig.java         # connection pool / JDBC helper
│
├── model/                          # pure POJOs, no logic, no imports of JavaFX
│   ├── User.java
│   ├── Subject.java
│   ├── Task.java
│   ├── AttendanceRecord.java
│   └── UserSubject.java
│
├── repository/                     # all SQL lives here, returns model objects
│   ├── UserRepository.java
│   ├── SubjectRepository.java
│   ├── TaskRepository.java
│   └── AttendanceRepository.java
│
├── service/                        # business rules, no JavaFX imports
│   ├── AuthService.java
│   ├── SubjectService.java
│   ├── TaskService.java
│   └── AttendanceService.java
│
├── controller/                     # JavaFX only — calls services, never SQL
│   ├── LoginController.java
│   ├── SignUpController.java
│   ├── DashboardController.java
│   ├── SubjectsController.java
│   ├── TaskManagerController.java
│   ├── ShowTasksController.java
│   └── AttendanceController.java
│
└── util/
    ├── SessionManager.java         # unchanged, move to util
    ├── NotificationManager.java    # move to util
    ├── PasswordUtil.java           # NEW: BCrypt wrapper
    └── FxUtils.java                # NEW: shared scene-switching helper
```

---

## Refactor Prompt (give this to your AI coding assistant)

---

### CONTEXT

You are refactoring a JavaFX + Maven + MySQL desktop app called **StudentManager**.
The full source is in `src/main/java/com/example/studentmanager/`.
The goal is to **reorganise the existing code** into a clean layered architecture
without changing any visible behaviour or breaking the build.
Do NOT rewrite business logic — only move it to the correct layer.
After every phase, the project must compile with `./mvnw clean compile`.

---

### PHASE 1 — Create the package skeleton

Create the following empty packages (just directories; Java will see them once
files are placed there):

```
src/main/java/com/example/studentmanager/config/
src/main/java/com/example/studentmanager/model/
src/main/java/com/example/studentmanager/repository/
src/main/java/com/example/studentmanager/service/
src/main/java/com/example/studentmanager/controller/
src/main/java/com/example/studentmanager/util/
```

---

### PHASE 2 — Move and fix models

Move `Subject.java` and `Task.java` to the `model/` package.
Create two new model classes in `model/`:

**`model/User.java`**
```java
package com.example.studentmanager.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    // standard getters/setters
}
```

**`model/AttendanceRecord.java`**
```java
package com.example.studentmanager.model;

import java.time.LocalDate;

public class AttendanceRecord {
    private int id;
    private String username;
    private String subjectName;
    private boolean attended;
    private LocalDate date;
    // standard getters/setters
}
```

**`model/UserSubject.java`**
```java
package com.example.studentmanager.model;

public class UserSubject {
    private int id;
    private String username;
    private String subjectName;
    private int minPercentage;
    // standard getters/setters
}
```

Update `package` declarations and any imports in existing controllers to point
to `com.example.studentmanager.model.Subject` and
`com.example.studentmanager.model.Task`.

---

### PHASE 3 — Centralise database config

Move `DatabaseConnection.java` to `config/DatabaseConfig.java`.
Rename the class to `DatabaseConfig`.

The class must:
- Read `DB_URL`, `DB_USER`, `DB_PASS` from environment variables (already done).
- Expose **one** method: `public static Connection getConnection() throws SQLException`.
- Use a simple connection-per-call pattern (existing behaviour) — do not
  introduce a connection pool in this phase.

Update every `import` across the project to use the new class name and package.

---

### PHASE 4 — Extract repositories

For each domain, create a repository class in the `repository/` package.
Each repository:
- Receives a `Connection` via `DatabaseConfig.getConnection()` (called inside
  each method, not stored as a field).
- Returns model objects (`User`, `Subject`, `Task`, etc.).
- Contains **only SQL** — no business rules, no password hashing, no calculations.

**`repository/UserRepository.java`** — methods:
```java
public Optional<User> findByUsername(String username)
public boolean existsByUsername(String username)
public void save(User user)            // INSERT
```

**`repository/SubjectRepository.java`** — methods:
```java
public List<Subject> findByUsername(String username)
public void save(Subject subject)
public void deleteById(int id)
public void updateMarks(int id, int marks)
```

**`repository/TaskRepository.java`** — methods:
```java
public List<Task> findByUsername(String username)
public void save(Task task)
public void deleteById(int id)
public List<Task> findUpcomingReminders()   // tasks where reminder_time <= now+5min
```

**`repository/AttendanceRepository.java`** — methods:
```java
public List<AttendanceRecord> findByUsername(String username)
public List<UserSubject> findSubjectsByUsername(String username)
public void saveRecord(AttendanceRecord record)
public void saveSubject(UserSubject subject)
public void deleteSubjectById(int id)
```

Migrate the relevant SQL from the existing controllers into these repository
methods. Leave the controllers temporarily broken — Phase 5 will fix them.

---

### PHASE 5 — Extract services

Create one service per domain in the `service/` package.
Services:
- Are plain Java classes (no JavaFX imports).
- Hold business logic extracted from controllers.
- Call repositories; never call JDBC directly.
- Are instantiated with `new` inside controllers (no DI framework needed).

**`service/AuthService.java`**
```java
public class AuthService {
    private final UserRepository userRepo = new UserRepository();

    /** Returns true and sets SessionManager if credentials are valid. */
    public boolean login(String username, String password)

    /** Throws IllegalArgumentException if username taken. Hashes password. */
    public void register(String username, String password)
}
```
- Move login validation logic from `LoginController` here.
- Move sign-up logic from `SignUpController` here.
- **Add password hashing**: use `PasswordUtil.hash(password)` on registration
  and `PasswordUtil.verify(raw, hash)` on login (see Phase 6).

**`service/SubjectService.java`**
```java
public class SubjectService {
    private final SubjectRepository repo = new SubjectRepository();

    public List<Subject> getSubjectsForUser(String username)
    public void addSubject(Subject subject)
    public void removeSubject(int id)
    public double calculateTotalPercentage(List<Subject> subjects)  // existing formula
}
```

**`service/TaskService.java`**
```java
public class TaskService {
    private final TaskRepository repo = new TaskRepository();

    public List<Task> getTasksForUser(String username)
    public void addTask(Task task)
    public void removeTask(int id)
    public List<Task> getUpcomingReminders()
}
```

**`service/AttendanceService.java`**
```java
public class AttendanceService {
    private final AttendanceRepository repo = new AttendanceRepository();

    public List<UserSubject> getSubjectsForUser(String username)
    public void addSubject(UserSubject subject)
    public void removeSubject(int id)
    public void recordAttendance(AttendanceRecord record)
    public double calculateAttendancePercentage(String username, String subjectName)
}
```

---

### PHASE 6 — Add PasswordUtil and FxUtils

**`util/PasswordUtil.java`**

Add `org.mindrot:jbcrypt:0.4` to `pom.xml` dependencies, then:

```java
package com.example.studentmanager.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {}

    public static String hash(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt(12));
    }

    public static boolean verify(String plaintext, String hash) {
        // Guard: if stored value is plaintext (legacy), fall back to direct compare
        if (!hash.startsWith("$2")) {
            return plaintext.equals(hash);
        }
        return BCrypt.checkpw(plaintext, hash);
    }
}
```

> The `verify` fallback ensures existing plain-text users can still log in;
> their password will be re-hashed on next save if you choose to add that later.

**`util/FxUtils.java`** — shared scene-switching (DRY up repeated code in controllers):

```java
package com.example.studentmanager.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class FxUtils {
    private FxUtils() {}

    public static void switchScene(Stage stage, String fxmlPath, String title)
            throws Exception {
        Parent root = FXMLLoader.load(
            FxUtils.class.getResource(fxmlPath));
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}
```

Move `SessionManager.java` and `NotificationManager.java` to the `util/`
package and update their `package` declarations and all imports.

---

### PHASE 7 — Clean up controllers

Each controller must now:
1. Contain **only** JavaFX `@FXML` fields, event handlers, and `initialize()`.
2. Call service methods for all logic — never touch JDBC or SQL directly.
3. Use `FxUtils.switchScene(...)` for navigation.
4. Show errors via `Alert` dialogs, not `System.out.println`.

Apply these changes to every controller:

| Controller | Service to inject |
|---|---|
| `LoginController` | `AuthService` |
| `SignUpController` | `AuthService` |
| `DashboardController` | `SubjectService` |
| `SubjectsController` | `SubjectService` |
| `TaskManagerController` | `TaskService` |
| `ShowTasksController` | `TaskService` |
| `AttendanceController` | `AttendanceService` |

Pattern for each controller:
```java
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        if (authService.login(username, password)) {
            try {
                FxUtils.switchScene(
                    (Stage) usernameField.getScene().getWindow(),
                    "/com/example/studentmanager/Dashboard.fxml",
                    "Dashboard"
                );
            } catch (Exception e) {
                errorLabel.setText("Failed to load dashboard.");
            }
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }
}
```

---

### PHASE 8 — Update module-info.java

Open `module-info.java` and ensure it reflects the new sub-packages:

```java
module com.example.studentmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.mindrot.jbcrypt;       // add this

    opens com.example.studentmanager.controller to javafx.fxml;
    exports com.example.studentmanager;
}
```

---

### PHASE 9 — Final verification checklist

Run each item and confirm it passes:

- [ ] `./mvnw clean compile` — zero errors
- [ ] `./mvnw javafx:run` — app launches, login works
- [ ] Sign up a new user — password stored as BCrypt hash (`$2a$...`) in DB
- [ ] Log in with that user — works correctly
- [ ] Old plain-text users — still log in (legacy fallback in `PasswordUtil`)
- [ ] Subjects: add, view, delete
- [ ] Tasks: add, view, delete, reminder fires
- [ ] Attendance: add subject, mark present/absent, percentage correct
- [ ] No `System.out.println` left in controller layer (use `Alert` or a logger)

---

### CONSTRAINTS (do not violate)

- Do **not** change any `.fxml` files or `style.css`.
- Do **not** introduce Spring, Guice, or any DI framework.
- Do **not** change the database schema.
- Do **not** change Maven group/artifact IDs.
- Keep `SessionManager` as a singleton — just move it to `util/`.
- `NotificationManager` background thread logic stays intact — just move it.
- Each phase must leave the project in a compilable state before starting the next.

---

## Summary of New Rules (for future development)

| Layer | Can import | Cannot import |
|---|---|---|
| `model/` | `java.*` only | JavaFX, JDBC, services |
| `repository/` | `model/`, `config/`, `java.sql` | JavaFX, services |
| `service/` | `model/`, `repository/`, `util/` | JavaFX, JDBC |
| `controller/` | `service/`, `model/`, `util/`, JavaFX | JDBC, repositories directly |
| `util/` | `java.*`, JavaFX (for FxUtils only) | services, repositories |

This enforces a strict **downward-only dependency flow** and makes every layer
independently testable.
