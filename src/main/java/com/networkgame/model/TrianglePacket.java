package com.networkgame.model;

import java.awt.*;
import java.awt.geom.Path2D;

public class TrianglePacket extends Packet {
    public TrianglePacket(Point2D startPoint, Point2D endPoint) {
        super(startPoint, endPoint, 3.0, Color.YELLOW);
        this.speed = 0.02; // Base speed
    }

    @Override
    protected Shape getShape() {
        Path2D triangle = new Path2D.Double();
        double height = size * Math.sqrt(3);
        
        triangle.moveTo(position.getX(), position.getY() - height/2);
        triangle.lineTo(position.getX() - size, position.getY() + height/2);
        triangle.lineTo(position.getX() + size, position.getY() + height/2);
        triangle.closePath();
        
        return triangle;
    }

    @Override
    public void update() {
        super.update();
        // Triangle packets have accelerated movement
        if (progress > 0.5) {
            speed = 0.02 * (1 + (progress - 0.5) * 2);
        }
    }
} 