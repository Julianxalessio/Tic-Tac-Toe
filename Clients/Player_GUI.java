package Clients;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Player_GUI {
    public static JPanel lobby = new JPanel();
    public static JPanel gamePanel = new JPanel();
    public static JPanel start = new JPanel();
    public static JFrame frame = new JFrame("Tic Tac Toe Lobby");
    
    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(45, 52, 54);
    private static final Color ACCENT_COLOR = new Color(99, 110, 250);
    private static final Color TEXT_COLOR = new Color(223, 230, 233);
    private static final Color CARD_COLOR = new Color(58, 66, 68);
    private static final Color SUCCESS_COLOR = new Color(46, 213, 115);
    private static final Color BLACK_COLOR = new Color(0, 0, 0);
    
    static Player1 player;
    public static void main(String[] args) {
        getStartFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);  
        frame.setVisible(true);

        // Initialisiere Player in einem separaten Thread
        new Thread(() -> {
            try {
                player = new Player1();
                player.init();
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> alert("Network error: " + e.getMessage()));
            }
        }).start();
    }

    public static void getStartFrame(){
        start.setVisible(true);
        lobby.setVisible(false);
        start.setLayout(new BorderLayout(20, 20));
        start.setBackground(BACKGROUND_COLOR);
        start.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        /**
         * Titel panel
         */

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));

        // Welcome title
        JLabel welcomeLabel = new JLabel("TIC TAC TOE");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        /**
         * Create Server section
         */

        JLabel createLabel = new JLabel("Create Server");
        createLabel.setFont(new Font("Arial", Font.BOLD, 24));
        createLabel.setForeground(TEXT_COLOR);
        createLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Server ID input row
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.setMaximumSize(new Dimension(900, 80));
        
        JLabel serverCreatorLabel = new JLabel("Server ID (Format: 1111): ");
        serverCreatorLabel.setFont(new Font("Monospace", Font.PLAIN, 20));
        serverCreatorLabel.setForeground(TEXT_COLOR);
        
        JTextField serverIDInput = new JTextField("");
        serverIDInput.setFont(new Font("Arial", Font.PLAIN, 20));
        serverIDInput.setForeground(BLACK_COLOR);
        serverIDInput.setPreferredSize(new Dimension(300, 30));
        
        inputPanel.add(serverCreatorLabel);
        inputPanel.add(serverIDInput);

        // Bot selection row
        JPanel botPanel = new JPanel();
        botPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botPanel.setBackground(BACKGROUND_COLOR);
        botPanel.setMaximumSize(new Dimension(900, 80));
        
        JLabel botLabel = new JLabel("Should it be a lobby with a bot?: ");
        botLabel.setFont(new Font("Monospace", Font.PLAIN, 20));
        botLabel.setForeground(TEXT_COLOR);
        
        JRadioButton botYes = new JRadioButton("yes");
        botYes.setFont(new Font("Arial", Font.PLAIN, 20));
        botYes.setForeground(BLACK_COLOR);
        botYes.setPreferredSize(new Dimension(60, 30));

        JRadioButton botNo = new JRadioButton("no");
        botNo.setFont(new Font("Arial", Font.PLAIN, 20));
        botNo.setForeground(BLACK_COLOR);
        botNo.setPreferredSize(new Dimension(60, 30));

        ButtonGroup botChoiceGroup = new ButtonGroup();
        botChoiceGroup.add(botYes);
        botChoiceGroup.add(botNo);

        JButton submit = new JButton("Create Lobby!");
        submit.setFont(new Font("Arial", Font.PLAIN, 20));
        submit.setForeground(BLACK_COLOR);
        submit.setAlignmentX(Component.CENTER_ALIGNMENT);
        submit.setPreferredSize(new Dimension(60, 30));
        submit.addActionListener(e -> {
            if (!botYes.isSelected() && botNo.isSelected()) {
                try {
                    if (Player1.createJoinServer("Create Server", serverIDInput.getText()) == false) {
                        alert("Server ID already in use! Please choose another one.");
                        return;
                    } else {
                        frame.getContentPane().removeAll();
                        getLobbyFrame();
                        frame.revalidate();
                        frame.repaint();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            } else if (botYes.isSelected() && !botNo.isSelected()) {
                try {
                    if (Player1.createJoinServer("Create Server_bot", serverIDInput.getText()) == false) {
                        alert("Server ID already in use! Please choose another one.");
                        return;
                    } else {
                        frame.getContentPane().removeAll();
                        getLobbyFrame();
                        frame.revalidate();
                        frame.repaint();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }

        });

        botPanel.add(botLabel);
        botPanel.add(botYes);
        botPanel.add(botNo);

        headerPanel.add(welcomeLabel);
        headerPanel.add(Box.createVerticalStrut(20));
        headerPanel.add(createLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(inputPanel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(botPanel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(submit);

        /**
         * Seperator between sections
         */
        JSeparator separator = new JSeparator();
        Border blackBorder = BorderFactory.createMatteBorder(5, 0, 0, 0, Color.WHITE);
        separator.setBorder(blackBorder);

        headerPanel.add(Box.createVerticalStrut(50));
        headerPanel.add(separator);
        
        /**
         * Join Server section
         */
        JLabel joinLabel = new JLabel("Join Server");
        joinLabel.setFont(new Font("Arial", Font.BOLD, 24));
        joinLabel.setForeground(TEXT_COLOR);
        joinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel inputJoinPanel = new JPanel();
        inputJoinPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputJoinPanel.setBackground(BACKGROUND_COLOR);
        inputJoinPanel.setMaximumSize(new Dimension(900, 80));
        
        JLabel serverJoinerLabel = new JLabel("Server ID (Format: 1111): ");
        serverJoinerLabel.setFont(new Font("Monospace", Font.PLAIN, 20));
        serverJoinerLabel.setForeground(TEXT_COLOR);
        
        JTextField serverIDInputJoin = new JTextField("");
        serverIDInputJoin.setFont(new Font("Arial", Font.PLAIN, 20));
        serverIDInputJoin.setForeground(BLACK_COLOR);
        serverIDInputJoin.setPreferredSize(new Dimension(300, 30));
        
        inputJoinPanel.add(serverJoinerLabel);
        inputJoinPanel.add(serverIDInputJoin);

        JButton submitJoin = new JButton("Join Lobby!");
        submitJoin.setFont(new Font("Arial", Font.PLAIN, 20));
        submitJoin.setForeground(BLACK_COLOR);
        submitJoin.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitJoin.setPreferredSize(new Dimension(60, 30));
        submitJoin.addActionListener(e -> {
            try {
                    if (Player1.createJoinServer("Join Server", serverIDInputJoin.getText()) == false) {
                        alert("Server ID already in use! Please choose another one.");
                        return;
                    } else {
                        frame.getContentPane().removeAll();
                        getLobbyFrame();
                        frame.revalidate();
                        frame.repaint();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
        });

        headerPanel.add(Box.createVerticalStrut(50));
        headerPanel.add(joinLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(inputJoinPanel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(submitJoin);


        start.add(headerPanel, BorderLayout.NORTH);
        frame.add(start);
    }

    public static void getLobbyFrame() {
        start.setVisible(false);
        lobby.setVisible(true);
        // Set up main lobby panel with BorderLayout
        lobby.setLayout(new BorderLayout(20, 20));
        lobby.setBackground(BACKGROUND_COLOR);
        lobby.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));
        
        // Welcome title
        JLabel welcomeLabel = new JLabel("TIC TAC TOE");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel serverIDInput = new JLabel("Online Multiplayer Lobby");
        serverIDInput.setFont(new Font("Arial", Font.PLAIN, 20));
        serverIDInput.setForeground(new Color(178, 190, 195));
        serverIDInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(welcomeLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(serverIDInput);
        
        // Center panel with server info
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        // Server info card
        JPanel serverCard = new JPanel();
        serverCard.setLayout(new BoxLayout(serverCard, BoxLayout.Y_AXIS));
        serverCard.setBackground(CARD_COLOR);
        serverCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2, true),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        serverCard.setMaximumSize(new Dimension(500, 150));
        
        JLabel serverTitleLabel = new JLabel("Server Information");
        serverTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        serverTitleLabel.setForeground(ACCENT_COLOR);
        serverTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel serverLabel = new JLabel("Server ID: " + (Player1.serverID.isEmpty() ? "Connecting..." : Player1.serverID));
        serverLabel.setFont(new Font("Monospace", Font.PLAIN, 16));
        serverLabel.setForeground(TEXT_COLOR);
        serverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        serverCard.add(serverTitleLabel);
        serverCard.add(Box.createVerticalStrut(15));
        serverCard.add(serverLabel);
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(serverCard);
        centerPanel.add(Box.createVerticalGlue());
        
        // Status panel at bottom
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel statusLabel = new JLabel("Waiting for opponent...");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        statusLabel.setForeground(new Color(178, 190, 195));
        statusPanel.add(statusLabel);
        
        lobby.add(headerPanel, BorderLayout.NORTH);
        lobby.add(centerPanel, BorderLayout.CENTER);
        lobby.add(statusPanel, BorderLayout.SOUTH);
        
        frame.add(lobby);

        // Warte auf Gegner in einem separaten Thread
        new Thread(() -> {
            try {
                if (player != null && player.waitForMessage("serverport", "", "")[0].equals("serverport")){
                    SwingUtilities.invokeLater(() -> Player_GUI.playerFound());
                }
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> alert("Error waiting for opponent: " + e.getMessage()));
            }
        }).start();
    }

    public static void playerFound() {
        // Update center panel to show player found
        Component[] components = lobby.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && ((JPanel) comp).getLayout() instanceof BoxLayout) {
                JPanel centerPanel = (JPanel) comp;
                centerPanel.removeAll();
                
                // Player found card
                JPanel foundCard = new JPanel();
                foundCard.setLayout(new BoxLayout(foundCard, BoxLayout.Y_AXIS));
                foundCard.setBackground(CARD_COLOR);
                foundCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SUCCESS_COLOR, 3, true),
                    BorderFactory.createEmptyBorder(40, 50, 40, 50)
                ));
                foundCard.setMaximumSize(new Dimension(500, 250));
                
                JLabel foundLabel = new JLabel("Opponent Found!");
                foundLabel.setFont(new Font("Arial", Font.BOLD, 28));
                foundLabel.setForeground(TEXT_COLOR);
                foundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                JLabel readyLabel = new JLabel("Ready to start the game");
                readyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                readyLabel.setForeground(new Color(178, 190, 195));
                readyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                // Start button
                JButton startButton = new JButton("START GAME");
                startButton.setFont(new Font("Arial", Font.BOLD, 18));
                startButton.setForeground(Color.WHITE);
                startButton.setBackground(ACCENT_COLOR);
                startButton.setFocusPainted(false);
                startButton.setBorderPainted(false);
                startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                startButton.setMaximumSize(new Dimension(250, 50));
                startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                // Hover effect
                startButton.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        startButton.setBackground(new Color(79, 90, 230));
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        startButton.setBackground(ACCENT_COLOR);
                    }
                });
                
                foundCard.add(Box.createVerticalStrut(15));
                foundCard.add(foundLabel);
                foundCard.add(Box.createVerticalStrut(10));
                foundCard.add(readyLabel);
                foundCard.add(Box.createVerticalStrut(30));
                foundCard.add(startButton);
                
                centerPanel.add(Box.createVerticalGlue());
                centerPanel.add(foundCard);
                centerPanel.add(Box.createVerticalGlue());
                
                centerPanel.revalidate();
                centerPanel.repaint();
                break;
            }
        }
    }
    public static void alert(String message) {
        JOptionPane.showMessageDialog(frame, message, "Alert", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Setzt die Anwendung zurück und startet von vorne
     */
    public static void resetApplication() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Schließe alle Sockets
                if (Player1.socketSend != null && !Player1.socketSend.isClosed()) {
                    Player1.socketSend.close();
                }
                if (Player1.socketReceive != null && !Player1.socketReceive.isClosed()) {
                    Player1.socketReceive.close();
                }
                if (Player1.socketStart != null && !Player1.socketStart.isClosed()) {
                    Player1.socketStart.close();
                }

                // Setze Player-Variablen zurück
                Player1.serverPort = 0;
                Player1.serverID = "";
                Player1.enteredID = "";
                Player1.botServer = false;
                Player1.latch = new java.util.concurrent.CountDownLatch(1);

                // Erstelle neue Sockets
                Player1.socketSend = new java.net.DatagramSocket(Player1.serverPort);
                Player1.socketReceive = new java.net.DatagramSocket(Player1.clientPort);
                Player1.socketStart = new java.net.DatagramSocket(Player1.startPort);

                // Lösche alle Panels
                frame.getContentPane().removeAll();
                lobby.removeAll();
                gamePanel.removeAll();
                start.removeAll();

                // Erstelle neue Panel-Instanzen
                lobby = new JPanel();
                gamePanel = new JPanel();
                start = new JPanel();

                // Starte GUI von vorne
                getStartFrame();
                frame.revalidate();
                frame.repaint();

                // Erstelle neuen Player und initialisiere
                player = new Player1();
                new Thread(() -> {
                    try {
                        player.init();
                        player.latch.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                        alert("Error during reinitialization: " + e.getMessage());
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
                alert("Reset failed: " + e.getMessage());
            }
        });
    }
}
