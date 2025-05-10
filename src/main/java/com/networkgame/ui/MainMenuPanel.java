package com.networkgame.ui;

import com.networkgame.controller.GameController;
import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    private GameController controller;

    public MainMenuPanel(GameController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(800, 600));
        setLayout(new GridBagLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Network Packet Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        add(titleLabel, gbc);

        // Buttons
        JButton startButton = new JButton("Start Game");
        JButton levelsButton = new JButton("Levels");
        JButton settingsButton = new JButton("Settings");
        JButton exitButton = new JButton("Exit");

        // Add action listeners
        startButton.addActionListener(e -> controller.startGame());
        levelsButton.addActionListener(e -> controller.showLevelSelect());
        settingsButton.addActionListener(e -> controller.showSettings());
        exitButton.addActionListener(e -> controller.exitGame());

        // Add buttons to panel
        add(startButton, gbc);
        add(levelsButton, gbc);
        add(settingsButton, gbc);
        add(exitButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw background
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
} 