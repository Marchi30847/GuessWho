package data;

import domain.GameClient;

public enum Command {
    SERVER("/server") {
        @Override
        public void execute(GameClient client, StringBuilder sender, StringBuilder message) {
            client.handleMessage(sender, message, Pallet.SERVER.value(), Pallet.MESSAGE.value());
        }
    },
    CLIENT("/client") {
        @Override
        public void execute(GameClient client, StringBuilder sender, StringBuilder message) {
            client.handleMessage(sender, message, Pallet.CLIENT.value(), Pallet.MESSAGE.value());
        }
    },
    HELP("/help") {
        @Override
        public void execute(GameClient client, StringBuilder sender, StringBuilder message) {
            client.handleMessage(sender, message, Pallet.SERVER.value(), Pallet.HELP.value());
        }
    },
    BAN("/ban") {
        @Override
        public void execute(GameClient client, StringBuilder sender, StringBuilder message) {
            client.handleMessage(sender, message, Pallet.SERVER.value(), Pallet.BAN.value());
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
