package client.domain;

import client.ui.ChatPanel;
import client.ui.ViewManager;

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
                respond(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void respond(String message) {
        int commandEnd = message.indexOf(' ');
        String command = message.substring(0, commandEnd);
        switch (command) {
            case "/Server" -> {
                getChatPanel().addMessage("Server: ", message.substring(commandEnd + 1), Color.BLUE);
            }
            case "/Client" -> {
                getChatPanel().addMessage("Client: ", message.substring(commandEnd + 1), Color.YELLOW);
            }
        }
    }

    public String getUserName() {return userName;}
    public ViewManager getViewManager() {return viewManager;}
    public ChatPanel getChatPanel() {return viewManager.getGamePanel().getChatPanel();}

    public void setUserName(String userName) {this.userName = userName;}

    public static void main(String[] args) {
        new GameClient();
    }
}
