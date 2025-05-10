package com.networkgame;

import com.networkgame.ui.*;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create main frame
        JFrame frame = new JFrame("Network Packet Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Create main panel with card layout
        JPanel mainPanel = new JPanel(new CardLayout());
        
        // Create and add panels
        MainMenuPanel mainMenuPanel = new MainMenuPanel((CardLayout) mainPanel.getLayout(), mainPanel);
        GamePanel gamePanel = new GamePanel();
        
        mainPanel.add(mainMenuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");

        // Add main panel to frame
        frame.add(mainPanel);
        
        // Show menu first
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "MENU");

        // Pack and show frame
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
} 