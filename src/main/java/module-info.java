module algorangers.kenkenrangers {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires transitive java.sql;


    opens algorangers.kenkenrangers.controllers to javafx.fxml;
    opens algorangers.kenkenrangers.controllers.base to javafx.fxml;
    opens algorangers.kenkenrangers.controllers.menu to javafx.fxml;
    opens algorangers.kenkenrangers.controllers.game_modes to javafx.fxml;
    opens algorangers.kenkenrangers.controllers.game_modes.rangers_saga to javafx.fxml;
    
    exports algorangers.kenkenrangers;
    exports algorangers.kenkenrangers.controllers;
    
}
