package com.networkgame.model;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class TrianglePacket extends Packet {
    public TrianglePacket(double x, double y, Port targetPort) {
        super(x, y, targetPort);
        this.size = 3.0;
        this.color = Color.YELLOW;
        this.speed = 0.02; // Base speed
    }

    @Override
    protected Shape getShape() {
        Path2D triangle = new Path2D.Double();
        double height = size * Math.sqrt(3);
        
        triangle.moveTo(x, y - height/2);
        triangle.lineTo(x - size, y + height/2);
        triangle.lineTo(x + size, y + height/2);
        triangle.closePath();
        
        return triangle;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        // Triangle packets have accelerated movement
        if (speed > 0.5) {
            speed = 0.02 * (1 + (speed - 0.5) * 2);
        }
    }

    @Override
    public int getReward() {
        return 2; // Triangle packets give 2 coins
    }
} 