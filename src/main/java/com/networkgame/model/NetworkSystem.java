package com.networkgame.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class NetworkSystem {
    private Rectangle2D bounds;
    private List<Port> inputPorts;
    private List<Port> outputPorts;
    private Queue<Packet> packetQueue;
    private boolean isReferenceSystem;
    private int maxQueueSize = 5;
    private boolean isActive;
    private Color color;

    public NetworkSystem(double x, double y, double width, double height, boolean isReferenceSystem) {
        this.bounds = new Rectangle2D.Double(x, y, width, height);
        this.inputPorts = new ArrayList<>();
        this.outputPorts = new ArrayList<>();
        this.packetQueue = new LinkedList<>();
        this.isReferenceSystem = isReferenceSystem;
        this.isActive = true;
        this.color = new Color(200, 200, 200);
        
        initializePorts();
    }

    private void initializePorts() {
        // Add input ports on the left side
        for (int i = 0; i < 2; i++) {
            double portY = bounds.getY() + (bounds.getHeight() / 3) * (i + 1);
            inputPorts.add(new Port(bounds.getX(), portY, PortType.SQUARE));
            inputPorts.add(new Port(bounds.getX(), portY + 20, PortType.TRIANGLE));
        }

        // Add output ports on the right side
        for (int i = 0; i < 2; i++) {
            double portY = bounds.getY() + (bounds.getHeight() / 3) * (i + 1);
            outputPorts.add(new Port(bounds.getMaxX(), portY, PortType.SQUARE));
            outputPorts.add(new Port(bounds.getMaxX(), portY + 20, PortType.TRIANGLE));
        }
    }

    public void update() {
        if (!isActive) return;

        // Process packets in the queue
        if (!packetQueue.isEmpty()) {
            Packet packet = packetQueue.peek();
            Port compatiblePort = findCompatibleOutputPort(packet);
            
            if (compatiblePort != null) {
                packetQueue.poll();
                // Handle packet output
            }
        }
    }

    private Port findCompatibleOutputPort(Packet packet) {
        for (Port port : outputPorts) {
            if (port.isCompatible(packet) && !port.isOccupied()) {
                return port;
            }
        }
        return null;
    }

    public void draw(Graphics2D g2d) {
        // Draw system body
        g2d.setColor(color);
        g2d.fill(bounds);
        g2d.setColor(Color.BLACK);
        g2d.draw(bounds);

        // Draw ports
        for (Port port : inputPorts) {
            port.draw(g2d);
        }
        for (Port port : outputPorts) {
            port.draw(g2d);
        }
    }

    public boolean addPacket(Packet packet) {
        if (packetQueue.size() < maxQueueSize) {
            packetQueue.add(packet);
            return true;
        }
        return false;
    }

    // Getters and setters
    public Rectangle2D getBounds() { return bounds; }
    public List<Port> getInputPorts() { return inputPorts; }
    public List<Port> getOutputPorts() { return outputPorts; }
    public boolean isReferenceSystem() { return isReferenceSystem; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
} 