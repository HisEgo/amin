package com.networkgame.ui;

import com.networkgame.controller.GameController;
import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private GameController controller;
    private JSlider volumeSlider;

    public SettingsPanel(GameController controller) {
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
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        add(titleLabel, gbc);

        // Volume control
        JLabel volumeLabel = new JLabel("Volume");
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(volumeLabel, gbc);

        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(controller.getVolume() * 100));
        volumeSlider.setMajorTickSpacing(20);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.addChangeListener(e -> {
            float volume = volumeSlider.getValue() / 100f;
            controller.setVolume(volume);
        });
        add(volumeSlider, gbc);

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