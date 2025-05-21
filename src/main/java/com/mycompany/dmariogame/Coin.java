package com.mycompany.dmariogame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class Coin extends Entity {
    public Coin(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2);
    }

    @Override
    public Shape render() {
        Circle circle = new Circle(x + width / 2, y + height / 2, width / 2);
        circle.setFill(Color.GOLD);
        return circle;
    }
} 