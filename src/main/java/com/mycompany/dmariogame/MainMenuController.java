package com.mycompany.dmariogame;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainMenuController {
    @FXML
    private void handleStartGame() throws IOException {
        App.setRoot("GameScreen");
    }

    @FXML
    private void handleSettings() throws IOException {
        App.setRoot("Settings");
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }
} 