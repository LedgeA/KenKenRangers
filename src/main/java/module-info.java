module algorangers.kenkenrangers {
    requires javafx.controls;
    requires javafx.fxml;


    opens algorangers.kenkenrangers to javafx.fxml;
    exports algorangers.kenkenrangers;
}