module app.animelist {
    requires javafx.controls;
    requires javafx.fxml;


    opens app.animelist to javafx.fxml;
    exports app.animelist;
}