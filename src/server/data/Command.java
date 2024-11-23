package server.data;

import server.domain.ClientHandler;
import server.domain.GameServer;

import java.util.ArrayList;
import java.util.Arrays;

public enum Command {
    ALL("/all ") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            removePrefix(message);
            server.sendMessageToAllClients(sender, message);
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

        private void removePrefix(StringBuilder message) {
            message.delete(0, message.toString().indexOf(']') + 1);
        }
    },
    LIST("/list") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            server.sendClientList(sender);
        }
    },
    BAN("/ban") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            server.sendBannedPhrasesList(sender);
        }
    },
    HELP("/help") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            server.sendHelpList(sender);
        }
    };

    private final String command;

    Command(String command) {
        this.command = command;
    }

    public abstract void execute(GameServer server, ClientHandler sender, StringBuilder message);
    private static ArrayList<String> extractClientNames(StringBuilder message) {
        int listBegin = message.indexOf("[");
        int listEnd = message.indexOf("]");
        if (listBegin >= listEnd) {
            return null;
        }
        String recipientList = message.substring(listBegin + 1, listEnd);
        recipientList = recipientList.replace(" ", "");
        return new ArrayList<>(Arrays.asList(recipientList.split(",")));
    }

    public String getCommand() {return command;}
}
