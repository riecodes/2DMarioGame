package com.mycompany.dmariogame.game;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class Entity {
    protected double x;
    protected double y;
    protected double velocityX;
    protected double velocityY;
    protected int width;
    protected int height;
    protected boolean isActive;
    protected BufferedImage sprite;
    
    public Entity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityX = 0;
        this.velocityY = 0;
        this.isActive = true;
    }
    
    public void update() {
        x += velocityX;
        y += velocityY;
    }
    
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    
    public boolean collidesWith(Entity other) {
        return this.getBounds().intersects(other.getBounds());
    }
    
    // Getters and setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    
    public double getVelocityX() { return velocityX; }
    public void setVelocityX(double velocityX) { this.velocityX = velocityX; }
    
    public double getVelocityY() { return velocityY; }
    public void setVelocityY(double velocityY) { this.velocityY = velocityY; }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public BufferedImage getSprite() { return sprite; }
    public void setSprite(BufferedImage sprite) { this.sprite = sprite; }
    
    public abstract void draw(java.awt.Graphics2D g);
} 