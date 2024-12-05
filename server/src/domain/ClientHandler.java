package domain;

import data.ChatCommand;
import data.GameCommand;
import data.ServerMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//try to reduce dependency with GameServer by returning some value if run() stops executing,
//so that it is handled directly in GameServer

public class ClientHandler implements Runnable {
    private final GameServer server;
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private String clientName;
    private final int clientPort;

    private String givenWord;
    private boolean voted = false;

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
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        handleClientConnection();
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                respond(new StringBuilder(message));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            disconnect();
        }
    }

    public void handleClientConnection() {
        while (server.clientHasRepeatingName(this)) {
            try {
                this.clientName = in.readLine();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        server.addClient(this);
    }

    public void respond(StringBuilder message) {
        if (message.charAt(0) != '/') {
            ChatCommand.ALL.execute(server, this, message.insert(0, "/all "));
            return;
        }
        for (ChatCommand command : ChatCommand.values()) {
            if (message.toString().startsWith(command.getCommand())) {
                command.execute(server, this, message);
                return;
            }
        }
        for (GameCommand command : GameCommand.values()) {
            if (message.toString().startsWith(command.getCommand())) {
                command.execute(server, this, message);
                return;
            }
        }
        server.getChatService().sendServerNotification(this, ServerMessage.UNKNOWN_COMMAND);
    }

    public void disconnect() {
        try {
            server.removeClient(this);
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            System.out.println("Client " + clientName + " disconnected.");
        } catch (IOException e) {
            System.err.println("Error closing connection for client: " + clientName);
        }
    }

    public String getClientName() {return clientName;}
    public String getGivenWord() {return givenWord;}
    public int getClientPort() {return clientPort;}
    public PrintWriter getOut() {return out;}
    public BufferedReader getIn() {return in;}


    public boolean hasVoted() {return voted;}
    public void setVoted(boolean voted) {this.voted = voted;}
    public void setGivenWord(String givenWord) {this.givenWord = givenWord;}
}
