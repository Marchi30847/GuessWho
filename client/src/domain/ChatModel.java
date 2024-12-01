package domain;

import data.ChatModelInterface;
import data.GameClient;

public class ChatModel implements ChatModelInterface {
    private final GameClient gameClient;
    public ChatModel(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @Override
    public void sendMessage(String message) {
        gameClient.sendMessageToServer(message);
    }
}
