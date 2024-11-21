package client.domain;

import client.ui.ChatPanel;
import client.ui.ViewManager;
import client.data.Command;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClient implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private ViewManager viewManager;

    private String userName;

    public GameClient() {
        viewManager = new ViewManager(this);
    }

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(this).start();
            out.println(userName);
            return true;
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
        } catch (IOException e) {
            System.err.println("I/O exception: " + e.getMessage());
        }
        return false;
    }

    public void sendMessageToServer(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = in.readLine();
                System.out.println(message);
                respond(new StringBuilder(message));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void respond(StringBuilder message) {
        for (Command command : Command.values()) {
            if (message.toString().startsWith(command.getCommand())) {
                command.execute(this, message);
                return;
            }
        }
    }

    public void handleServerMessage(StringBuilder message) {
        normaliseMessage(message);
        getChatPanel().addMessage("Server", message.toString(), Color.YELLOW);
    }

    public void handleClientMessage(StringBuilder message) {
        normaliseMessage(message);
        getChatPanel().addMessage("Client", message.toString(), Color.BLUE);
    }

    private void normaliseMessage(StringBuilder message) {
        for (Command command : Command.values()) {
            if (message.toString().startsWith(command.getCommand())) message.delete(0, command.getCommand().length());
        }
    }

    public String getUserName() {
        return userName;
    }

    public ViewManager getViewManager() {
        return viewManager;
    }

    public ChatPanel getChatPanel() {
        return viewManager.getGamePanel().getChatPanel();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static void main(String[] args) {
        new GameClient();
    }
}
