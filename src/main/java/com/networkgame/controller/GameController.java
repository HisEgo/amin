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

    public GameController() {
        initializeUI();
        loadSounds();
    }

    private void initializeUI() {
        mainFrame = new JFrame("Network Packet Game");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize panels
        menuPanel = new MainMenuPanel(this);
        gamePanel = new GamePanel();
        settingsPanel = new SettingsPanel(this);
        levelSelectPanel = new LevelSelectPanel(this);

        // Add panels to card layout
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(settingsPanel, "SETTINGS");
        mainPanel.add(levelSelectPanel, "LEVELS");

        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
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
        
        switch (level) {
            case 1:
                // Level 1: Simple network with 2 reference systems and 2 intermediate systems
                NetworkSystem source1 = new NetworkSystem(50, 200, 100, 200, true);
                NetworkSystem target1 = new NetworkSystem(650, 200, 100, 200, true);
                NetworkSystem intermediate1 = new NetworkSystem(250, 150, 100, 200, false);
                NetworkSystem intermediate2 = new NetworkSystem(450, 250, 100, 200, false);
                
                gameState.addSystem(source1);
                gameState.addSystem(intermediate1);
                gameState.addSystem(intermediate2);
                gameState.addSystem(target1);
                break;

            case 2:
                // Level 2: More complex network with 3 reference systems and 4 intermediate systems
                NetworkSystem source2 = new NetworkSystem(50, 150, 100, 200, true);
                NetworkSystem target2 = new NetworkSystem(650, 150, 100, 200, true);
                NetworkSystem target3 = new NetworkSystem(650, 350, 100, 200, true);
                
                NetworkSystem intermediate3 = new NetworkSystem(200, 100, 100, 200, false);
                NetworkSystem intermediate4 = new NetworkSystem(350, 200, 100, 200, false);
                NetworkSystem intermediate5 = new NetworkSystem(500, 300, 100, 200, false);
                NetworkSystem intermediate6 = new NetworkSystem(200, 400, 100, 200, false);
                
                gameState.addSystem(source2);
                gameState.addSystem(intermediate3);
                gameState.addSystem(intermediate4);
                gameState.addSystem(intermediate5);
                gameState.addSystem(intermediate6);
                gameState.addSystem(target2);
                gameState.addSystem(target3);
                break;
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
} 