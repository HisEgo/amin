package com.networkgame.model;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class Packet {
    protected Point2D position;
    protected Point2D velocity;
    protected Point2D acceleration;
    protected double noise;
    protected double size;
    protected boolean isActive;
    protected Color color;
    protected static final double MAX_SPEED = 200.0;
    protected static final double ACCELERATION = 100.0;
    protected static final double DECELERATION = 50.0;
    protected static final double NOISE_INCREASE_RATE = 10.0;

    public Packet(Point2D startPoint, Point2D endPoint) {
        this.position = new Point2D.Double(startPoint.getX(), startPoint.getY());
        this.velocity = new Point2D.Double(0, 0);
        this.acceleration = new Point2D.Double(0, 0);
        this.noise = 0;
        this.isActive = true;
        
        // Calculate initial direction
        double dx = endPoint.getX() - startPoint.getX();
        double dy = endPoint.getY() - startPoint.getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            this.velocity.setLocation(dx / length * MAX_SPEED * 0.5, dy / length * MAX_SPEED * 0.5);
        }
    }

    public void update(double deltaTime) {
        if (!isActive) return;

        // Update position based on velocity
        position.setLocation(
            position.getX() + velocity.getX() * deltaTime,
            position.getY() + velocity.getY() * deltaTime
        );

        // Update velocity based on acceleration
        velocity.setLocation(
            velocity.getX() + acceleration.getX() * deltaTime,
            velocity.getY() + acceleration.getY() * deltaTime
        );

        // Apply deceleration
        double currentSpeed = Math.sqrt(
            velocity.getX() * velocity.getX() + 
            velocity.getY() * velocity.getY()
        );
        
        if (currentSpeed > 0) {
            double decelerationFactor = DECELERATION * deltaTime;
            if (decelerationFactor > currentSpeed) {
                decelerationFactor = currentSpeed;
            }
            double scale = (currentSpeed - decelerationFactor) / currentSpeed;
            velocity.setLocation(velocity.getX() * scale, velocity.getY() * scale);
        }

        // Increase noise over time
        noise += NOISE_INCREASE_RATE * deltaTime;
        
        // Check if packet is too noisy
        if (noise >= size) {
            isActive = false;
        }
    }

    public void applyImpact(double force, Point2D impactPoint) {
        // Calculate direction from impact point
        double dx = position.getX() - impactPoint.getX();
        double dy = position.getY() - impactPoint.getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        
        if (length > 0) {
            // Apply force in the direction away from impact
            double impactX = (dx / length) * force * MAX_SPEED;
            double impactY = (dy / length) * force * MAX_SPEED;
            
            // Add impact to velocity
            velocity.setLocation(
                velocity.getX() + impactX,
                velocity.getY() + impactY
            );
            
            // Increase noise
            noise += force * 20;
        }
    }

    public void resetNoise() {
        noise = 0;
    }

    public Point2D getPosition() {
        return position;
    }

    public double getNoise() {
        return noise;
    }

    public double getSize() {
        return size;
    }

    public boolean isActive() {
        return isActive;
    }

    public abstract void draw(Graphics2D g2d);
} 