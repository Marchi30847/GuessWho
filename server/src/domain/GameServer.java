package domain;

import data.*;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//сделать возможность отправки многих сообщений за раз или
//добавить возможность обрабатывать сообщение с числом линий больше 3 или
//добавить доп синхронизацию на отправку композитных сообщений

//добавить ответы сервера всем пользователям в темплейты ответов
//move client disconnecting to GameServer

public class GameServer {
    private ServerSocket serverSocket;
    private final int port;
    private final String serverName;

    private final ExecutorService clientHandlerExecutor = Executors.newCachedThreadPool();
    private final List<ClientHandler> clients = new ArrayList<>();

    private final ChatService chatService;
    private final GameService gameService;

    public GameServer(String configPath) {
        List<String> bannedPhrases = new ArrayList<>();
        try {
            BufferedReader configReader = new BufferedReader(new FileReader(configPath));
            port = Integer.parseInt(configReader.readLine());
            serverName = configReader.readLine();
            String bannedPhrase;
            while ((bannedPhrase = configReader.readLine()) != null) {
                bannedPhrases.add(bannedPhrase);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading server configuration file: " + e.getMessage(), e);
        }

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize server socket on port " + port, e);
        }

        chatService = new ChatService(clients, bannedPhrases, createMessageSender(), createMessageHistory());
        gameService = new GameService(clients, createMessageSender(), createMessageHistory());

    }

    public void start() {
        try {
            while (true) {
                clientHandlerExecutor.submit(new ClientHandler(
                        this,
                        serverSocket.accept()
                ));
            }
        } catch (IOException e) {
            System.err.println("Accept failed");
        } finally {
            stop();
        }
    }

    public void stop() {
        try {
            serverSocket.close();

            synchronized (clients) {
                for (ClientHandler client : clients) {
                    client.disconnect();
                }
                clients.clear();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        } finally {
            clientHandlerExecutor.shutdownNow();
        }
    }

    //add a better condition to start the game
    public void addClient(ClientHandler client) {
        synchronized (clients) {
            clients.add(client);
            chatService.sendChatHistory(client);
            chatService.sendClientConnectedMessage(client);
            chatService.sendClientList(client);
            chatService.sendHelpList(client);
        }
        if (clients.size() == 2) gameService.startGame();
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            gameService.onClientRemoved(client);
            clients.remove(client);
            chatService.sendClientDisconnectedMessage(client);
        }
    }

    public boolean clientHasRepeatingName(ClientHandler client) {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                if (c.getClientName().equals(client.getClientName())) {
                    chatService.sendServerNotification(client, ServerMessage.REPEATING_USERNAME);
                    return true;
                }
            }
            return false;
        }
    }

    private MessageSender createMessageSender() {
        return new MessageSender() {
            @Override
            public void sendClientMessage(String command, ClientHandler sender, ArrayList<String> targetClients, String message) {
                synchronized (clients) {
                    sender.getOut().println(ServerUtils.formatMessage(command, "Me", message));
                    String formattedMessage = ServerUtils.formatMessage(command, sender.getClientName(), message);
                    for (ClientHandler client : clients) {
                        if (!client.equals(sender) && targetClients.contains(client.getClientName())) {
                            client.getOut().println(formattedMessage);
                        }
                    }
                }
            }

            @Override
            public void sendServerMessage(String command, ArrayList<String> targetClients, String message) {
                synchronized (clients) {
                    String formattedMessage = ServerUtils.formatMessage(command, serverName, message);
                    for (ClientHandler client : clients) {
                        if (targetClients.contains(client.getClientName())) {
                            client.getOut().println(formattedMessage);
                        }
                    }
                }
            }

            @Override
            public void sendServerNotification(String command, ClientHandler client, ServerMessage message, Object... arguments) {
                String formattedMessage = ServerUtils.formatMessage(command, serverName, message.getMessage(arguments));
                client.getOut().println(formattedMessage);
            }

        };
    }

    private MessageHistory createMessageHistory() {
        return new MessageHistory() {
            private final ArrayList<ChatMessage> history = new ArrayList<>();

            @Override
            public void addClientMessageToHistory(String command, String sender, String message) {
                synchronized (history) {
                    history.add(new ChatMessage(command, sender, message));
                }
            }

            @Override
            public void addServerMessageToHistory(String command, String message) {
                synchronized (history) {
                    history.add(new ChatMessage(command, serverName, message));
                }
            }

            @Override
            public ArrayList<ChatMessage> getHistory() {
                synchronized (history) {
                    return history;
                }
            }
        };
    }

    public ChatService getChatService() {return chatService;}
    public GameService getGameService() {return gameService;}
}