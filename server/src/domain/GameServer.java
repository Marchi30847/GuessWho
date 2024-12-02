package domain;

import data.ChatCommand;
import data.Command;
import data.GameCommand;
import data.ServerMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class GameServer {
    private ServerSocket serverSocket;
    private final int port;
    private final String serverName;

    private final List<String>  bannedPhrases = new ArrayList<>();
    private final List<Command> commands      = new ArrayList<>();
    private final List<ClientHandler> clients = new ArrayList<>();

    private final ChatService chatService;
    private final GameService gameService;

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

        commands.addAll(List.of(ChatCommand.values()));
        commands.addAll(List.of(GameCommand.values()));

        chatService = new ChatService(clients, bannedPhrases, serverName);
        gameService = new GameService(clients);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
        }
        while (true) {
            try {
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
            chatService.sendChatHistory(client);
            chatService.sendClientConnectedMessage(client);
            chatService.sendClientList(client);
            chatService.sendHelpList(client);
        }
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client.getClientName());
            chatService.sendClientDisconnectedMessage(client);
        }
    }

    public boolean clientHasRepeatingName(ClientHandler client) {
        for (ClientHandler c : clients) {
            if (c.getClientName().equals(client.getClientName())) {
                chatService.sendServerNotification(client, ServerMessage.REPEATING_USERNAME);
                return true;
            }
        }
        return false;
    }

    public ChatService getChatService() {return chatService;}
    public GameService getGameService() {return gameService;}
}