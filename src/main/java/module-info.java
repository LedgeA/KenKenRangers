module algorangers.kenkenrangers {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires transitive java.sql;


    opens algorangers.kenkenrangers.controllers to javafx.fxml;
    opens algorangers.kenkenrangers.controllers.base to javafx.fxml;
    opens algorangers.kenkenrangers.controllers.menu to javafx.fxml;
    exports algorangers.kenkenrangers;
    exports algorangers.kenkenrangers.controllers;
    
}
