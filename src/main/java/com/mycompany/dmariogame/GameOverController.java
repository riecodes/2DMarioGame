package com.mycompany.dmariogame;

import java.io.IOException;

import javafx.fxml.FXML;

public class GameOverController {
    @FXML
    private void handleRestart() throws IOException {
        App.setRoot("GameScreen");
    }

    @FXML
    private void handleMenu() throws IOException {
        App.setRoot("MainMenu");
    }
} 