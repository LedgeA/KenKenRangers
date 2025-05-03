package algorangers.kenkenrangers.controllers.menu;

import algorangers.kenkenrangers.database.DatabaseManager;
import algorangers.kenkenrangers.utils.GameUtils;
import algorangers.kenkenrangers.utils.SoundUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class ConfigureTrialController {
    
    @FXML
    private Pane p_main;

    @FXML
    private Text t_dimensions, t_dot, t_powerSurge, t_invincibility, t_cellReveal;

    @FXML
    private Button b_dimensionsUp, b_dotUp, b_powerSurgeUp, b_invincibilityUp, b_cellRevealUp;

    @FXML
    private Button b_dimensionsDown, b_dotDown, b_powerSurgeDown, b_invincibilityDown, b_cellRevealDown;

    @FXML
    private Text t_start, t_goBack;

    int dimensions = 3, dot = 4, powerSurge = 3, invincibility = 3, cellReveal = 3;
    
    @FXML   
    public void initialize() {
        setupSpinnerButtons();
        setupMenuButtons();
    }
    
    private void setupMenuButtons() {
        GameUtils.setMenuTextAppearance(t_start);
        GameUtils.setMenuTextAppearance(t_goBack);

        t_start.setOnMouseClicked(event -> {
            DatabaseManager.updateInitialGameSession(
                dimensions, 
                dot, 
                "custom_trial", 
                powerSurge, 
                invincibility, 
                cellReveal);
            SoundUtils.playPress();
            GameUtils.navigate("custom-trial.fxml", p_main);

        });
        

        t_goBack.setOnMouseClicked(event -> {
            SoundUtils.playPress();
            GameUtils.navigate("main-menu.fxml", p_main);
        });
    }

    private void setupSpinnerButtons() {
        setupButton(b_dimensionsUp, () -> ++dimensions, t_dimensions, 6);
        setupButton(b_dimensionsDown, () -> --dimensions, t_dimensions, 3);
    
        setupButton(b_dotUp, () -> ++dot, t_dot, 10);
        setupButton(b_dotDown, () -> --dot, t_dot, 3);
    
        setupButton(b_powerSurgeUp, () -> ++powerSurge, t_powerSurge, 9);
        setupButton(b_powerSurgeDown, () -> --powerSurge, t_powerSurge, 0);
    
        setupButton(b_invincibilityUp, () -> ++invincibility, t_invincibility, 9);
        setupButton(b_invincibilityDown, () -> --invincibility, t_invincibility, 0);
    
        setupButton(b_cellRevealUp, () -> ++cellReveal, t_cellReveal, 9);
        setupButton(b_cellRevealDown, () -> --cellReveal, t_cellReveal, 0);
    }
    
    private void setupButton(Button button, Runnable updater, Text targetText, int limit) {
        button.setOnAction(e -> {
            if (getTextAsNum(targetText) == limit) return;
            SoundUtils.playPress();
            
            updater.run();
            targetText.setText(getValueForText(targetText));
        });
    }

    private int getTextAsNum(Text text) {
        String strText = text.getText();
        String value = strText.substring(strText.length() - 1);

        return Integer.parseInt(value);
    }
    
    private String getValueForText(Text text) {
        return switch (text.getId()) {
            case "t_dimensions" -> dimensions + "×" + dimensions;
            case "t_dot" -> "×" + dot;
            case "t_powerSurge" -> "×" + powerSurge;
            case "t_invincibility" -> "×" + invincibility;
            case "t_cellReveal" -> "×" + cellReveal;
            default -> "";
        };
    }
    

}
