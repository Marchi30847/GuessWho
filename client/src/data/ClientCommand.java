package data;

import presenter.GamePresenter;

public enum ClientCommand {
    SERVER("/server") {
        @Override
        public void execute(GamePresenter presenter, StringBuilder sender, StringBuilder message) {
            presenter.handleChatMessage(sender, message, Pallet.SERVER.value(), Pallet.MESSAGE.value());
        }
    },
    CLIENT("/client") {
        @Override
        public void execute(GamePresenter presenter, StringBuilder sender, StringBuilder message) {
            presenter.handleChatMessage(sender, message, Pallet.CLIENT.value(), Pallet.MESSAGE.value());
        }
    },
    HELP("/help") {
        @Override
        public void execute(GamePresenter presenter, StringBuilder sender, StringBuilder message) {
            presenter.handleChatMessage(sender, message, Pallet.SERVER.value(), Pallet.HELP.value());
        }
    },
    BAN("/ban") {
        @Override
        public void execute(GamePresenter presenter, StringBuilder sender, StringBuilder message) {
            presenter.handleChatMessage(sender, message, Pallet.SERVER.value(), Pallet.BAN.value());
        }
    },
    VOTE("/voted") {
        @Override
        public void execute(GamePresenter presenter, StringBuilder sender, StringBuilder message) {
            presenter.handleChatMessage(sender, message, Pallet.SERVER.value(), Pallet.VOTE.value());
        }
    },
    QUESTION("/question") {
        @Override
        public void execute(GamePresenter presenter, StringBuilder sender, StringBuilder message) {
            presenter.handleChatMessage(sender, message, Pallet.SERVER.value(), Pallet.QUESTION.value());
            presenter.handleVoteMessage(sender, message, Pallet.SERVER.value(), Pallet.QUESTION.value());
        }
    };

    private final String command;

    ClientCommand(String command) {
        this.command = command;
    }

    public abstract void execute(GamePresenter presenter, StringBuilder sender, StringBuilder message);

    public String getCommand() {
        return command;
    }
}
