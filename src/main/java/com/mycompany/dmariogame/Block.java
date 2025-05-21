package com.mycompany.dmariogame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Block extends Entity {
    public Block(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public Shape render() {
        Rectangle rect = new Rectangle(x, y, width, height);
        rect.setFill(Color.SADDLEBROWN);
        return rect;
    }
} 