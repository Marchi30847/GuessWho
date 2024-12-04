package data;

import domain.ClientHandler;
import domain.CommandUtils;
import domain.GameServer;

import java.util.ArrayList;

public enum GameCommand implements Command {
    GIVE("/give ") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            ArrayList<String> clientNames = CommandUtils.extractClientNames(message);
            if (clientNames == null || clientNames.size() != 1) server.getChatService().sendServerNotification(sender, ServerMessage.INCORRECT_SYNTAX);
            else {
                CommandUtils.removeList(message, ']');
                server.getGameService().sendGiveWordMessage(sender, clientNames.getFirst(), message);
            }
        }

        @Override
        public String getDescription() {
            return "/give [client] word: " +
                    "Gives a word to the specified client.";
        }
    },
    VOTE("/vote ") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            CommandUtils.removePrefix(message, getCommand());
            server.getGameService().addVoteFor(sender, message.toString());
        }

        @Override
        public String getDescription() {
            return "/vote option: " +
                    "Adds a vote for a specified option (YES, NO, IDK).";
        }
    },
    ASK("/ask ") {
        @Override
        public void execute(GameServer server, ClientHandler sender, StringBuilder message) {
            CommandUtils.removePrefix(message, getCommand());
            server.getGameService().sendQuestionMessage(sender, message);
        }

        @Override
        public String getDescription() {
            return "/ask question: " +
                    "Raises a question if it is your turn.";
        }
    };


    private final String command;

    GameCommand(String command) {
        this.command = command;
    }

    @Override
    public String getCommand() {return command;}
}
