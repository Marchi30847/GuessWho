package server.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandler implements Runnable {
    private final GameServer server;
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private String clientName;
    private int clientPort;

    private boolean active = true;
    ClientHandler(GameServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.clientPort = clientSocket.getPort();

        try {
            out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        try {
            clientName = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            while (active) {
                String message = in.readLine();
                System.out.println(message);
                respond(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void respond(String message) {
        String command = "";
        String response = "";
        if (message.charAt(0) == '/') {
            int commandEnd = message.indexOf(' ');
            if (commandEnd != -1) {
                command = message.substring(0, commandEnd);
                response = message.substring(commandEnd + 1);
            }
        } else {
            command = "/all";
            response = message;
        }
        //append command in server class
        switch (command) {
            case "/all" -> {
                server.sendMessageToAllClients(this, "/Client [" + clientName + "] " + response);
            }
            case "/to" -> {
                ArrayList<String> clientNames = extractClientNames(message);
                if (clientNames != null) {
                    server.sendMessageToClients(this, clientNames, "/Client [" + clientName + "] " + response);
                }
            }
            case "/except" -> {
                ArrayList<String> clientNames = extractClientNames(message);
                if (clientNames != null) {
                    server.sendMessageExceptClients(this,  clientNames, "/Client [" + clientName + "] " + response);
                }
            }
            case "/banWords" -> {}
            default -> {server.sendUnknownCommandMessage(clientName);}
        }
    }
    private ArrayList<String> extractClientNames(String message) {
        int listBegin = message.indexOf("[");
        int listEnd = message.indexOf("]");
        if (listBegin >= listEnd) {
            server.sendUnknownCommandMessage(clientName);
            return null;
        }
        String recipientList = message.substring(listBegin + 1, listEnd);
        message = message.substring(listEnd);
        recipientList = recipientList.replace(" ", "");
        return new ArrayList<>(Arrays.asList(recipientList.split(",")));
    }

    public String getClientName() {return clientName;}
    public int getClientPort() {return clientPort;}
    public PrintWriter getOut() {return out;}
    public BufferedReader getIn() {return in;}
}
