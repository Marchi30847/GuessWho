package domain;

import data.ServerMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameService {
    private final List<ClientHandler> clients;
    private final Map<String, Integer> votes = new HashMap<>();

    private ClientHandler currentTurn;
    private int currentClientIndex;

    public GameService(List<ClientHandler> clients) {
        this.clients = clients;
        votes.put("YES", 0);
        votes.put("NO", 0);
        votes.put("IDK", 0);
    }

    //add sync on currentTurn
    //perhaps add to the history
    public void sendQuestionMessage(ClientHandler sender, StringBuilder question) {
        if (currentTurn != sender) {
            sendServerNotification(sender, ServerMessage.NOT_YOUR_TURN);
            return;
        }
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client == sender) {
                    sender.getOut().println(formatMessage(
                                    "/question",
                                    "Me",
                                    question.toString()
                            )
                    );
                    continue;
                }
                client.getOut().println(formatMessage(
                                "/question",
                                sender.getClientName(),
                                question.toString()
                        )
                );
            }
        }
    }

    public void sendAnswerForAQuestionMessage(ClientHandler sender) {
        String answer = "";
        synchronized (votes) {
            int maxVotes = 0;
            for (String key : votes.keySet()) {
                if (votes.get(key) > maxVotes) maxVotes = votes.get(key);
            }
            for (String key : votes.keySet()) {
                if (votes.get(key) == maxVotes) {
                    answer = key;
                    break;
                }
            }
        }

        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/server",
                            serverName,
                            "The answer for your question is " + answer
                    )
            );
        }
    }

    public void sendGiveAWordMessage(ClientHandler sender, String receiver, StringBuilder word) {
        if (sender.getClientName().equals(receiver)) {
        }
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client.getClientName().equals(receiver)) {
                    sender.getOut().println(formatMessage(
                                    "/server",
                                    serverName,
                                    "You are given a word " + word.toString() +
                                            " by " + sender.getClientName()
                            )
                    );
                    continue;
                }
                sender.getOut().println(formatMessage(
                                "/server",
                                serverName,
                                sender.getClientName() + "gives a word " + word.toString() +
                                        " to " + receiver
                        )
                );
            }
        }
    }

    //handle a case when someone leaves during a voting process
    public void addVoteFor(ClientHandler sender, String option) {
        if (sender.hasVoted()) {
            sendServerNotification(sender, ServerMessage.ALREADY_VOTED);
            return;
        }
        if (clients.size() < 2) return;
        if (sender == currentTurn) return;

        if (votes.containsKey(option)) {
            synchronized (votes) {
                votes.put(option, votes.get(option) + 1);
            }
            sender.setVoted(true);
            sendServerNotification(sender,  ServerMessage.VOTE_COUNTED, option);
        } else {
            sendServerNotification(sender, ServerMessage.INCORRECT_SYNTAX);
        }
    }

    private void checkGameState(ClientHandler sender) {
        if (everyOneHasWord()) {
            if (everyOneVoted()) {
                sendAnswerForAQuestionMessage(sender);
                resetVotes();
                switchTurn();
                sendServerNotification(currentTurn, ServerMessage.YOUR_TURN);
            }
        } else {

        }
    }

    private boolean everyOneVoted() {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client == currentTurn) continue;
                if (!client.hasVoted()) return false;
            }
        }
        return true;
    }

    private boolean everyOneHasWord() {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client.getGivenWord() == null) return false;
            }
        }
        return true;
    }

    private void resetVotes() {
        synchronized (votes) {
            for (String option : votes.keySet())
                votes.put(option, 0);
        }
    }

    private void startGame() {
        currentClientIndex = 0;
        currentTurn = clients.get(currentClientIndex);
    }

    private void switchTurn() {
        synchronized (clients) {
            currentClientIndex = (currentClientIndex + 1) % clients.size();
            currentTurn = clients.get(currentClientIndex);
        }
    }
}
