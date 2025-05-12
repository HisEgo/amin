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
    private boolean isReferenceSystem;
    private Queue<Packet> storedPackets;
    private static final int MAX_STORED_PACKETS = 5;
    private boolean isActive;
    private Color color;
    private boolean indicatorOn;

    public NetworkSystem(double x, double y, double width, double height, boolean isReferenceSystem) {
        this.bounds = new Rectangle2D.Double(x, y, width, height);
        this.isReferenceSystem = isReferenceSystem;
        this.inputPorts = new ArrayList<>();
        this.outputPorts = new ArrayList<>();
        this.storedPackets = new LinkedList<>();
        this.isActive = false;
        this.color = new Color(200, 200, 200);
        this.indicatorOn = false;
    }

    public void addInputPort(PortType type) {
        Port port = new Port(
            bounds.getX(),
            bounds.getY() + (bounds.getHeight() / (inputPorts.size() + 2)) * (inputPorts.size() + 1),
            type,
            true
        );
        port.setParentSystem(this);
        inputPorts.add(port);
    }

    public void addOutputPort(PortType type) {
        Port port = new Port(
            bounds.getMaxX(),
            bounds.getY() + (bounds.getHeight() / (outputPorts.size() + 2)) * (outputPorts.size() + 1),
            type,
            false
        );
        port.setParentSystem(this);
        outputPorts.add(port);
    }

    public void update() {
        // Check if all ports are connected
        boolean allInputsConnected = true;
        boolean allOutputsConnected = true;

        for (Port port : inputPorts) {
            if (!port.isOccupied()) {
                allInputsConnected = false;
                break;
            }
        }

        for (Port port : outputPorts) {
            if (!port.isOccupied()) {
                allOutputsConnected = false;
                break;
            }
        }

        // Update system active state
        isActive = allInputsConnected && allOutputsConnected;
        indicatorOn = isActive;

        // Process stored packets if system is active
        if (isActive && !storedPackets.isEmpty()) {
            Packet packet = storedPackets.peek();
            Port outputPort = findCompatibleOutputPort(packet);
            if (outputPort != null && !outputPort.isOccupied()) {
                storedPackets.poll();
                outputPort.setOccupied(true);
                packet.setTargetPort(outputPort);
            }
        }
    }

    public void draw(Graphics2D g2d) {
        // Draw system body
        g2d.setColor(isReferenceSystem ? new Color(200, 200, 255) : new Color(240, 240, 240));
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

        // Draw indicator
        if (indicatorOn) {
            g2d.setColor(Color.GREEN);
            g2d.fillOval(
                (int)bounds.getX() + bounds.width - 20,
                (int)bounds.getY() - 20,
                20, 20
            );
        }
    }

    public boolean isIndicatorOn() {
        return indicatorOn;
    }

    public boolean canStorePacket() {
        return storedPackets.size() < MAX_STORED_PACKETS;
    }

    public void storePacket(Packet packet) {
        if (canStorePacket()) {
            storedPackets.add(packet);
        }
    }

    public Port findAvailableOutputPort() {
        for (Port port : outputPorts) {
            if (!port.isOccupied()) {
                return port;
            }
        }
        return null;
    }

    public boolean hasAvailableOutputPort() {
        for (Port port : outputPorts) {
            if (!port.isOccupied()) {
                return true;
            }
        }
        return false;
    }

    private Port findCompatibleOutputPort(Packet packet) {
        // First try to find a compatible unoccupied port
        for (Port port : outputPorts) {
            if (!port.isOccupied() && isCompatible(packet, port)) {
                return port;
            }
        }
        // If no compatible port is available, find any unoccupied port
        for (Port port : outputPorts) {
            if (!port.isOccupied()) {
                return port;
            }
        }
        return null;
    }

    private boolean isCompatible(Packet packet, Port port) {
        if (packet instanceof SquarePacket) {
            return port.getType() == PortType.SQUARE;
        } else if (packet instanceof TrianglePacket) {
            return port.getType() == PortType.TRIANGLE;
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
    public Queue<Packet> getStoredPackets() { return storedPackets; }
} 