package client.domain;

import client.ui.ViewManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClient {
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
            return true;
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
        } catch (IOException e) {
            System.err.println("I/O exception: " + e.getMessage());
        }
        return false;
    }

    public String getUserName() {return userName;}
    public ViewManager getViewManager() {return viewManager;}
    public void setUserName(String userName) {this.userName = userName;}

    public static void main(String[] args) {
        GameClient gameClient = new GameClient();
    }
}
