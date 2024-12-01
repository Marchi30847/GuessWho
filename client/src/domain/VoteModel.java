package domain;

import data.GameClient;
import data.VoteModelInterface;

public class VoteModel implements VoteModelInterface {
    private final GameClient gameClient;
    public VoteModel(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @Override
    public void sendMessage(String message) {
        gameClient.sendMessageToServer(message);
    }
}
