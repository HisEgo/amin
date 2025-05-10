package com.networkgame.model;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class Packet {
    protected Point2D position;
    protected Point2D velocity;
    protected double noise;
    protected double size;
    protected Color color;
    protected boolean isActive;
    protected double speed;
    protected Point2D startPoint;
    protected Point2D endPoint;
    protected double progress;

    public Packet(Point2D startPoint, Point2D endPoint, double size, Color color) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.position = new Point2D.Double(startPoint.getX(), startPoint.getY());
        this.size = size;
        this.color = color;
        this.isActive = true;
        this.progress = 0;
        this.noise = 0;
        calculateVelocity();
    }

    protected void calculateVelocity() {
        double dx = endPoint.getX() - startPoint.getX();
        double dy = endPoint.getY() - startPoint.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        this.velocity = new Point2D.Double(dx / distance * speed, dy / distance * speed);
    }

    public void update() {
        if (!isActive) return;
        
        progress += speed;
        position.setLocation(
            startPoint.getX() + (endPoint.getX() - startPoint.getX()) * progress,
            startPoint.getY() + (endPoint.getY() - startPoint.getY()) * progress
        );

        if (progress >= 1.0) {
            isActive = false;
        }
    }

    public void applyImpact(double impactForce, Point2D impactPoint) {
        double distance = position.distance(impactPoint);
        if (distance < size * 2) {
            noise += impactForce * (1 - distance / (size * 2));
            if (noise > size) {
                isActive = false;
            }
        }
    }

    public void draw(Graphics2D g2d) {
        if (!isActive) return;
        
        g2d.setColor(color);
        g2d.fill(getShape());
    }

    protected abstract Shape getShape();

    // Getters and setters
    public Point2D getPosition() { return position; }
    public double getNoise() { return noise; }
    public boolean isActive() { return isActive; }
    public double getSize() { return size; }
    public Color getColor() { return color; }
} 