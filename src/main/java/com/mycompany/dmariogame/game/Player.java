package com.mycompany.dmariogame.game;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.mycompany.dmariogame.App;
import com.mycompany.dmariogame.utils.ResourceManager;

public class Player extends Entity {
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    private static final float JUMP_FORCE = -15.0f;
    private static final float MOVE_SPEED = 5.0f;
    private static final float GRAVITY = 0.8f;
    
    private float velocityX;
    private float velocityY;
    private boolean isJumping;
    private boolean isMovingLeft;
    private boolean isMovingRight;
    private boolean facingRight = true;
    private int score;
    private int lives;
    private int levelWidth;
    
    public Player(float x, float y, int levelWidth) {
        super(x, y, WIDTH, HEIGHT);
        this.velocityX = 0;
        this.velocityY = 0;
        this.isJumping = false;
        this.isMovingLeft = false;
        this.isMovingRight = false;
        this.score = 0;
        this.lives = 3;
        this.levelWidth = levelWidth;
    }
    
    @Override
    public void update() {
        velocityY += GRAVITY;
        if (isMovingLeft) {
            velocityX = -MOVE_SPEED;
            facingRight = false;
        } else if (isMovingRight) {
            velocityX = MOVE_SPEED;
            facingRight = true;
        } else {
            velocityX = 0;
        }
        x += velocityX;
        y += velocityY;
        if (x < 0) x = 0;
        if (x > levelWidth - WIDTH) x = levelWidth - WIDTH;
        if (y < 0) y = 0;
        if (y > 600 - HEIGHT) {
            y = 600 - HEIGHT;
            velocityY = 0;
            isJumping = false;
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (facingRight) {
            g.drawImage(ResourceManager.getSprite(ResourceManager.PLAYER), (int)x, (int)y, WIDTH, HEIGHT, null);
        } else {
            g.drawImage(ResourceManager.getSprite(ResourceManager.PLAYER), (int)x + WIDTH, (int)y, -WIDTH, HEIGHT, null);
        }
    }
    
    public void jump() {
        if (!isJumping) {
            velocityY = JUMP_FORCE;
            isJumping = true;
            App.getSoundManager().playSound("jump.wav");
        }
    }
    public void setMovingLeft(boolean moving) { isMovingLeft = moving; }
    public void setMovingRight(boolean moving) { isMovingRight = moving; }
    @Override
    public Rectangle getBounds() { return new Rectangle((int)x, (int)y, WIDTH, HEIGHT); }
    public void addScore(int points) { score += points; }
    public void loseLife() { lives--; if (lives <= 0) setActive(false); }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public void setJumping(boolean jumping) { this.isJumping = jumping; }
    @Override
    public double getVelocityY() { return velocityY; }
    @Override
    public void setVelocityY(double vy) { this.velocityY = (float) vy; }
    public boolean isJumping() { return isJumping; }
    public void reset() {
        lives = 3;
        score = 0;
        isJumping = false;
        isMovingLeft = false;
        isMovingRight = false;
        facingRight = true;
        velocityX = 0;
        velocityY = 0;
        setActive(true);
    }
} 