module com.example.chatgpt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.chatgpt to javafx.fxml;
    exports com.example.chatgpt;
}