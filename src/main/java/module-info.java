module app.animelist {
    requires javafx.controls;
    requires javafx.fxml;
    requires dotenv;


    opens app.animelist to javafx.fxml;
    exports app.animelist;
}