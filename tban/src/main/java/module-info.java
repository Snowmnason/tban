module sora.tban {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires java.base;

    opens sora.tban to javafx.fxml, com.google.gson;
    exports sora.tban;
}
