package data;

import domain.ClientHandler;
import domain.GameServer;

import java.util.ArrayList;
import java.util.Arrays;

public enum Command {
    ALL("/all ") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            removePrefix(message);
            server.sendMessageToAllClients(sender, message);
        }

        @Override
        public String getDescription() {
            return "/all message: " +
                    "Sends a message to all connected clients.";
        }

        private void removePrefix(StringBuilder message) {
            message.delete(0, this.getCommand().length());
        }
    },
    TO("/to ") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            ArrayList<String> clientNames = extractClientNames(message);
            if (clientNames == null) server.sendIncorrectSyntaxMessage(sender);
            else {
                removePrefix(message);
                server.sendMessageToClients(sender, clientNames, message);
            }
        }

        @Override
        public String getDescription() {
            return "/to [names] message: " +
                    "Sends a private message to the specified clients.";
        }

        private void removePrefix(StringBuilder message) {
            message.delete(0, message.toString().indexOf(']') + 1);
        }
    },
    EXCEPT("/except ") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            ArrayList<String> clientNames = extractClientNames(message);
            if (clientNames == null) server.sendIncorrectSyntaxMessage(sender);
            else {
                removePrefix(message);
                server.sendMessageExceptClients(sender, clientNames, message);
            }
        }

        @Override
        public String getDescription() {
            return "/except [names] message: " +
                    "Sends a message to all clients except the specified ones.";
        }

        private void removePrefix(StringBuilder message) {
            message.delete(0, message.toString().indexOf(']') + 1);
        }
    },
    LIST("/list") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            server.sendClientList(sender);
        }

        @Override
        public String getDescription() {
            return "/list: " +
                    "Displays a list of all currently connected clients.";
        }
    },
    BAN("/ban") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            server.sendBannedPhrasesList(sender);
        }

        @Override
        public String getDescription() {
            return "/ban: " +
                    "Displays the list of words or phrases that are banned from being used in messages.";
        }
    },
    HELP("/help") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            server.sendHelpList(sender);
        }

        @Override
        public String getDescription() {
            return "/help: " +
                    "Provides a list of all available commands with their descriptions.";
        }
    };

    private final String command;

    Command(String command) {
        this.command = command;
    }

    public abstract void execute(GameServer server, ClientHandler sender, StringBuilder message);
    public abstract String getDescription();
    private static ArrayList<String> extractClientNames(StringBuilder message) {
        int listBegin = message.indexOf("[");
        int listEnd = message.indexOf("]");

        if (listBegin >= listEnd || message.charAt(listEnd + 1) != ' ') return null;

        String recipientList = message.substring(listBegin + 1, listEnd);
        recipientList = recipientList.replace(" ", "");
        return new ArrayList<>(Arrays.asList(recipientList.split(",")));
    }

    public String getCommand() {return command;}
}