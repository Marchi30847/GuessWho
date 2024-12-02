package data;

import domain.ClientHandler;

import java.util.ArrayList;

public interface MessageSender {
    void sendClientMessageTo(String command, ClientHandler sender, ArrayList<String> targetClients, StringBuilder message);
    void sendServerMessageTo(String command, ClientHandler sender, ArrayList<String> targetClients, StringBuilder message);
}
