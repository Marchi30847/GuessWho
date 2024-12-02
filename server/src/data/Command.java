package data;

import domain.ClientHandler;
import domain.GameServer;

public interface Command {
    void execute(GameServer server, ClientHandler sender, StringBuilder message);
    String getCommand();
    String getDescription();
}
