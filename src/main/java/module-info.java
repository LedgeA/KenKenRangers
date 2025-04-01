module algorangers.kenkenrangers {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens algorangers.kenkenrangers.controllers to javafx.fxml;
    exports algorangers.kenkenrangers;
    
}
