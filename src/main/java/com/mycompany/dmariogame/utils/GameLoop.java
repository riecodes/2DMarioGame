package com.mycompany.dmariogame.utils;

import java.awt.event.ActionListener;

import javax.swing.Timer;

public class GameLoop {
    private static final int TARGET_FPS = 60;
    private static final int FRAME_TIME = 1000 / TARGET_FPS;
    
    private Timer gameTimer;
    private boolean isRunning;
    
    public GameLoop(ActionListener gameUpdate) {
        gameTimer = new Timer(FRAME_TIME, gameUpdate);
        isRunning = false;
    }
    
    public void start() {
        if (!isRunning) {
            isRunning = true;
            gameTimer.start();
        }
    }
    
    public void stop() {
        if (isRunning) {
            isRunning = false;
            gameTimer.stop();
        }
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public void setFPS(int fps) {
        if (fps > 0) {
            gameTimer.setDelay(1000 / fps);
        }
    }
} 