package com.networkgame.controller;

import com.networkgame.model.*;
import com.networkgame.ui.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.*;

public class GameController {
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GamePanel gamePanel;
    private MainMenuPanel menuPanel;
    private SettingsPanel settingsPanel;
    private LevelSelectPanel levelSelectPanel;
    private GameState gameState;
    private Clip backgroundMusic;
    private float volume = 0.5f;
    private int currentLevel = 1;
    private static final int MAX_LEVELS = 2;
    private boolean isLevelCompleted = false;

    public GameController() {
        initializeUI();
        loadSounds();
    }

    private void initializeUI() {
        mainFrame = new JFrame("Network Packet Game");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        
        // Disable maximize button
        mainFrame.setUndecorated(true);
        mainFrame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        
        // Center window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setLocation(
            (screenSize.width - 800) / 2,
            (screenSize.height - 600) / 2
        );

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize panels
        menuPanel = new MainMenuPanel(this);
        gamePanel = new GamePanel(this);
        settingsPanel = new SettingsPanel(this);
        levelSelectPanel = new LevelSelectPanel(this);

        // Add panels to card layout
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(settingsPanel, "SETTINGS");
        mainPanel.add(levelSelectPanel, "LEVELS");

        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private void loadSounds() {
        try {
            // Load background music
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                getClass().getResource("/sounds/background.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioIn);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            setVolume(volume);
        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
        }
    }

    public void startGame() {
        gameState = new GameState();
        initializeLevel(currentLevel);
        isLevelCompleted = false;
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocus();
    }

    public void showMainMenu() {
        cardLayout.show(mainPanel, "MENU");
    }

    public void showSettings() {
        cardLayout.show(mainPanel, "SETTINGS");
    }

    public void showLevelSelect() {
        cardLayout.show(mainPanel, "LEVELS");
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if (backgroundMusic != null) {
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }

    public float getVolume() {
        return volume;
    }

    private void initializeLevel(int level) {
        gameState = new GameState();
        LevelConfig levelConfig = new LevelConfig(level);
        
        // Set initial wire length
        gameState.setRemainingWireLength(levelConfig.getInitialWireLength());
        
        // Create systems
        for (LevelConfig.SystemConfig sysConfig : levelConfig.getSystems()) {
            NetworkSystem system = new NetworkSystem(
                sysConfig.getX(),
                sysConfig.getY(),
                100, 200,
                sysConfig.isReference()
            );
            
            // Configure ports
            for (PortType type : sysConfig.getInputPorts()) {
                system.addInputPort(type);
            }
            for (PortType type : sysConfig.getOutputPorts()) {
                system.addOutputPort(type);
            }
            
            gameState.addSystem(system);
        }
        
        // Initialize packets
        for (LevelConfig.PacketConfig packetConfig : levelConfig.getPackets()) {
            NetworkSystem targetSystem = null;
            if (packetConfig.getSystemId() > 0) {
                targetSystem = gameState.getSystems().get(packetConfig.getSystemId() - 1);
            }
            
            for (int i = 0; i < packetConfig.getCount(); i++) {
                Packet packet;
                if (packetConfig.getType() == PortType.SQUARE) {
                    packet = new SquarePacket(0, 0, null);
                } else {
                    packet = new TrianglePacket(0, 0, null);
                }
                
                if (targetSystem != null) {
                    targetSystem.storePacket(packet);
                }
            }
        }
    }

    public void nextLevel() {
        if (currentLevel < MAX_LEVELS) {
            currentLevel++;
            startGame();
        } else {
            // Game completed
            showMainMenu();
        }
    }

    public void playSound(String soundName) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                getClass().getResource("/sounds/" + soundName + ".wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    public void exitGame() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
        System.exit(0);
    }

    public void checkLevelCompletion() {
        if (!isLevelCompleted && gameState != null) {
            int totalPackets = gameState.getTotalPackets();
            int successfulPackets = gameState.getSuccessfulPackets();
            
            if (totalPackets > 0 && (double)successfulPackets / totalPackets >= 0.5) {
                isLevelCompleted = true;
                showLevelComplete();
            }
        }
    }

    private void showLevelComplete() {
        playSound("success");
        
        JDialog dialog = new JDialog(mainFrame, "Level Complete!", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel statsLabel = new JLabel(String.format(
            "Success Rate: %d%%\nTotal Packets: %d\nSuccessful Packets: %d",
            (int)((double)gameState.getSuccessfulPackets() / gameState.getTotalPackets() * 100),
            gameState.getTotalPackets(),
            gameState.getSuccessfulPackets()
        ));
        dialog.add(statsLabel, gbc);

        JButton nextButton = new JButton("Next Level");
        nextButton.addActionListener(e -> {
            dialog.dispose();
            nextLevel();
        });
        dialog.add(nextButton, gbc);

        JButton menuButton = new JButton("Back to Menu");
        menuButton.addActionListener(e -> {
            dialog.dispose();
            showMainMenu();
        });
        dialog.add(menuButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }
} 