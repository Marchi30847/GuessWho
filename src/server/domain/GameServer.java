package server.domain;

import server.data.ChatMessage;
import server.data.Command;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameServer {
    private ServerSocket serverSocket;
    private final int port;
    private final String serverName;

    private final List<String> bannedPhrases = new ArrayList<>();
    private final List<ClientHandler> clients = new LinkedList<>();
    private final ArrayList<ChatMessage> chatHistory = new ArrayList<>();

    public GameServer(String configPath) {
        try {
            BufferedReader configReader = new BufferedReader(new FileReader(configPath));
            port = Integer.parseInt(configReader.readLine());
            serverName = configReader.readLine();
            String bannedPhrase;
            while ((bannedPhrase = configReader.readLine()) != null) {
                bannedPhrases.add(bannedPhrase);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
        }
        while (true) {
            try {
                System.out.println("Waiting for clients...");
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(this, socket);
                new Thread(client).start();
            } catch (IOException e) {
                System.err.println("Accept failed");
            }
        }
    }

    public void addClient(ClientHandler client) {
        synchronized (clients) {
            clients.add(client);
            sendChatHistory(client);
            sendClientConnectedMessage(client);
            sendClientList(client);
            sendHelpList(client);
        }
    }

    public boolean clientHasRepeatingName(ClientHandler client) {
        for (ClientHandler c : clients) {
            if (c.getClientName().equals(client.getClientName())) return true;
        }
        return false;
    }

    public void sendMessageToAllClients(ClientHandler sender, StringBuilder message) {
        synchronized (clients) {
            String bannedPhrase = containsBannedPhrase(message.toString());
            if (bannedPhrase != null) {
                sendContainsBannedPhraseMessage(sender, bannedPhrase);
                return;
            }
            for (ClientHandler client : clients) {
                if (client == sender) {
                    sender.getOut().println(formatMessage(
                                    "/Client",
                                    "Me",
                                    message.toString()
                            )
                    );
                    continue;
                }
                client.getOut().println(formatMessage(
                                "/Client",
                                sender.getClientName(),
                                message.toString()
                        )
                );
            }
            addMessageToHistory("/Client", sender.getClientName(), message.toString());
        }
    }

    public void sendMessageToClients(ClientHandler sender, ArrayList<String> clientNames, StringBuilder message) {
        synchronized (clients) {
            String bannedPhrase = containsBannedPhrase(message.toString());
            if (bannedPhrase != null) {
                sendContainsBannedPhraseMessage(sender, bannedPhrase);
                return;
            }
            for (ClientHandler client : clients) {
                if (client == sender) {
                    sender.getOut().println(formatMessage(
                                    "/Client",
                                    "Me",
                                    message.toString()
                            )
                    );
                    continue;
                }
                if (clientNames.contains(client.getClientName())) {
                    client.getOut().println(formatMessage(
                                    "/Client",
                                     "(Personal) " + sender.getClientName(),
                                    message.toString()
                            )
                    );
                }
            }
        }
    }

    public void sendMessageExceptClients(ClientHandler sender, ArrayList<String> clientNames, StringBuilder message) {
        synchronized (clients) {
            String bannedPhrase = containsBannedPhrase(message.toString());
            if (bannedPhrase != null) {
                sendContainsBannedPhraseMessage(sender, bannedPhrase);
                return;
            }
            for (ClientHandler client : clients) {
                if (client == sender) {
                    sender.getOut().println(formatMessage(
                                    "/Client",
                                    "Me",
                                    message.toString()
                            )
                    );
                    continue;
                }
                if (clientNames.contains(client.getClientName())) continue;
                client.getOut().println(formatMessage(
                                "/Client",
                                "(Personal) " + sender.getClientName(),
                                message.toString()
                        )
                );
            }
        }
    }

    public void sendClientList(ClientHandler sender) {
        synchronized (clients) {
            StringBuilder clientNames = new StringBuilder();
            for (ClientHandler client : clients) {
                clientNames.append(client.getClientName());
                if (client != clients.getLast()) clientNames.append(", ");
            }
            sender.getOut().println(formatMessage(
                            "/Server",
                            serverName,
                            "[" + clientNames + "]"
                    )
            );
        }
    }

    public void sendHelpList(ClientHandler sender) {
        synchronized (clients) {
            StringBuilder commandList = new StringBuilder();
            for (Command cmd : Command.values()) {
                commandList.append(cmd.getCommand());
                if (cmd != Command.values()[Command.values().length - 1]) commandList.append(", ");
            }
            sender.getOut().println(formatMessage(
                            "/Server",
                            serverName,
                            "[" + commandList + "]"
                    )
            );
        }
    }

    public void sendBannedPhrasesList(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/Server",
                            serverName,
                            bannedPhrases.toString()
                    )
            );
        }
    }

    public void sendClientConnectedMessage(ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.getOut().println(formatMessage(
                                "/Server",
                                serverName,
                                "client " + sender.getClientName() + " connected"
                        )
                );
            }
            addMessageToHistory("/Server", serverName, "client " + sender.getClientName() + " connected");
        }
    }

    public void sendClientDisconnectedMessage(ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.getOut().println(formatMessage(
                                "/Server",
                                serverName,
                                "client " + sender.getClientName() + " disconnected"
                        )
                );
            }
            addMessageToHistory("/Server", serverName, "client " + sender.getClientName() + " disconnected");
        }
    }

    public void sendChatHistory (ClientHandler sender) {
        synchronized (chatHistory) {
            for (ChatMessage chatMessage : chatHistory) {
                sender.getOut().println(formatMessage(chatMessage));
            }
        }
    }


    public void sendUnknownCommandMessage(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/Server",
                            serverName,
                            "Unknown command, for more info use " + Command.HELP.getCommand()
                    )
            );
        }
    }

    public void sendIncorrectSyntaxMessage(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/Server",
                            serverName,
                            "Incorrect syntax, for more info use " + Command.HELP.getCommand()
                    )
            );

        }
    }

    public void sendContainsBannedPhraseMessage(ClientHandler sender, String bannedPhrase) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/Server",
                            serverName,
                            "Your message contains banned phrase \"" + bannedPhrase + "\", " +
                                     "for more info use " + Command.BAN.getCommand()
                    )
            );
        }
    }

    public void sendChangeNameMessage(ClientHandler client) {
        client.getOut().println(formatMessage(
                        "/Server",
                        serverName,
                        "Your username is already in use, please use another one"
                )
        );
    }


    private String formatMessage(String command, String sender, String message) {
       return command + "\n" + sender + "\n" + message;
    }

    private String formatMessage(ChatMessage chatMessage) {
        return chatMessage.command() + "\n" + chatMessage.sender() + "\n" + chatMessage.message();
    }

    public String containsBannedPhrase(String message) {
        message = message.toLowerCase();
        for (String bannedPhrase : bannedPhrases) {
            if(message.contains(bannedPhrase)) return bannedPhrase;
        }
        return null;
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
            sendClientDisconnectedMessage(client);
        }
    }

    private void addMessageToHistory(String command, String sender, String message) {
        synchronized (chatHistory) {
            chatHistory.add(new ChatMessage(command, sender, message));
        }
    }
}