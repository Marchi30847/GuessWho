package client.data;

import client.domain.GameClient;

public enum Command {
    SERVER("/Server") {
        @Override
        public void execute(GameClient client, StringBuilder message) {
            client.handleServerMessage(message);
        }
    },
    CLIENT("/Client") {
        public void execute(GameClient client, StringBuilder message) {
            client.handleClientMessage(message);
        }
    };
    private final String command;

    Command(String command) {
        this.command = command;
    }

    public abstract void execute(GameClient client, StringBuilder message);

    public String getCommand() {
        return command;
    }
}
