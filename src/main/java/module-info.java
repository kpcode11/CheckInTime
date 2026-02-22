module com.example.studentmanager {
    // transitively expose libraries that appear in exported APIs
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;   // Stage is used in public classes
    requires transitive java.sql;          // Connection appears in exported types
    requires java.desktop;

    opens com.example.studentmanager to javafx.fxml;
    exports com.example.studentmanager;
}