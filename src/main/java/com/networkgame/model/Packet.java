package com.networkgame.model;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class Packet {
    protected double x, y;
    protected double targetX, targetY;
    protected double speed;
    protected double maxSpeed;
    protected double acceleration;
    protected double deceleration;
    protected double noise;
    protected Port targetPort;
    protected boolean isMoving;
    protected static final double IMPACT_THRESHOLD = 0.5;
    protected static final double NOISE_THRESHOLD = 1.0;
    protected static final double IMPACT_RADIUS = 50.0;
    protected static final double IMPACT_FORCE = 0.5;

    public Packet(double x, double y, Port targetPort) {
        this.x = x;
        this.y = y;
        this.targetPort = targetPort;
        this.targetX = targetPort.getX();
        this.targetY = targetPort.getY();
        this.noise = 0;
        this.isMoving = true;
        this.speed = 0;
        this.maxSpeed = 2.0;
        this.acceleration = 0.1;
        this.deceleration = 0.05;
    }

    public void update(double deltaTime) {
        if (!isMoving) return;

        // Calculate direction to target
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < 5) {
            isMoving = false;
            return;
        }

        // Normalize direction
        dx /= distance;
        dy /= distance;

        // Update speed with acceleration/deceleration
        if (distance > 100) {
            // Accelerate
            speed = Math.min(speed + acceleration * deltaTime, maxSpeed);
        } else {
            // Decelerate
            speed = Math.max(speed - deceleration * deltaTime, 0);
        }

        // Apply movement
        x += dx * speed * deltaTime;
        y += dy * speed * deltaTime;
    }

    public void applyImpact(Point2D impactPoint, double force) {
        double dx = x - impactPoint.getX();
        double dy = y - impactPoint.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < IMPACT_RADIUS) {
            // Calculate impact force based on distance
            double impactForce = force * (1 - distance / IMPACT_RADIUS);
            
            // Add noise based on impact force
            noise += impactForce;
            
            // Apply deflection
            double deflectionAngle = Math.atan2(dy, dx);
            x += Math.cos(deflectionAngle) * impactForce * 10;
            y += Math.sin(deflectionAngle) * impactForce * 10;
        }
    }

    public void resetNoise() {
        noise = 0;
    }

    public boolean isDestroyed() {
        return noise > NOISE_THRESHOLD;
    }

    public abstract void draw(Graphics2D g2d);
    public abstract double getSize();
    public abstract int getReward();
} 