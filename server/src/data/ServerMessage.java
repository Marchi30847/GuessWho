package data;

public enum ServerMessage {
    UNKNOWN_COMMAND("Unknown command, for more info use " + ChatCommand.HELP.getCommand()),
    INCORRECT_SYNTAX("Incorrect syntax, for more info use " + ChatCommand.HELP.getCommand()),
    CONTAINS_BANNED_PHRASE("Your message contains banned phrase \"%s\", " +
            "for more info use " + ChatCommand.BAN.getCommand()),
    REPEATING_USERNAME("Your username is already in use, please use another one"),

    YOUR_TURN("It's your turn to ask questions"),
    NOT_YOUR_TURN("Wait for your turn"),

    ALREADY_VOTED("You have already voted"),
    VOTE_COUNTED("You successfully voted for %s"),
    WAIT_FOR_VOTES("Waiting for others to vote"),

    ANSWER_OWN_QUESTION("You cannot give answer to your own question"),
    ANSWER_QUESTION("The answer for your question is \"%s\""),

    ALREADY_GIVEN("The player %s already has word"),
    GIVE_YOURSELF("You cannot give a word to yourself");

    private final String template;

    ServerMessage(String template) {
        this.template = template;
    }

    public String getMessage(Object... arguments) {
        if (arguments.length > 0) {
            return String.format(template, arguments);
        }
        return template;
    }
}
