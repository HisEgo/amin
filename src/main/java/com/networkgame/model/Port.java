package com.networkgame.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;

public class Port {
    private double x, y;
    private PortType type;
    private boolean isOccupied;
    private static final double SIZE = 10;

    public Port(double x, double y, PortType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.isOccupied = false;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(isOccupied ? Color.RED : Color.BLACK);
        
        if (type == PortType.SQUARE) {
            g2d.fill(new Rectangle2D.Double(x - SIZE/2, y - SIZE/2, SIZE, SIZE));
        } else {
            Path2D triangle = new Path2D.Double();
            triangle.moveTo(x, y - SIZE/2);
            triangle.lineTo(x - SIZE/2, y + SIZE/2);
            triangle.lineTo(x + SIZE/2, y + SIZE/2);
            triangle.closePath();
            g2d.fill(triangle);
        }
    }

    public boolean isCompatible(Packet packet) {
        if (packet instanceof SquarePacket) {
            return type == PortType.SQUARE;
        } else if (packet instanceof TrianglePacket) {
            return type == PortType.TRIANGLE;
        }
        return false;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public PortType getType() {
        return type;
    }
} 