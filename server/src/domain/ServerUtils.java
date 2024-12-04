package domain;

import data.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ServerUtils {

    private ServerUtils() {}

    public static ArrayList<String> getClientNames(List<ClientHandler> clients) {
        ArrayList<String> clientNames = new ArrayList<>();
        for (ClientHandler client : clients)
            clientNames.add(client.getClientName());
        return clientNames;
    }

    public static String formatMessage(String command, String sender, String message) {
        return command + "\n" + sender + "\n" + message;
    }

    public static String formatMessage(ChatMessage chatMessage) {
        return chatMessage.command() + "\n" + chatMessage.sender() + "\n" + chatMessage.message();
    }

    public static String containsBannedPhrase(String message,  List<String> bannedPhrases) {
        message = message.toLowerCase();
        for (String bannedPhrase : bannedPhrases) {
            if(message.contains(bannedPhrase)) return bannedPhrase;
        }
        return null;
    }
}
