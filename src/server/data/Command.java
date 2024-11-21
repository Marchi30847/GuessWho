package server.data;

import client.domain.GameClient;
import server.domain.ClientHandler;
import server.domain.GameServer;

import java.util.ArrayList;
import java.util.Arrays;

public enum Command {
    ALL("/all") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            server.sendMessageToAllClients(sender, message);
        }
    },
    TO("/to") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            ArrayList<String> clientNames = extractClientNames(message);
            if (clientNames == null) server.sendIncorrectSyntaxMessage(sender);
            else server.sendMessageToClients(sender, clientNames, message);
        }
    },
    EXCEPT("/except") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            ArrayList<String> clientNames = extractClientNames(message);
            if (clientNames == null) server.sendIncorrectSyntaxMessage(sender);
            else server.sendMessageExceptClients(sender, clientNames, message);
        }
    },
    LIST("/list") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            server.sendClientList(sender);
        }
    },
    BAN("/banWords") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {

        }
    },
    HELP("/help") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            server.sendHelp(sender);
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
