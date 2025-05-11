package com.networkgame.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

public class SquarePacket extends Packet {
    public SquarePacket(double x, double y, Port targetPort) {
        super(x, y, targetPort);
        this.size = 2.0;
        this.color = Color.RED;
        this.speed = 0.02; // Base speed
    }

    @Override
    protected Shape getShape() {
        return new Rectangle2D.Double(
            x - size,
            y - size,
            size * 2,
            size * 2
        );
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        // Square packets move at constant speed
    }

    @Override
    public int getReward() {
        return 1; // Square packets give 1 coin
    }
} 