package com.mycompany.dmariogame;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.mycompany.dmariogame.audio.SoundManager;
import com.mycompany.dmariogame.ui.GamePanel;
import com.mycompany.dmariogame.ui.MainMenuPanel;

public class App {
    private static JFrame frame;
    private static GamePanel gamePanel;
    private static MainMenuPanel menuPanel;
    private static SoundManager soundManager;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Initialize sound manager
            soundManager = SoundManager.getInstance();
            
            frame = new JFrame("2D Mario Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            menuPanel = new MainMenuPanel(
                e -> switchToGame(),
                e -> System.exit(0)
            );
            frame.setContentPane(menuPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void switchToGame() {
        gamePanel = new GamePanel(menuPanel, frame, App::restartGame);
        frame.setContentPane(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        gamePanel.startGame();
        gamePanel.requestFocusInWindow();
        
        // Start background music when game starts
        soundManager.startBackgroundMusic();
    }

    public static void restartGame() {
        switchToGame();
    }
    
    public static SoundManager getSoundManager() {
        return soundManager;
    }
} 