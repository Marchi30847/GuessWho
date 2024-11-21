package server.domain;

import server.data.Command;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//remove clients if they disconnect
//move prefix deletion to Command
public class GameServer {
    private ServerSocket serverSocket;
    private final int port;
    private final String name;

    private final List<String> bannedWords = new ArrayList<>();
    private final List<ClientHandler> clients = new LinkedList<>();
    private final ArrayList<StringBuilder> chatHistory = new ArrayList<>();

    public GameServer(String configPath) {
        try {
            BufferedReader configReader = new BufferedReader(new FileReader(configPath));
            port = Integer.parseInt(configReader.readLine());
            name = configReader.readLine();
            String bannedWord;
            while ((bannedWord = configReader.readLine()) != null) {
                bannedWords.add(bannedWord);
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
                System.out.println("Client " + client.getClientName() + " connected");
                handleClientConnection(client);
            } catch (IOException e) {
                System.err.println("Accept failed");
            }
        }
    }

    public void handleClientConnection(ClientHandler client) {
        new Thread(client).start();
        synchronized (clients) {
            clients.add(client);
            sendBanned(client);
            sendHelp(client);
        }
    }

    public void sendMessageToAllClients(ClientHandler sender, StringBuilder message) {
        normaliseMessage(message, false);
        addMessage(message);
        synchronized (clients) {
            sender.getOut().println(message);
            for (ClientHandler client : clients) {
                if (client == sender) continue;
                client.getOut().println(message);
            }
        }
    }

    public void sendMessageToClients(ClientHandler sender, ArrayList<String> clientNames, StringBuilder message) {
        normaliseMessage(message, true);
        synchronized (clients) {
            sender.getOut().println(message);
            for (ClientHandler client : clients) {
                if (client == sender) continue;
                if (clientNames.contains(client.getClientName())) client.getOut().println(message);
            }
        }
    }

    public void sendMessageExceptClients(ClientHandler sender, ArrayList<String> clientNames, StringBuilder message) {
        normaliseMessage(message, true);
        synchronized (clients) {
            sender.getOut().println(message);
            for (ClientHandler client : clients) {
                if (client == sender) continue;
                if (clientNames.contains(client.getClientName())) continue;
                client.getOut().println(message);
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
            sender.getOut().println("/Server" + "[" + clientNames + "]");
        }
    }

    public void sendHelp(ClientHandler sender) {
        synchronized (clients) {
            StringBuilder commandList = new StringBuilder();
            for (Command cmd : Command.values()) {
                commandList.append(cmd.getCommand());
                if (cmd != Command.values()[Command.values().length - 1]) commandList.append(", ");
            }
            sender.getOut().println("/Server" + "[" + commandList + "]");
        }
    }

    public void sendBanned(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println("/Server" + bannedWords);
        }
    }

    public void sendUnknownCommandMessage(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println("/Server Unknown command, for more info use /help");
        }
    }

    public void sendIncorrectSyntaxMessage(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println("/Server Incorrect syntax, for more info use /help");
        }
    }

    private void normaliseMessage(StringBuilder message, boolean list) {
        if (list) message.delete(0, message.toString().indexOf(']') + 1);
        else {
            for (Command command : Command.values()) {
                if (message.toString().startsWith(command.getCommand())) message.delete(0, command.getCommand().length());
            }
        }
        message.insert(0, "/Client");
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public void addMessage(StringBuilder message) {
        synchronized (chatHistory) {
            chatHistory.add(message);
        }
    }

    public ArrayList<StringBuilder> getChatHistory() {return chatHistory;}

    public static void main(String[] args) {
        GameServer gameServer = new GameServer("src/server/data/ServerConfig.txt");
        gameServer.start();
    }
}
