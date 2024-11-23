package client.domain;

import client.data.Pallet;
import client.ui.ChatPanel;
import client.ui.ConnectionPanel;
import client.ui.ViewManager;
import client.data.Command;

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

    private final ViewManager viewManager;

    private String userName;

    public GameClient() {
        viewManager = new ViewManager(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String command = in.readLine();
                String sender = in.readLine();
                String message = in.readLine();
                if (command == null || sender == null || message == null) break;
                System.out.println(command + " " + sender + " " + message);
                respond(new StringBuilder(command), new StringBuilder(sender), new StringBuilder(message));
            } catch (IOException e) {
                System.err.println("I/O Exception: " + e.getMessage());
            }
        }
    }

    public boolean connect(String host, String port) {
        try {
            socket = new Socket(host, Integer.parseInt(port));
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(this).start();
            out.println(userName);
            return true;
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
            getConnectionPanel().setIncorrectHostName();
        } catch (NumberFormatException e) {
            System.err.println("Not a number: " + e.getMessage());
            getConnectionPanel().setIncorrectHostPort();
        } catch (IOException e) {
            System.err.println("I/O exception: " + e.getMessage());
            switch (e.getMessage()) {
                case "No route to host" -> getConnectionPanel().setIncorrectHostName();
                case "Connection refused" -> getConnectionPanel().setIncorrectHostPort();
                default -> {
                    getConnectionPanel().setIncorrectHostName();
                    getConnectionPanel().setIncorrectHostPort();
                }
            }
        }
        return false;
    }

    public void sendMessageToServer(String message) {
        out.println(message);
    }

    public void respond(StringBuilder command, StringBuilder sender, StringBuilder message) {
        for (Command cmd : Command.values()) {
            if (cmd.getCommand().contentEquals(command)) cmd.execute(this, sender, message);
        }
    }

    public void handleServerMessage(StringBuilder sender, StringBuilder message) {
        getChatPanel().addMessage(sender, message,
                Pallet.SERVER.value(), Pallet.MESSAGE.value());
    }

    public void handleClientMessage(StringBuilder sender, StringBuilder message) {
        getChatPanel().addMessage(sender, message,
                Pallet.CLIENT.value(), Pallet.MESSAGE.value());
    }

    public String getUserName() {return userName;}
    public ViewManager getViewManager() {return viewManager;}
    public ChatPanel getChatPanel() {return viewManager.getGamePanel().getChatPanel();}
    public ConnectionPanel getConnectionPanel() {return viewManager.getConnectionPanel();}
    public void setUserName(String userName) {this.userName = userName;}
}
