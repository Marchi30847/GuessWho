package domain;

import data.*;

import java.util.*;

public class ChatService {
    private final MessageSender messageSender;
    private final MessageHistory messageHistory;

    private final List<ClientHandler> clients;
    private final List<String> bannedPhrases;

    public ChatService(List<ClientHandler> clients, List<String> bannedPhrases, MessageSender messageSender, MessageHistory messageHistory) {
        this.clients = clients;
        this.bannedPhrases = bannedPhrases;
        this.messageSender = messageSender;
        this.messageHistory = messageHistory;
    }

    public void sendMessageToAllClients(ClientHandler sender, StringBuilder message) {
        String bannedPhrase = ServerUtils.containsBannedPhrase(message.toString(), bannedPhrases);
        if (bannedPhrase != null) {
            messageSender.sendServerNotification(
                    "/server",
                    sender,
                    ServerMessage.CONTAINS_BANNED_PHRASE,
                    bannedPhrase
            );
            return;
        }

        messageSender.sendClientMessage(
                "/client",
                sender,
                ServerUtils.getClientNames(clients),
                message.toString()
        );

        messageHistory.addClientMessageToHistory(
                "/client",
                sender.getClientName(),
                message.toString()
        );
    }

    public void sendMessageToClients(ClientHandler sender, ArrayList<String> targetClients, StringBuilder message) {
        String bannedPhrase = ServerUtils.containsBannedPhrase(message.toString(), bannedPhrases);
        if (bannedPhrase != null) {
            messageSender.sendServerNotification(
                    "/server",
                    sender,
                    ServerMessage.CONTAINS_BANNED_PHRASE,
                    bannedPhrase
            );
            return;
        }

        messageSender.sendClientMessage(
                "/client",
                sender,
                targetClients,
                message.insert(0, "(Personal) ").toString()
        );
    }

    public void sendMessageExceptClients(ClientHandler sender, ArrayList<String> nonTargetClients, StringBuilder message) {
        String bannedPhrase = ServerUtils.containsBannedPhrase(message.toString(), bannedPhrases);
        if (bannedPhrase != null) {
            messageSender.sendServerNotification(
                    "/server",
                    sender,
                    ServerMessage.CONTAINS_BANNED_PHRASE,
                    bannedPhrase
            );
            return;
        }

        ArrayList<String> targetClients = ServerUtils.getClientNames(clients);
        targetClients.removeAll(nonTargetClients);
        messageSender.sendClientMessage(
                "/client",
                sender,
                targetClients,
                message.insert(0, "(Personal) ").toString()
        );
    }

    public void sendClientList(ClientHandler sender) {
        messageSender.sendServerMessage(
                "/server",
                new ArrayList<>(Collections.singleton(sender.getClientName())),
                ServerUtils.getClientNames(clients).toString()
        );
    }

    //possibly send all commands
    public void sendHelpList(ClientHandler sender) {
        synchronized (clients) {
            for (Command cmd : ChatCommand.values()) {
                messageSender.sendServerMessage(
                        "/help",
                        new ArrayList<>(Collections.singletonList(sender.getClientName())),
                        cmd.getDescription()
                );
            }
            for (Command cmd : GameCommand.values()) {
                messageSender.sendServerMessage(
                        "/help",
                        new ArrayList<>(Collections.singletonList(sender.getClientName())),
                        cmd.getDescription()
                );
            }
        }
    }

    public void sendBannedPhrasesList(ClientHandler sender) {
        messageSender.sendServerMessage(
                "/ban",
                new ArrayList<>(Collections.singleton(sender.getClientName())),
                bannedPhrases.toString()
        );
    }

    public void sendClientConnectedMessage(ClientHandler sender) {
        messageSender.sendServerMessage(
                "/server",
                ServerUtils.getClientNames(clients),
                "client " + sender.getClientName() + " connected"
        );

        messageHistory.addServerMessageToHistory(
                "/server",
                "client " + sender.getClientName() + " connected"
        );
    }

    public void sendClientDisconnectedMessage(ClientHandler sender) {
        messageSender.sendServerMessage(
                "/server",
                ServerUtils.getClientNames(clients),
                "client " + sender.getClientName() + " disconnected"
        );

        messageHistory.addServerMessageToHistory(
                "/server",
                "client " + sender.getClientName() + " disconnected"
        );
    }


    public void sendServerNotification(ClientHandler sender, ServerMessage message, Object... arguments) {
        messageSender.sendServerNotification(
                "/server",
                sender,
                message,
                arguments
        );
    }

    public void sendChatHistory(ClientHandler sender) {
        for (ChatMessage chatMessage : messageHistory.getHistory()) {
            sender.getOut().println(ServerUtils.formatMessage(chatMessage));
        }
    }
}