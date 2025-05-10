package com.networkgame.model;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Connection {
    private Port startPort;
    private Port endPort;
    private Line2D line;
    private boolean isActive;
    private Color color;

    public Connection(Port startPort, Port endPort) {
        this.startPort = startPort;
        this.endPort = endPort;
        this.isActive = true;
        this.color = Color.BLACK;
        updateLine();
    }

    private void updateLine() {
        line = new Line2D.Double(
            startPort.getX(),
            startPort.getY(),
            endPort.getX(),
            endPort.getY()
        );
    }

    public void draw(Graphics2D g2d) {
        if (!isActive) return;
        
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(line);
    }

    public double getLength() {
        return line.getP1().distance(line.getP2());
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Port getStartPort() {
        return startPort;
    }

    public Port getEndPort() {
        return endPort;
    }

    public Line2D getLine() {
        return line;
    }
} 