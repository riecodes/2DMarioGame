package com.mycompany.dmariogame.game;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.mycompany.dmariogame.utils.ResourceManager;

public class Flag extends Entity {
    private static final int FLAG_WIDTH = 32;
    private static final int FLAG_HEIGHT = 96;
    private boolean isReached;
    
    public Flag(float x, float y) {
        super(x, y, FLAG_WIDTH, FLAG_HEIGHT);
        this.isReached = false;
    }
    
    @Override
    public void update() {
        // Flag doesn't need to update unless we add animation
    }
    
    @Override
    public void draw(Graphics2D g) {
        g.drawImage(ResourceManager.getSprite(ResourceManager.FLAG), 
                   (int)x, (int)y, width, height, null);
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    
    public void reach() {
        isReached = true;
    }
    
    public boolean isReached() {
        return isReached;
    }
} 