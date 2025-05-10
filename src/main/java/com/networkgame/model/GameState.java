package com.networkgame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {
    private List<NetworkSystem> systems;
    private List<Connection> connections;
    private List<Packet> activePackets;
    private double remainingWireLength;
    private double temporalProgress;
    private int packetLoss;
    private int coins;
    private boolean isGameOver;
    private boolean isPaused;
    private Random random;
    private int totalPackets;
    private int successfulPackets;
    private double packetSpawnTimer;
    private static final double PACKET_SPAWN_INTERVAL = 2.0; // seconds
    private static final double IMPACT_RADIUS = 20.0;
    private static final double IMPACT_FORCE = 0.5;

    public GameState() {
        this.systems = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.activePackets = new ArrayList<>();
        this.remainingWireLength = 1000; // Initial wire length
        this.temporalProgress = 0;
        this.packetLoss = 0;
        this.coins = 0;
        this.isGameOver = false;
        this.isPaused = false;
        this.random = new Random();
        this.totalPackets = 0;
        this.successfulPackets = 0;
        this.packetSpawnTimer = 0;
    }

    public void update(double deltaTime) {
        if (isPaused || isGameOver) return;

        // Update temporal progress
        temporalProgress = Math.min(1.0, temporalProgress + deltaTime * 0.1);

        // Update packet spawn timer
        packetSpawnTimer += deltaTime;
        if (packetSpawnTimer >= PACKET_SPAWN_INTERVAL) {
            spawnPacket();
            packetSpawnTimer = 0;
        }

        // Update all systems
        for (NetworkSystem system : systems) {
            system.update();
        }

        // Update all packets and check collisions
        List<Packet> packetsToRemove = new ArrayList<>();
        for (Packet packet : activePackets) {
            packet.update();
            
            // Check if packet reached its destination
            if (!packet.isActive()) {
                packetsToRemove.add(packet);
                if (isPacketAtDestination(packet)) {
                    successfulPackets++;
                    addCoins(calculateReward(packet));
                } else {
                    packetLoss++;
                }
            }

            // Check for collisions with other packets
            for (Packet otherPacket : activePackets) {
                if (packet != otherPacket && packet.isActive() && otherPacket.isActive()) {
                    checkCollision(packet, otherPacket);
                }
            }
        }

        // Remove inactive packets
        activePackets.removeAll(packetsToRemove);

        // Check game over condition
        if (packetLoss > 50 || (totalPackets > 0 && (double)packetLoss / totalPackets > 0.5)) {
            isGameOver = true;
        }
    }

    private void spawnPacket() {
        // Find a reference system with available output ports
        NetworkSystem sourceSystem = findAvailableReferenceSystem();
        if (sourceSystem == null) return;

        // Find a compatible output port
        Port outputPort = findAvailableOutputPort(sourceSystem);
        if (outputPort == null) return;

        // Find a target reference system
        NetworkSystem targetSystem = findTargetReferenceSystem(sourceSystem);
        if (targetSystem == null) return;

        // Create packet based on port type
        Packet packet;
        Point2D startPoint = new Point2D.Double(outputPort.getX(), outputPort.getY());
        Point2D endPoint = new Point2D.Double(targetSystem.getBounds().getCenterX(), targetSystem.getBounds().getCenterY());

        if (outputPort.getType() == PortType.SQUARE) {
            packet = new SquarePacket(startPoint, endPoint);
        } else {
            packet = new TrianglePacket(startPoint, endPoint);
        }

        activePackets.add(packet);
        totalPackets++;
        outputPort.setOccupied(true);
    }

    private NetworkSystem findAvailableReferenceSystem() {
        for (NetworkSystem system : systems) {
            if (system.isReferenceSystem() && hasAvailableOutputPort(system)) {
                return system;
            }
        }
        return null;
    }

    private boolean hasAvailableOutputPort(NetworkSystem system) {
        for (Port port : system.getOutputPorts()) {
            if (!port.isOccupied()) {
                return true;
            }
        }
        return false;
    }

    private Port findAvailableOutputPort(NetworkSystem system) {
        for (Port port : system.getOutputPorts()) {
            if (!port.isOccupied()) {
                return port;
            }
        }
        return null;
    }

    private NetworkSystem findTargetReferenceSystem(NetworkSystem sourceSystem) {
        List<NetworkSystem> targets = new ArrayList<>();
        for (NetworkSystem system : systems) {
            if (system.isReferenceSystem() && system != sourceSystem) {
                targets.add(system);
            }
        }
        return targets.isEmpty() ? null : targets.get(random.nextInt(targets.size()));
    }

    private void checkCollision(Packet packet1, Packet packet2) {
        double distance = packet1.getPosition().distance(packet2.getPosition());
        if (distance < IMPACT_RADIUS) {
            // Calculate impact force based on distance
            double impactForce = IMPACT_FORCE * (1 - distance / IMPACT_RADIUS);
            
            // Apply impact to both packets
            packet1.applyImpact(impactForce, packet2.getPosition());
            packet2.applyImpact(impactForce, packet1.getPosition());
        }
    }

    private boolean isPacketAtDestination(Packet packet) {
        for (NetworkSystem system : systems) {
            if (system.isReferenceSystem() && 
                system.getBounds().contains(packet.getPosition().getX(), packet.getPosition().getY())) {
                return true;
            }
        }
        return false;
    }

    private int calculateReward(Packet packet) {
        // Base reward
        int reward = 1;
        
        // Additional reward for triangle packets
        if (packet instanceof TrianglePacket) {
            reward = 2;
        }
        
        // Bonus for successful delivery
        if (packet.getNoise() < packet.getSize() * 0.5) {
            reward *= 2;
        }
        
        return reward;
    }

    public boolean addConnection(Port startPort, Port endPort) {
        // Check if ports are already connected
        for (Connection connection : connections) {
            if ((connection.getStartPort() == startPort && connection.getEndPort() == endPort) ||
                (connection.getStartPort() == endPort && connection.getEndPort() == startPort)) {
                return false;
            }
        }

        double connectionLength = startPort.getX() - endPort.getX();
        if (connectionLength > remainingWireLength) {
            return false;
        }

        Connection connection = new Connection(startPort, endPort);
        connections.add(connection);
        remainingWireLength -= connectionLength;
        return true;
    }

    public void addSystem(NetworkSystem system) {
        systems.add(system);
    }

    public void addPacket(Packet packet) {
        activePackets.add(packet);
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public void setTemporalProgress(double progress) {
        this.temporalProgress = Math.max(0, Math.min(1, progress));
    }

    // Getters
    public List<NetworkSystem> getSystems() { return systems; }
    public List<Connection> getConnections() { return connections; }
    public List<Packet> getActivePackets() { return activePackets; }
    public double getRemainingWireLength() { return remainingWireLength; }
    public double getTemporalProgress() { return temporalProgress; }
    public int getPacketLoss() { return packetLoss; }
    public int getCoins() { return coins; }
    public boolean isGameOver() { return isGameOver; }
    public boolean isPaused() { return isPaused; }
    public void setPaused(boolean paused) { isPaused = paused; }
    public int getTotalPackets() { return totalPackets; }
    public int getSuccessfulPackets() { return successfulPackets; }
} 