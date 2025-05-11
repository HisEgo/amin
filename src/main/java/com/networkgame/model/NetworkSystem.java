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

    public NetworkSystem(double x, double y, double width, double height, boolean isReferenceSystem) {
        this.bounds = new Rectangle2D.Double(x, y, width, height);
        this.isReferenceSystem = isReferenceSystem;
        this.inputPorts = new ArrayList<>();
        this.outputPorts = new ArrayList<>();
        this.storedPackets = new LinkedList<>();
        this.isActive = true;
        this.color = new Color(200, 200, 200);
        
        initializePorts();
    }

    private void initializePorts() {
        // Add input ports on the left side
        for (int i = 0; i < 3; i++) {
            PortType type = i % 2 == 0 ? PortType.SQUARE : PortType.TRIANGLE;
            Port port = new Port(
                bounds.getX(),
                bounds.getY() + (bounds.getHeight() / 4) * (i + 1),
                type,
                true
            );
            port.setParentSystem(this);
            inputPorts.add(port);
        }

        // Add output ports on the right side
        for (int i = 0; i < 3; i++) {
            PortType type = i % 2 == 0 ? PortType.SQUARE : PortType.TRIANGLE;
            Port port = new Port(
                bounds.getMaxX(),
                bounds.getY() + (bounds.getHeight() / 4) * (i + 1),
                type,
                false
            );
            port.setParentSystem(this);
            outputPorts.add(port);
        }
    }

    public void update() {
        if (!isActive) return;

        // Process stored packets
        if (!storedPackets.isEmpty()) {
            Packet packet = storedPackets.peek();
            Port outputPort = findCompatibleOutputPort(packet);
            if (outputPort != null && !outputPort.isOccupied()) {
                storedPackets.poll();
                outputPort.setOccupied(true);
                packet.setTargetPort(outputPort);
            }
        }
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

    public boolean canStorePacket() {
        return storedPackets.size() < MAX_STORED_PACKETS;
    }

    public void storePacket(Packet packet) {
        if (canStorePacket()) {
            storedPackets.add(packet);
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

    // Getters and setters
    public Rectangle2D getBounds() { return bounds; }
    public List<Port> getInputPorts() { return inputPorts; }
    public List<Port> getOutputPorts() { return outputPorts; }
    public boolean isReferenceSystem() { return isReferenceSystem; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public Queue<Packet> getStoredPackets() { return storedPackets; }
} 