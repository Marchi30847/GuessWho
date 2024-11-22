package client.data;

import client.domain.GameClient;

public enum Command {
    SERVER("/Server") {
        @Override
        public void execute(GameClient client, StringBuilder sender, StringBuilder message) {
            client.handleServerMessage(sender, message);
        }
    },
    CLIENT("/Client") {
        public void execute(GameClient client, StringBuilder sender, StringBuilder message) {
            client.handleClientMessage(sender, message);
        }
    };
    private final String command;

    Command(String command) {
        this.command = command;
    }

    public abstract void execute(GameClient client, StringBuilder sender, StringBuilder message);

    public String getCommand() {
        return command;
    }
}
