package com.networkgame.model;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class GameState {
    private List<NetworkSystem> systems;
    private List<Connection> connections;
    private List<Packet> activePackets;
    private double remainingWireLength;
    private double temporalProgress;
    private int totalPackets;
    private int successfulPackets;
    private int coins;
    private double packetSpawnTimer;
    private boolean isPaused;
    private static final double PACKET_SPAWN_INTERVAL = 2.0;
    private static final double IMPACT_WAVE_DURATION = 0.5;
    private List<ImpactWave> activeImpactWaves;
    private boolean isGameOver;
    private double gameTimer;
    private static final double GAME_DURATION = 120.0; // 120 seconds
    private boolean allSystemsActive;

    public GameState() {
        systems = new ArrayList<>();
        connections = new ArrayList<>();
        activePackets = new ArrayList<>();
        activeImpactWaves = new ArrayList<>();
        remainingWireLength = 5000.0; // Initial wire length for level 1
        temporalProgress = 0.0;
        totalPackets = 0;
        successfulPackets = 0;
        coins = 0;
        packetSpawnTimer = 0.0;
        isPaused = false;
        isGameOver = false;
        gameTimer = GAME_DURATION;
        allSystemsActive = false;
    }

    public void update(double deltaTime) {
        if (isPaused) return;

        // Update game timer
        gameTimer -= deltaTime;
        if (gameTimer <= 0) {
            isGameOver = true;
            return;
        }

        // Update temporal progress
        temporalProgress = Math.max(0, Math.min(1, temporalProgress));

        // Update impact waves
        updateImpactWaves(deltaTime);

        // Check if all systems are active
        allSystemsActive = true;
        for (NetworkSystem system : systems) {
            if (!system.isIndicatorOn()) {
                allSystemsActive = false;
                break;
            }
        }

        // Only spawn new packets if all systems are active
        if (allSystemsActive) {
            packetSpawnTimer += deltaTime;
            if (packetSpawnTimer >= PACKET_SPAWN_INTERVAL) {
                packetSpawnTimer = 0;
                spawnPacket();
            }
        }

        // Update systems
        for (NetworkSystem system : systems) {
            system.update();
        }

        // Update packets
        Iterator<Packet> packetIterator = activePackets.iterator();
        while (packetIterator.hasNext()) {
            Packet packet = packetIterator.next();
            packet.update(deltaTime);

            // Check for collisions
            checkPacketCollisions(packet);

            // Check if packet reached destination
            if (!packet.isMoving()) {
                if (packet.getTargetPort() != null && 
                    packet.getTargetPort().getParentSystem().isReferenceSystem()) {
                    successfulPackets++;
                    coins += packet.getReward();
                }
                packetIterator.remove();
            }

            // Check if packet is destroyed
            if (packet.isDestroyed()) {
                packetIterator.remove();
            }
        }

        // Check for game over condition
        if (getPacketLoss() > 50) {
            isGameOver = true;
        }
    }

    private void updateImpactWaves(double deltaTime) {
        Iterator<ImpactWave> waveIterator = activeImpactWaves.iterator();
        while (waveIterator.hasNext()) {
            ImpactWave wave = waveIterator.next();
            wave.update(deltaTime);
            if (wave.isExpired()) {
                waveIterator.remove();
            }
        }
    }

    private void checkPacketCollisions(Packet packet) {
        for (Packet other : activePackets) {
            if (packet != other) {
                double distance = Math.sqrt(
                    Math.pow(packet.getX() - other.getX(), 2) +
                    Math.pow(packet.getY() - other.getY(), 2)
                );
                
                if (distance < (packet.getSize() + other.getSize()) / 2) {
                    // Create impact wave at collision point
                    Point2D impactPoint = new Point2D.Double(
                        (packet.getX() + other.getX()) / 2,
                        (packet.getY() + other.getY()) / 2
                    );
                    createImpactWave(impactPoint);
                    
                    // Apply impact to both packets
                    packet.applyImpact(impactPoint, 1.0);
                    other.applyImpact(impactPoint, 1.0);
                }
            }
        }
    }

    private void createImpactWave(Point2D center) {
        activeImpactWaves.add(new ImpactWave(center, IMPACT_WAVE_DURATION));
    }

    private void spawnPacket() {
        // Find available reference system for spawning
        NetworkSystem sourceSystem = findAvailableReferenceSystem();
        if (sourceSystem != null) {
            // Find available output port
            Port outputPort = sourceSystem.findAvailableOutputPort();
            if (outputPort != null) {
                // Create packet based on port type
                Packet packet;
                if (outputPort.getType() == PortType.SQUARE) {
                    packet = new SquarePacket(outputPort.getX(), outputPort.getY(), null);
                } else {
                    packet = new TrianglePacket(outputPort.getX(), outputPort.getY(), null);
                }
                activePackets.add(packet);
                totalPackets++;
            }
        }
    }

    private NetworkSystem findAvailableReferenceSystem() {
        for (NetworkSystem system : systems) {
            if (system.isReferenceSystem() && system.hasAvailableOutputPort()) {
                return system;
            }
        }
        return null;
    }

    public void addConnection(Port startPort, Port endPort) {
        double length = startPort.getPosition().distance(endPort.getPosition());
        if (length <= remainingWireLength) {
            connections.add(new Connection(startPort, endPort));
            remainingWireLength -= length;
        }
    }

    public void addSystem(NetworkSystem system) {
        systems.add(system);
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public void setTemporalProgress(double progress) {
        temporalProgress = progress;
    }

    public int getPacketLoss() {
        if (totalPackets == 0) return 0;
        return (int)((double)(totalPackets - successfulPackets) / totalPackets * 100);
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    // Getters
    public List<NetworkSystem> getSystems() { return systems; }
    public List<Connection> getConnections() { return connections; }
    public List<Packet> getActivePackets() { return activePackets; }
    public double getRemainingWireLength() { return remainingWireLength; }
    public double getTemporalProgress() { return temporalProgress; }
    public int getTotalPackets() { return totalPackets; }
    public int getSuccessfulPackets() { return successfulPackets; }
    public int getCoins() { return coins; }
    public boolean isPaused() { return isPaused; }
    public List<ImpactWave> getActiveImpactWaves() { return activeImpactWaves; }
} 