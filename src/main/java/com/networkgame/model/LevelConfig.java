package com.networkgame.model;

import java.util.ArrayList;
import java.util.List;

public class LevelConfig {
    private int levelNumber;
    private double initialWireLength;
    private List<SystemConfig> systems;
    private List<PacketConfig> packets;

    public LevelConfig(int levelNumber) {
        this.levelNumber = levelNumber;
        this.systems = new ArrayList<>();
        this.packets = new ArrayList<>();
        initializeLevel();
    }

    private void initializeLevel() {
        switch (levelNumber) {
            case 1:
                initializeLevel1();
                break;
            case 2:
                initializeLevel2();
                break;
        }
    }

    private void initializeLevel1() {
        initialWireLength = 5000.0;

        // System 1 (Reference)
        SystemConfig system1 = new SystemConfig(50, 200, true);
        system1.addOutputPort(PortType.SQUARE);
        system1.addOutputPort(PortType.SQUARE);
        system1.addOutputPort(PortType.TRIANGLE);
        system1.addInputPort(PortType.SQUARE);
        systems.add(system1);

        // System 2
        SystemConfig system2 = new SystemConfig(250, 150, false);
        system2.addOutputPort(PortType.SQUARE);
        system2.addOutputPort(PortType.TRIANGLE);
        system2.addInputPort(PortType.SQUARE);
        system2.addInputPort(PortType.SQUARE);
        systems.add(system2);

        // System 3
        SystemConfig system3 = new SystemConfig(450, 250, false);
        system3.addOutputPort(PortType.SQUARE);
        system3.addInputPort(PortType.SQUARE);
        system3.addInputPort(PortType.TRIANGLE);
        systems.add(system3);

        // System 4
        SystemConfig system4 = new SystemConfig(650, 200, false);
        system4.addOutputPort(PortType.SQUARE);
        system4.addInputPort(PortType.SQUARE);
        system4.addInputPort(PortType.TRIANGLE);
        systems.add(system4);

        // Packets
        packets.add(new PacketConfig(PortType.SQUARE, 3)); // 3 square packets
        packets.add(new PacketConfig(PortType.TRIANGLE, 2)); // 2 triangle packets
    }

    private void initializeLevel2() {
        initialWireLength = 8000.0;

        // System 1 (Reference)
        SystemConfig system1 = new SystemConfig(50, 150, true);
        system1.addOutputPort(PortType.SQUARE);
        system1.addOutputPort(PortType.SQUARE);
        system1.addOutputPort(PortType.TRIANGLE);
        system1.addInputPort(PortType.SQUARE);
        system1.addInputPort(PortType.SQUARE);
        system1.addInputPort(PortType.TRIANGLE);
        systems.add(system1);

        // System 2 (Reference)
        SystemConfig system2 = new SystemConfig(50, 350, true);
        system2.addOutputPort(PortType.SQUARE);
        system2.addOutputPort(PortType.SQUARE);
        system2.addOutputPort(PortType.TRIANGLE);
        system2.addInputPort(PortType.TRIANGLE);
        system2.addInputPort(PortType.SQUARE);
        systems.add(system2);

        // System 3
        SystemConfig system3 = new SystemConfig(250, 150, false);
        system3.addOutputPort(PortType.SQUARE);
        system3.addOutputPort(PortType.SQUARE);
        system3.addOutputPort(PortType.TRIANGLE);
        system3.addInputPort(PortType.SQUARE);
        system3.addInputPort(PortType.SQUARE);
        system3.addInputPort(PortType.TRIANGLE);
        systems.add(system3);

        // System 4
        SystemConfig system4 = new SystemConfig(250, 350, false);
        system4.addOutputPort(PortType.SQUARE);
        system4.addOutputPort(PortType.TRIANGLE);
        system4.addInputPort(PortType.SQUARE);
        system4.addInputPort(PortType.SQUARE);
        systems.add(system4);

        // System 5
        SystemConfig system5 = new SystemConfig(450, 250, false);
        system5.addOutputPort(PortType.SQUARE);
        system5.addInputPort(PortType.SQUARE);
        system5.addInputPort(PortType.TRIANGLE);
        systems.add(system5);

        // System 6
        SystemConfig system6 = new SystemConfig(650, 200, false);
        system6.addOutputPort(PortType.SQUARE);
        system6.addInputPort(PortType.SQUARE);
        system6.addInputPort(PortType.TRIANGLE);
        systems.add(system6);

        // Packets for System 1
        packets.add(new PacketConfig(PortType.SQUARE, 4, 1)); // 4 square packets
        packets.add(new PacketConfig(PortType.TRIANGLE, 1, 1)); // 1 triangle packet

        // Packets for System 2
        packets.add(new PacketConfig(PortType.SQUARE, 3, 2)); // 3 square packets
        packets.add(new PacketConfig(PortType.TRIANGLE, 2, 2)); // 2 triangle packets
    }

    public double getInitialWireLength() {
        return initialWireLength;
    }

    public List<SystemConfig> getSystems() {
        return systems;
    }

    public List<PacketConfig> getPackets() {
        return packets;
    }

    public static class SystemConfig {
        private double x, y;
        private boolean isReference;
        private List<PortType> inputPorts;
        private List<PortType> outputPorts;

        public SystemConfig(double x, double y, boolean isReference) {
            this.x = x;
            this.y = y;
            this.isReference = isReference;
            this.inputPorts = new ArrayList<>();
            this.outputPorts = new ArrayList<>();
        }

        public void addInputPort(PortType type) {
            inputPorts.add(type);
        }

        public void addOutputPort(PortType type) {
            outputPorts.add(type);
        }

        public double getX() { return x; }
        public double getY() { return y; }
        public boolean isReference() { return isReference; }
        public List<PortType> getInputPorts() { return inputPorts; }
        public List<PortType> getOutputPorts() { return outputPorts; }
    }

    public static class PacketConfig {
        private PortType type;
        private int count;
        private int systemId;

        public PacketConfig(PortType type, int count) {
            this(type, count, 0);
        }

        public PacketConfig(PortType type, int count, int systemId) {
            this.type = type;
            this.count = count;
            this.systemId = systemId;
        }

        public PortType getType() { return type; }
        public int getCount() { return count; }
        public int getSystemId() { return systemId; }
    }
} 