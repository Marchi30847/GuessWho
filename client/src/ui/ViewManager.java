package ui;

import domain.GameClient;

import javax.swing.*;

public class ViewManager {
    private JFrame frame;

    private final GameClient client;
    private ConnectionPanel connectionPanel;
    private GamePanel gamePanel;

    public ViewManager(GameClient client) {
        this.client = client;
        initFrame();
        initPanels();
        showConnectionPanel();
    }

    private void initFrame() {
        frame = new JFrame();
        frame.setSize(1920, 1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initPanels() {
        connectionPanel = new ConnectionPanel(client);
        gamePanel = new GamePanel(client, frame.getSize());
    }

    public void showConnectionPanel() {
        if (gamePanel != null) frame.remove(gamePanel);
        frame.add(connectionPanel);
        frame.revalidate();
    }

    public void showGamePanel() {
        if (gamePanel != null) frame.remove(connectionPanel);
        frame.add(gamePanel);
        frame.revalidate();
    }

    public GamePanel getGamePanel() {return gamePanel;}
    public ConnectionPanel getConnectionPanel() {return connectionPanel;}
}
