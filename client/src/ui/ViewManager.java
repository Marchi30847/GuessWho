package ui;

import javax.swing.*;

public class ViewManager {
    private final JFrame frame = new JFrame();

    private GameView gameView;
    private ConnectionView connectionView;

    public ViewManager() {
        configureFrame();
        configureConnectionView();
        configureGameView();
        showConnectionView();
    }

    private void configureFrame() {
        frame.setSize(1920, 1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void configureConnectionView() {
        connectionView = new ConnectionView();
    }

    private void configureGameView() {
        gameView = new GameView(frame.getSize());
    }

    public void showConnectionView() {
        if (gameView != null) frame.remove(gameView);
        frame.add(connectionView);
        frame.revalidate();
    }

    public void showGameView() {
        if (gameView != null) frame.remove(connectionView);
        frame.add(gameView);
        frame.revalidate();
    }

    public GameView getGameView() {return gameView;}
    public ConnectionView getConnectionView() {return connectionView;}
}
