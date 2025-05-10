package com.networkgame;

import com.networkgame.controller.GameController;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameController();
        });
    }
} 