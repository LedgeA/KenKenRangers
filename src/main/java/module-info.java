module algorangers.kenkenrangers {
    requires javafx.controls;
    requires javafx.fxml;


    opens algorangers.kenkenrangers.controllers to javafx.fxml;
    exports algorangers.kenkenrangers;
    
}
