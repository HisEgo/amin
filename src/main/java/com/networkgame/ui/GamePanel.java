package com.networkgame.ui;

import com.networkgame.model.*;
import com.networkgame.controller.GameController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
    private GameState gameState;
    private Port selectedPort;
    private boolean isWiring;
    private Timer gameTimer;
    private static final int FPS = 60;
    private long lastUpdateTime;
    private boolean isShopOpen;
    private JDialog shopDialog;
    private GameController controller;
    private boolean[] activeEffects = new boolean[3]; // Atar, Airyaman, Anahita
    private long[] effectEndTimes = new long[3];

    public GamePanel(GameController controller) {
        this.controller = controller;
        this.gameState = new GameState();
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);

        // Initialize game timer
        lastUpdateTime = System.nanoTime();
        gameTimer = new Timer(1000 / FPS, e -> {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0;
            lastUpdateTime = currentTime;
            
            updateEffects(currentTime);
            gameState.update(deltaTime);
            repaint();
        });
        gameTimer.start();

        // Initialize some test systems
        initializeTestSystems();
    }

    private void initializeTestSystems() {
        // Add reference systems
        NetworkSystem sourceSystem = new NetworkSystem(50, 200, 100, 200, true);
        NetworkSystem targetSystem = new NetworkSystem(650, 200, 100, 200, true);
        
        // Add intermediate systems
        NetworkSystem intermediate1 = new NetworkSystem(250, 150, 100, 200, false);
        NetworkSystem intermediate2 = new NetworkSystem(450, 250, 100, 200, false);
        
        gameState.addSystem(sourceSystem);
        gameState.addSystem(intermediate1);
        gameState.addSystem(intermediate2);
        gameState.addSystem(targetSystem);
    }

    private void updateEffects(long currentTime) {
        for (int i = 0; i < activeEffects.length; i++) {
            if (activeEffects[i] && currentTime > effectEndTimes[i]) {
                activeEffects[i] = false;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw connections
        for (Connection connection : gameState.getConnections()) {
            connection.draw(g2d);
        }

        // Draw systems
        for (NetworkSystem system : gameState.getSystems()) {
            system.draw(g2d);
            drawSystemIndicator(g2d, system);
            drawStoredPackets(g2d, system);
        }

        // Draw packets
        for (Packet packet : gameState.getActivePackets()) {
            packet.draw(g2d);
        }

        // Draw HUD
        drawHUD(g2d);

        // Draw wiring preview
        if (isWiring && selectedPort != null) {
            Point mousePoint = getMousePosition();
            if (mousePoint != null) {
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                g2d.drawLine(
                    (int) selectedPort.getX(),
                    (int) selectedPort.getY(),
                    mousePoint.x,
                    mousePoint.y
                );
            }
        }

        // Draw active effects
        drawActiveEffects(g2d);

        // Draw game over screen
        if (gameState.isGameOver()) {
            drawGameOver(g2d);
        }
    }

    private void drawSystemIndicator(Graphics2D g2d, NetworkSystem system) {
        if (system.isReferenceSystem()) {
            g2d.setColor(Color.GREEN);
            g2d.fillOval(
                (int)system.getBounds().getX() + system.getBounds().width - 20,
                (int)system.getBounds().getY() - 20,
                20, 20
            );
        }
    }

    private void drawStoredPackets(Graphics2D g2d, NetworkSystem system) {
        int storedCount = system.getStoredPackets().size();
        if (storedCount > 0) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("Stored: " + storedCount, 
                (int)system.getBounds().getX(),
                (int)system.getBounds().getY() - 5);
        }
    }

    private void drawActiveEffects(Graphics2D g2d) {
        int y = 100;
        if (activeEffects[0]) { // Atar
            g2d.setColor(Color.RED);
            g2d.drawString("Atar Active", 10, y);
        }
        if (activeEffects[1]) { // Airyaman
            g2d.setColor(Color.BLUE);
            g2d.drawString("Airyaman Active", 10, y + 20);
        }
        if (activeEffects[2]) { // Anahita
            g2d.setColor(Color.GREEN);
            g2d.drawString("Anahita Active", 10, y + 40);
        }
    }

    private void drawHUD(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        
        int y = 20;
        g2d.drawString("Wire Length: " + (int)gameState.getRemainingWireLength(), 10, y);
        g2d.drawString("Temporal Progress: " + (int)(gameState.getTemporalProgress() * 100) + "%", 10, y += 20);
        g2d.drawString("Packet Loss: " + gameState.getPacketLoss() + "%", 10, y += 20);
        g2d.drawString("Coins: " + gameState.getCoins(), 10, y += 20);
        g2d.drawString("Success Rate: " + calculateSuccessRate() + "%", 10, y += 20);
    }

    private void drawGameOver(Graphics2D g2d) {
        // Semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Game over text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        String gameOver = "Game Over";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(gameOver)) / 2;
        g2d.drawString(gameOver, x, getHeight() / 2);

        // Statistics
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String stats = String.format("Success Rate: %d%%", calculateSuccessRate());
        x = (getWidth() - fm.stringWidth(stats)) / 2;
        g2d.drawString(stats, x, getHeight() / 2 + 40);
    }

    private int calculateSuccessRate() {
        if (gameState.getTotalPackets() == 0) return 0;
        return (int)((double)gameState.getSuccessfulPackets() / gameState.getTotalPackets() * 100);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                gameState.setTemporalProgress(Math.max(0, gameState.getTemporalProgress() - 0.1));
                break;
            case KeyEvent.VK_RIGHT:
                gameState.setTemporalProgress(Math.min(1, gameState.getTemporalProgress() + 0.1));
                break;
            case KeyEvent.VK_S:
                if (!isShopOpen) {
                    openShop();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                if (isShopOpen) {
                    closeShop();
                }
                break;
        }
    }

    private void openShop() {
        isShopOpen = true;
        gameState.setPaused(true);
        
        shopDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Shop", true);
        shopDialog.setLayout(new GridLayout(3, 1, 10, 10));
        
        // Add shop items
        addShopItem("Atar (3 coins)", "Disable impact waves for 10 seconds", 3);
        addShopItem("Airyaman (4 coins)", "Disable packet collisions for 5 seconds", 4);
        addShopItem("Anahita (5 coins)", "Reset noise for all packets", 5);
        
        shopDialog.pack();
        shopDialog.setLocationRelativeTo(this);
        shopDialog.setVisible(true);
    }

    private void addShopItem(String name, String description, int cost) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        JButton buyButton = new JButton("Buy (" + cost + " coins)");
        buyButton.addActionListener(e -> {
            if (gameState.getCoins() >= cost) {
                gameState.addCoins(-cost);
                // Apply effect based on item
                applyShopEffect(name);
                closeShop();
            }
        });
        
        JLabel nameLabel = new JLabel(name);
        JLabel descLabel = new JLabel(description);
        
        itemPanel.add(nameLabel, BorderLayout.NORTH);
        itemPanel.add(descLabel, BorderLayout.CENTER);
        itemPanel.add(buyButton, BorderLayout.SOUTH);
        
        shopDialog.add(itemPanel);
    }

    private void applyShopEffect(String itemName) {
        long currentTime = System.nanoTime();
        switch (itemName) {
            case "Atar (3 coins)":
                activeEffects[0] = true;
                effectEndTimes[0] = currentTime + 10_000_000_000L; // 10 seconds
                controller.playSound("atar");
                break;
            case "Airyaman (4 coins)":
                activeEffects[1] = true;
                effectEndTimes[1] = currentTime + 5_000_000_000L; // 5 seconds
                controller.playSound("airyaman");
                break;
            case "Anahita (5 coins)":
                activeEffects[2] = true;
                effectEndTimes[2] = currentTime + 1_000_000_000L; // 1 second
                controller.playSound("anahita");
                for (Packet packet : gameState.getActivePackets()) {
                    packet.resetNoise();
                }
                break;
        }
    }

    private void closeShop() {
        isShopOpen = false;
        gameState.setPaused(false);
        if (shopDialog != null) {
            shopDialog.dispose();
        }
    }

    // Existing mouse event handlers...
    @Override public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            // Check if clicked on a port
            for (NetworkSystem system : gameState.getSystems()) {
                for (Port port : system.getOutputPorts()) {
                    if (isPointNearPort(e.getPoint(), port)) {
                        selectedPort = port;
                        isWiring = true;
                        return;
                    }
                }
            }
        }
    }

    @Override public void mouseReleased(MouseEvent e) {
        if (isWiring && selectedPort != null) {
            // Check if released on an input port
            for (NetworkSystem system : gameState.getSystems()) {
                for (Port port : system.getInputPorts()) {
                    if (isPointNearPort(e.getPoint(), port)) {
                        gameState.addConnection(selectedPort, port);
                        break;
                    }
                }
            }
        }
        isWiring = false;
        selectedPort = null;
        repaint();
    }

    private boolean isPointNearPort(Point point, Port port) {
        return point.distance(port.getX(), port.getY()) < 10;
    }

    // Required interface methods
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
} 