package com.mycompany.dmariogame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Flag extends Entity {
    public Flag(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public Shape render() {
        Rectangle rect = new Rectangle(x, y, width, height);
        rect.setFill(Color.LIMEGREEN);
        return rect;
    }
} 