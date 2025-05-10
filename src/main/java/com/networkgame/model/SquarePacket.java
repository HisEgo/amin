package com.networkgame.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class SquarePacket extends Packet {
    public SquarePacket(Point2D startPoint, Point2D endPoint) {
        super(startPoint, endPoint, 2.0, Color.RED);
        this.speed = 0.02; // Base speed
    }

    @Override
    protected Shape getShape() {
        return new Rectangle2D.Double(
            position.getX() - size,
            position.getY() - size,
            size * 2,
            size * 2
        );
    }

    @Override
    public void update() {
        super.update();
        // Square packets move at constant speed
    }
} 