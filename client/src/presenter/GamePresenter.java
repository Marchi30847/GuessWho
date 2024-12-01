package presenter;

import data.ClientCallBack;
import data.ClientCommand;
import data.GameClient;
import domain.ChatModel;
import domain.VoteModel;
import ui.ConnectionView;
import ui.GameView;
import ui.ViewManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePresenter {
    private final ViewManager view;
    private final GameClient client;
    private final ConnectionView connectionView;
    private final GameView gameView;

    private final ChatPresenter chatPresenter;
    private final VotePresenter votePresenter;

    public GamePresenter(ViewManager view) {
        this.view = view;
        client = new GameClient(createClientCallBack());

        connectionView = view.getConnectionView();
        gameView = view.getGameView();

        chatPresenter = new ChatPresenter(gameView.getChatView(), new ChatModel(client));
        votePresenter = new VotePresenter(gameView.getVoteView(), new VoteModel(client));

        initListeners();
    }

    private ClientCallBack createClientCallBack() {
        return new ClientCallBack() {
            @Override
            public void onInvalidHostName() {
                connectionView.setIncorrectHostName();
            }

            @Override
            public void onInvalidHostPort() {
                connectionView.setIncorrectHostPort();
            }

            @Override
            public void onMessageReceived(StringBuilder command, StringBuilder sender, StringBuilder message) {
                for (ClientCommand cmd : ClientCommand.values()) {
                    if (cmd.getCommand().contentEquals(command)) cmd.execute(GamePresenter.this, sender, message);
                }
            }
        };
    }

    private void initListeners() {
        initConnectionListeners();
    }

    private void initConnectionListeners() {
        KeyListener keyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    connectToServer();
                }
            }
        };

        connectionView.addListenersToUserName(
                new FieldChosenListener(connectionView.getUserNameField()),
                keyListener
        );
        connectionView.addListenersToHostName(
                new FieldChosenListener(connectionView.getHostNameField()),
                keyListener
        );
        connectionView.addListenersToHostPort(
                new FieldChosenListener(connectionView.getHostPortField()),
                keyListener
        );
    }

    public void handleChatMessage(StringBuilder sender, StringBuilder message, Color senderColor, Color messageColor) {
        chatPresenter.addIncomingMessage(sender, message, senderColor, messageColor);
    }

    public void handleVoteMessage(StringBuilder word, StringBuilder question, Color wordColor, Color questionColor) {
        votePresenter.addIncomingMessage(word, question, wordColor, questionColor);
    }

    public void connectToServer() {
        String userName = connectionView.getUserNameField().getText();
        String hostName = connectionView.getHostNameField().getText();
        String hostPort = connectionView.getHostPortField().getText();

        client.setUserName(userName);
        if (client.connect(hostName, hostPort)) {
            view.showGameView();
        }
    }

    private class FieldChosenListener extends MouseAdapter {
        private final JTextField textField;

        public FieldChosenListener(JTextField textField) {
            this.textField = textField;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            connectionView.setDefaultTextIfEmpty(textField);
            if (e.getClickCount() == 2) textField.setText("");
        }
    }
}
