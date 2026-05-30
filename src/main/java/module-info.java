module com.example.studentmanager {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires java.desktop;
    requires jbcrypt; // jbcrypt 0.4 automatic module name is often jbcrypt

    opens com.example.studentmanager.controller to javafx.fxml;
    opens com.example.studentmanager to javafx.fxml;
    exports com.example.studentmanager;
}