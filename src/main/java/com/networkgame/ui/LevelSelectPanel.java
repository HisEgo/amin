package com.networkgame.ui;

import com.networkgame.controller.GameController;
import javax.swing.*;
import java.awt.*;

public class LevelSelectPanel extends JPanel {
    private GameController controller;
    private static final int MAX_LEVELS = 2;

    public LevelSelectPanel(GameController controller) {
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
        JLabel titleLabel = new JLabel("Select Level");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        add(titleLabel, gbc);

        // Level buttons
        for (int i = 1; i <= MAX_LEVELS; i++) {
            final int level = i;
            JButton levelButton = new JButton("Level " + i);
            levelButton.addActionListener(e -> {
                controller.startGame();
            });
            add(levelButton, gbc);
        }

        // Back button
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> controller.showMainMenu());
        add(backButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw background
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
} 