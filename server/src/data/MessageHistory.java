package data;

import java.util.ArrayList;

public interface MessageHistory {
    void addClientMessageToHistory(String command, String sender, String message);
    void addServerMessageToHistory(String command, String message);
    ArrayList<ChatMessage> getHistory();
}
