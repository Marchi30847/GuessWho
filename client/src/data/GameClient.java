package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private String userName;

    private final ClientCallBack callBack;
    private final ExecutorService messageListenerExecutor = Executors.newSingleThreadExecutor();

    public GameClient(ClientCallBack callBack) {
        this.callBack = callBack;
    }

    public boolean connect(String host, String port) {
        try {
            socket = new Socket(host, Integer.parseInt(port));
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            messageListenerExecutor.execute(new MessageListener());
            out.println(userName);
            return true;
        } catch (NumberFormatException e) {
            System.err.println("Not a number: " + e.getMessage());
            callBack.onInvalidHostPort();
        } catch (ConnectException e) {
            System.err.println("Server listens to a different port: " + e.getMessage());
            callBack.onInvalidHostPort();
        } catch (NoRouteToHostException e) {
            System.err.println("Unable to reach the specified host: " + e.getMessage());
            callBack.onInvalidHostName();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
            callBack.onInvalidHostName();
        } catch (IOException e) {
            System.err.println("I/O exception: " + e.getMessage());
            callBack.onInvalidHostPort();
            callBack.onInvalidHostName();
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
        } finally {
            messageListenerExecutor.shutdownNow();
        }

    }

    public void sendMessageToServer(String message) {
        out.println(message);
    }

    public String getUserName() {return userName;}

    public void setUserName(String userName) {this.userName = userName;}




    private class MessageListener implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String command = in.readLine();
                    String sender = in.readLine();
                    String message = in.readLine();
                    System.out.println(command + " " + sender + " " + message);
                    callBack.onMessageReceived(new StringBuilder(command), new StringBuilder(sender), new StringBuilder(message));
                }
            } catch (IOException e) {
                System.err.println("I/O Exception: " + e.getMessage());
            } finally {
                disconnect();
            }
        }
    }
}
