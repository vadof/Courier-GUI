module com.example.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.gui to javafx.fxml;
    exports com.example.gui;
    exports com.example.gui.admin;
    opens com.example.gui.admin to javafx.fxml;
    exports com.example.gui.courier;
    opens com.example.gui.courier to javafx.fxml;
}