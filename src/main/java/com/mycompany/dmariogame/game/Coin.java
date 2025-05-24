package com.mycompany.dmariogame.game;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.mycompany.dmariogame.App;
import com.mycompany.dmariogame.utils.ResourceManager;

public class Coin extends Entity {
    private static final int COIN_SIZE = 32;
    private static final int COIN_VALUE = 100;
    private boolean collected;
    
    public Coin(float x, float y) {
        super(x, y, COIN_SIZE, COIN_SIZE);
        this.collected = false;
    }
    
    @Override
    public void update() {
        // Coins don't need to update unless we add animation
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (!collected) {
            g.drawImage(ResourceManager.getSprite(ResourceManager.COIN), 
                       (int)x, (int)y, width, height, null);
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    
    public void collect() {
        collected = true;
        setActive(false);
        App.getSoundManager().playSound("collect.wav");
    }
    
    public boolean isCollected() {
        return collected;
    }
    
    public int getValue() {
        return COIN_VALUE;
    }
} 