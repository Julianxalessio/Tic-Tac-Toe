package Clients;
import javax.swing.*;
public class Player_GUI {
    public static JPanel lobby = new JPanel();
    public static JPanel gamePanel = new JPanel();
    public static JFrame frame = new JFrame("Tic Tac Toe Lobby");
    public static void main(String[] args) {
        getLobbyFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);  
        frame.setVisible(true);
        
        playerFound();
    }

    public static void getLobbyFrame() {
        lobby.add(new JLabel("Welcome to Tic Tac Toe Lobby \n"));
        lobby.add(new JLabel("Server ID:" + Player1.serverID +"\n"));
        frame.add(lobby);
    }

    public static void playerFound() {
        lobby.add(new JLabel("Player found!\n"));
        lobby.add(new JButton("Start Game"));
    }
}
