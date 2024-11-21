package server.domain;

import server.data.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
                respond(new StringBuilder(message));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void respond(StringBuilder message) {
        if (message.charAt(0) != '/') {
            Command.ALL.execute(server, this, message.insert(0, "/all"));
            return;
        }
        for (Command command : Command.values()) {
            if (message.toString().startsWith(command.getCommand())) {
                command.execute(server, this, message);
                return;
            }
        }
        server.sendUnknownCommandMessage(this);
    }

    public String getClientName() {return clientName;}
    public int getClientPort() {return clientPort;}
    public PrintWriter getOut() {return out;}
    public BufferedReader getIn() {return in;}
}
