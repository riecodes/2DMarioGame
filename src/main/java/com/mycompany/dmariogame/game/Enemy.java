package com.mycompany.dmariogame.game;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.mycompany.dmariogame.utils.ResourceManager;

public class Enemy extends Entity {
    private static final int ENEMY_SIZE = 32;
    private static final float MOVE_SPEED = 2.0f;
    private static final float GRAVITY = 0.8f;
    
    private float velocityX;
    private float velocityY;
    private boolean isStomped;
    private boolean isMovingRight;
    private float patrolLeft;
    private float patrolRight;
    private boolean hasPatrol;
    private boolean facingRight = true;
    
    public Enemy(float x, float y) {
        super(x, y, ENEMY_SIZE, ENEMY_SIZE);
        this.velocityX = MOVE_SPEED;
        this.velocityY = 0;
        this.isStomped = false;
        this.isMovingRight = true;
        this.hasPatrol = false;
        this.facingRight = true;
    }
    
    public Enemy(float x, float y, float patrolLeft, float patrolRight) {
        this(x, y);
        this.patrolLeft = patrolLeft;
        this.patrolRight = patrolRight;
        this.hasPatrol = true;
    }
    
    @Override
    public void update() {
        if (!isStomped) {
            // Apply gravity
            velocityY += GRAVITY;
            
            // Update position
            x += velocityX;
            y += velocityY;
            
            // Keep enemy in bounds
            if (hasPatrol) {
                if (x < patrolLeft) {
                    x = patrolLeft;
                    velocityX = MOVE_SPEED;
                    isMovingRight = true;
                    facingRight = true;
                } else if (x > patrolRight) {
                    x = patrolRight;
                    velocityX = -MOVE_SPEED;
                    isMovingRight = false;
                    facingRight = false;
                }
            } else {
                if (x < 0) {
                    x = 0;
                    velocityX = MOVE_SPEED;
                    isMovingRight = true;
                    facingRight = true;
                } else if (x > 800 - ENEMY_SIZE) {
                    x = 800 - ENEMY_SIZE;
                    velocityX = -MOVE_SPEED;
                    isMovingRight = false;
                    facingRight = false;
                }
            }
            
            // Prevent falling through the bottom
            if (y > 600 - ENEMY_SIZE) {
                y = 600 - ENEMY_SIZE;
                velocityY = 0;
            }
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (isStomped) {
            if (facingRight) {
                g.drawImage(ResourceManager.getSprite(ResourceManager.ENEMY_STOMPED), (int)x, (int)y, width, height, null);
            } else {
                g.drawImage(ResourceManager.getSprite(ResourceManager.ENEMY_STOMPED), (int)x + width, (int)y, -width, height, null);
            }
        } else {
            if (facingRight) {
                g.drawImage(ResourceManager.getSprite(ResourceManager.ENEMY), (int)x, (int)y, width, height, null);
            } else {
                g.drawImage(ResourceManager.getSprite(ResourceManager.ENEMY), (int)x + width, (int)y, -width, height, null);
            }
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    
    public void stomp() {
        isStomped = true;
        velocityX = 0;
        velocityY = 0;
    }
    
    public boolean isStomped() {
        return isStomped;
    }
    
    public boolean isMovingRight() {
        return isMovingRight;
    }
    
    public void setMovingRight(boolean moving) {
        isMovingRight = moving;
        velocityX = moving ? MOVE_SPEED : -MOVE_SPEED;
    }
    
    @Override
    public double getVelocityY() { return velocityY; }
    @Override
    public void setVelocityY(double vy) { this.velocityY = (float) vy; }
} 