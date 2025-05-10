package com.networkgame.model;

import java.awt.*;
import java.awt.geom.Point2D;

public class ImpactWave {
    private Point2D center;
    private double radius;
    private double maxRadius;
    private double duration;
    private double elapsedTime;
    private static final double MAX_RADIUS = 100.0;

    public ImpactWave(Point2D center, double duration) {
        this.center = center;
        this.radius = 0;
        this.maxRadius = MAX_RADIUS;
        this.duration = duration;
        this.elapsedTime = 0;
    }

    public void update(double deltaTime) {
        elapsedTime += deltaTime;
        radius = (elapsedTime / duration) * maxRadius;
    }

    public void draw(Graphics2D g2d) {
        double alpha = 1.0 - (elapsedTime / duration);
        if (alpha <= 0) return;

        g2d.setColor(new Color(1.0f, 0.0f, 0.0f, (float)alpha));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawOval(
            (int)(center.getX() - radius),
            (int)(center.getY() - radius),
            (int)(radius * 2),
            (int)(radius * 2)
        );
    }

    public boolean isExpired() {
        return elapsedTime >= duration;
    }

    public Point2D getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }
} 