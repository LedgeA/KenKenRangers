module algorangers.kenkenrangers {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens algorangers.kenkenrangers.controllers to javafx.fxml;
    exports algorangers.kenkenrangers;
    exports algorangers.kenkenrangers.controllers;
    
}
