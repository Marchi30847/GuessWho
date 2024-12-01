package ui;

import javax.swing.*;

public class ViewManager {
    private final JFrame frame = new JFrame();

    private ConnectionView connectionPanel;
    private GameView gamePanel;

    public ViewManager() {
        initFrame();
        initPanels();
        showConnectionView();
    }

    private void initFrame() {
        frame.setSize(1920, 1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initPanels() {
        connectionPanel = new ConnectionView();
        gamePanel = new GameView(frame.getSize());
    }

    public void showConnectionView() {
        if (gamePanel != null) frame.remove(gamePanel);
        frame.add(connectionPanel);
        frame.revalidate();
    }

    public void showGameView() {
        if (gamePanel != null) frame.remove(connectionPanel);
        frame.add(gamePanel);
        frame.revalidate();
    }

    public GameView getGameView() {return gamePanel;}
    public ConnectionView getConnectionView() {return connectionPanel;}
}
