package com.mycompany.dmariogame;

import java.io.IOException;

import javafx.fxml.FXML;

public class SettingsController {
    @FXML
    private void handleBack() throws IOException {
        App.setRoot("MainMenu");
    }
} 