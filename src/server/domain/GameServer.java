package server.domain;

import server.data.ChatMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameServer {
    private ServerSocket serverSocket;
    private final int port = 5588;

    private final List<ClientHandler> clients = new LinkedList<>();

    private final ArrayList<ChatMessage> chatHistory = new ArrayList<>();

    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
        gameServer.start();
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
                synchronized (client) {
                    System.out.println("Client " + client.getClientName() + " connected");
                }
                synchronized (clients) {
                    clients.add(client);
                }
                new Thread(client).start();
            } catch (IOException e) {
                System.err.println("Accept failed");
            }
        }
    }

    public void sendMessageToAllClients(ClientHandler sender, String message) {
        synchronized (clients) {
            sender.getOut().println(message);
            for (ClientHandler client : clients) {
                if (client == sender) continue;
                client.getOut().println(message);
            }
        }
    }

    public void sendMessageToClients(ClientHandler sender, ArrayList<String> clientNames, String message) {
        synchronized (clients) {
            sender.getOut().println(message);
            for (ClientHandler client : clients) {
                if (client == sender) continue;
                if (clientNames.contains(client.getClientName())) client.getOut().println(message);
            }
        }
    }

    public void sendMessageExceptClients(ClientHandler sender, ArrayList<String> clientNames, String message) {
        synchronized (clients) {
            sender.getOut().println(message);
            for (ClientHandler client : clients) {
                if (client == sender) continue;
                if (clientNames.contains(client.getClientName())) continue;
                client.getOut().println(message);
            }
        }
    }

    public void sendUnknownCommandMessage(String clientName) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (clientName.equals(client.getClientName())) client.getOut().println("Unknown command");
            }
        }
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public void addMessage(ChatMessage message) {
        synchronized (chatHistory) {
            chatHistory.add(message);
        }
    }

    public ArrayList<ChatMessage> getChatHistory() {return chatHistory;}
}
