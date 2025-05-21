package com.mycompany.dmariogame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Player extends Entity {
    private double velocityX = 0;
    private double velocityY = 0;
    private boolean onGround = false;
    private static final double WIDTH = 30, HEIGHT = 40;

    public Player(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
    }

    public void move(double dx) {
        this.x += dx;
    }

    public void jump() {
        if (onGround) {
            velocityY = -12;
            onGround = false;
        }
    }

    public void setOnGround(boolean value) { onGround = value; }
    public double getVelocityY() { return velocityY; }
    public void setVelocityY(double v) { velocityY = v; }

    @Override
    public Shape render() {
        Rectangle rect = new Rectangle(x, y, width, height);
        rect.setFill(Color.RED);
        return rect;
    }
} 