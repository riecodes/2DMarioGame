package com.mycompany.dmariogame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Enemy extends Entity {
    private double velocityX = 1.5;
    private static final double WIDTH = 30, HEIGHT = 30;

    public Enemy(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
    }

    public void update() {
        x += velocityX;
    }

    public void reverse() {
        velocityX = -velocityX;
    }

    @Override
    public Shape render() {
        Rectangle rect = new Rectangle(x, y, width, height);
        rect.setFill(Color.ORANGE);
        return rect;
    }
} 