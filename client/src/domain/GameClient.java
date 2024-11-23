package domain;

import ui.ChatPanel;
import ui.ConnectionPanel;
import ui.ViewManager;
import data.Command;
import ui.VotePanel;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
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
        disconnect();
    }

    public boolean connect(String host, String port) {
        try {
            socket = new Socket(host, Integer.parseInt(port));
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(this).start();
            out.println(userName);
            return true;
        } catch (NumberFormatException e) {
            System.err.println("Not a number: " + e.getMessage());
            getConnectionPanel().setIncorrectHostPort();
        } catch (ConnectException e) {
            System.err.println("Server listens to a different port: " + e.getMessage());
            getConnectionPanel().setIncorrectHostPort();
        } catch (NoRouteToHostException e) {
            System.err.println("Unable to reach the specified host: " + e.getMessage());
            getConnectionPanel().setIncorrectHostName();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
            getConnectionPanel().setIncorrectHostName();
        } catch (IOException e) {
            System.err.println("I/O exception: " + e.getMessage());
            getConnectionPanel().setIncorrectHostPort();
            getConnectionPanel().setIncorrectHostName();
        }
        return false;
    }

    private void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Client " + userName + " disconnected.");
        } catch (IOException e) {
            System.err.println("Error closing connection for client: " + userName);
        }

    }

    public void respond(StringBuilder command, StringBuilder sender, StringBuilder message) {
        for (Command cmd : Command.values()) {
            if (cmd.getCommand().contentEquals(command)) cmd.execute(this, sender, message);
        }
    }

    public void sendMessageToServer(String message) {
        out.println(message);
    }

    public void handleMessageChat(StringBuilder sender, StringBuilder message, Color senderColor, Color messageColor) {
        getChatPanel().addMessage(sender, message, senderColor, messageColor);
    }

    public void handleMessageVote(StringBuilder word, StringBuilder question, Color wordColor, Color questionColor) {
        getVotePanel().setQuestion(word, question, wordColor, questionColor);
    }


    public String getUserName() {return userName;}
    public ViewManager getViewManager() {return viewManager;}
    public ChatPanel getChatPanel() {return viewManager.getGamePanel().getChatPanel();}
    public ConnectionPanel getConnectionPanel() {return viewManager.getConnectionPanel();}
    public VotePanel getVotePanel() {return viewManager.getGamePanel().getVotePanel();}
    public void setUserName(String userName) {this.userName = userName;}
}
