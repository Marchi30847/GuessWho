package domain;

import data.ChatCommand;
import data.ChatMessage;
import data.ServerMessage;

import java.util.*;

public class ChatService {
    private final List<ClientHandler> clients;
    private final List<String> bannedPhrases;
    private final ArrayList<ChatMessage> chatHistory;

    private final String serverName;

    public ChatService(List<ClientHandler> clients, List<String> bannedPhrases, String serverName) {
        this.clients = clients;
        this.bannedPhrases = bannedPhrases;
        this.chatHistory = new ArrayList<>();
        this.serverName = serverName;
    }

    private void sendMessage(String command, ClientHandler sender, ArrayList<String> targetClients, StringBuilder message) {
        synchronized (clients) {
            String bannedPhrase = ServerUtils.containsBannedPhrase(message.toString(), bannedPhrases);
            if (bannedPhrase != null) {
                sendServerNotification(sender, ServerMessage.CONTAINS_BANNED_PHRASE, bannedPhrase);
                return;
            }

            sender.getOut().println(ServerUtils.formatMessage(command, "Me", message.toString()));

            String formattedMessage = ServerUtils.formatMessage(command, sender.getClientName(), message.toString());
            for (ClientHandler client : clients) {
                if (!client.equals(sender) && targetClients.contains(client.getClientName())) {
                    client.getOut().println(formattedMessage);
                }
            }
        }
    }

    public void sendMessageToAllClients(ClientHandler sender, StringBuilder message) {
        sendMessage("/client", sender,ServerUtils.getClientNames(clients), message);
        addMessageToHistory("/client", sender.getClientName(), message.toString());
    }

    public void sendMessageToClients(ClientHandler sender, ArrayList<String> targetClients, StringBuilder message) {
        sendMessage("/client", sender, targetClients, message.insert(0, "(Personal) "));
    }

    public void sendMessageExceptClients(ClientHandler sender, ArrayList<String> nonTargetClients, StringBuilder message) {
        ArrayList<String> targetClients = ServerUtils.getClientNames(clients);
        targetClients.removeAll(nonTargetClients);
        sendMessage("/client", sender, targetClients, message.insert(0, "(Personal) "));
    }

    public void sendClientList(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println(ServerUtils.formatMessage(
                            "/server",
                            serverName,
                            ServerUtils.getClientNames(clients).toString()
                    )
            );
        }
    }

    public void sendHelpList(ClientHandler sender) {
        synchronized (clients) {
            for (ChatCommand cmd : ChatCommand.values()) {
                sender.getOut().println(ServerUtils.formatMessage(
                                "/help",
                                serverName,
                                cmd.getDescription()
                        )
                );
            }
        }
    }

    public void sendBannedPhrasesList(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println(ServerUtils.formatMessage(
                            "/ban",
                            serverName,
                            bannedPhrases.toString()
                    )
            );
        }
    }

    public void sendClientConnectedMessage(ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.getOut().println(ServerUtils.formatMessage(
                                "/server",
                                serverName,
                                "client " + sender.getClientName() + " connected"
                        )
                );
            }
            addMessageToHistory("/server", serverName, "client " + sender.getClientName() + " connected");
        }
    }

    public void sendClientDisconnectedMessage(ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.getOut().println(ServerUtils.formatMessage(
                                "/server",
                                serverName,
                                "client " + sender.getClientName() + " disconnected"
                        )
                );
            }
            addMessageToHistory("/server", serverName, "client " + sender.getClientName() + " disconnected");
        }
    }


    public void sendServerNotification(ClientHandler sender, ServerMessage message, Object... arguments) {
        sender.getOut().println(ServerUtils.formatMessage(
                        "/server",
                        serverName,
                        message.getMessage(arguments)
                )
        );
    }

    public void sendChatHistory (ClientHandler sender) {
        synchronized (chatHistory) {
            for (ChatMessage chatMessage : chatHistory) {
                sender.getOut().println(ServerUtils.formatMessage(chatMessage));
            }
        }
    }

    private void addMessageToHistory(String command, String sender, String message) {
        synchronized (chatHistory) {
            chatHistory.add(new ChatMessage(command, sender, message));
        }
    }
}