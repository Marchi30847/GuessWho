package data;

import domain.ClientHandler;

import java.util.ArrayList;

public interface MessageSender {
    void sendClientMessage(String command, ClientHandler sender, ArrayList<String> targetClients, String message);
    void sendServerMessage(String command, ArrayList<String> targetClients, String message);
    void sendServerNotification(String command, ClientHandler client, ServerMessage message, Object... arguments);
}
