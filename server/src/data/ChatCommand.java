package data;

import domain.ClientHandler;
import domain.CommandUtils;
import domain.GameServer;

import java.util.ArrayList;

//replace GameServer parameter by some common interface of ChatService and GameService

public enum ChatCommand implements Command {
    ALL("/all ") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            CommandUtils.removePrefix(message, getCommand());
            server.getChatService().sendMessageToAllClients(sender, message);
        }

        @Override
        public String getDescription() {
            return "/all message: " +
                    "Sends a message to all connected clients.";
        }
    },
    TO("/to ") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            ArrayList<String> clientNames = CommandUtils.extractClientNames(message);
            if (clientNames == null) server.getChatService().sendServerNotification(sender, ServerMessage.INCORRECT_SYNTAX);
            else {
                CommandUtils.removeList(message, ']');
                server.getChatService().sendMessageToClients(sender, clientNames, message);
            }
        }

        @Override
        public String getDescription() {
            return "/to [names] message: " +
                    "Sends a private message to the specified clients.";
        }
    },
    EXCEPT("/except ") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            ArrayList<String> clientNames = CommandUtils.extractClientNames(message);
            if (clientNames == null) server.getChatService().sendServerNotification(sender, ServerMessage.INCORRECT_SYNTAX);
            else {
                CommandUtils.removeList(message, ']');
                server.getChatService().sendMessageExceptClients(sender, clientNames, message);
            }
        }

        @Override
        public String getDescription() {
            return "/except [names] message: " +
                    "Sends a message to all clients except the specified ones.";
        }
    },
    LIST("/list") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            server.getChatService().sendClientList(sender);
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
            server.getChatService().sendBannedPhrasesList(sender);
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
            server.getChatService().sendHelpList(sender);
        }

        @Override
        public String getDescription() {
            return "/help: " +
                    "Provides a list of all available commands with their descriptions.";
        }
    };


    private final String command;

    ChatCommand(String command) {
        this.command = command;
    }

    @Override
    public String getCommand() {return command;}
}