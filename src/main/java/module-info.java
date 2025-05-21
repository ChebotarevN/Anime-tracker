module app.animelist {
    requires javafx.controls;
    requires javafx.fxml;
    requires dotenv;
    requires java.sql;
    requires sqlite.jdbc;


    opens app.animelist to javafx.fxml;
    exports app.animelist;
}