package domain;

import data.MessageHistory;
import data.MessageSender;
import data.ServerMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//add some messages to history
//add functionality if client leaves during their turn
//refactor GameMonitor with Executor functionality

public class GameService {
    private final MessageSender messageSender;
    private final MessageHistory messageHistory;

    private final List<ClientHandler> clients;

    private final Map<String, Integer> votes = new HashMap<>();

    private ClientHandler currentTurn;
    private int currentClientIndex;

    private ExecutorService gameMonitorExecutor = Executors.newSingleThreadExecutor();
    private boolean gameActive = false;
    private boolean everyoneHasWord = false;

    public GameService(List<ClientHandler> clients, MessageSender messageSender, MessageHistory messageHistory) {
        this.clients = clients;
        this.messageSender = messageSender;
        this.messageHistory = messageHistory;

        votes.put("YES", 0);
        votes.put("NO", 0);
        votes.put("IDK", 0);

        gameMonitorExecutor.submit(new GameMonitor());
    }

    public void onClientRemoved(ClientHandler removed) {
        if (currentTurn == removed) switchTurn();
    }

    public void sendGameStarted() {
        messageSender.sendServerMessage(
                "/server",
                ServerUtils.getClientNames(clients),
                "The game begins"
        );

        messageHistory.addServerMessageToHistory(
                "/server",
                "The game begins"
        );
    }

    public void sendQuestionMessage(ClientHandler sender, StringBuilder question) {
        if (!gameActive) {
            sendNotEnoughPlayers();
            return;
        }

        if (!everyoneHasWord) {
            sendNotEveryoneHasWord();
            return;
        }
        if (currentTurn != sender) {
            messageSender.sendServerNotification(
                    "/server",
                    sender,
                    ServerMessage.NOT_YOUR_TURN
            );
            return;
        }

        messageSender.sendClientMessage(
                "/question",
                sender,
                ServerUtils.getClientNames(clients),
                question.toString()
        );

        messageHistory.addClientMessageToHistory(
                "/question",
                sender.getClientName(),
                question.toString()
        );
    }

    public void sendAnswerForAQuestionMessage(ClientHandler currentTurn) {
        messageSender.sendServerNotification(
                "/server",
                currentTurn,
                ServerMessage.ANSWER_QUESTION,
                calculateVotes()
        );
    }

    //send a notification to the receiver
    //add a proper logic to handle cases when client is already given a word
    //handle a situation when a word is given to a non-existing client
    public void sendGiveWordMessage(ClientHandler sender, String receiver, StringBuilder word) {
        if (!gameActive) {
            sendNotEnoughPlayers();
            return;
        }

        if (sender.getClientName().equals(receiver)) {
            messageSender.sendServerNotification(
                    "/server",
                    sender,
                    ServerMessage.GIVE_YOURSELF
            );
            return;
        }

        if (!ServerUtils.getClientNames(clients).contains(receiver)) {
            messageSender.sendServerNotification(
                    "/server",
                    sender,
                    ServerMessage.INCORRECT_SYNTAX
            );
            return;
        }

        if(!hasWord(receiver)) giveWord(receiver, word.toString());
        else return;

        ArrayList<String> targetClients = ServerUtils.getClientNames(clients);
        targetClients.remove(receiver);
        messageSender.sendClientMessage(
                "/server",
                sender,
                targetClients,
                receiver + " is given a word \"" + word + "\" by " + sender.getClientName()
        );

    }

    public void addVoteFor(ClientHandler sender, String option) {
        if (!gameActive) {
            sendNotEnoughPlayers();
            return;
        }

        if (!everyoneHasWord) {
            sendNotEveryoneHasWord();
            return;
        }

        if (sender == currentTurn) {
            messageSender.sendServerNotification(
                    "/server",
                    sender,
                    ServerMessage.ANSWER_OWN_QUESTION
            );
            return;
        }

        if (sender.hasVoted()) {
            messageSender.sendServerNotification(
                    "/server",
                    sender,
                    ServerMessage.ALREADY_VOTED
            );
            return;
        }

        if (votes.containsKey(option)) {
            synchronized (votes) {
                votes.put(option, votes.get(option) + 1);
            }
            sender.setVoted(true);
            messageSender.sendServerNotification(
                    "/server",
                    sender,
                    ServerMessage.VOTE_COUNTED,
                    option
            );
        } else {
            messageSender.sendServerNotification(
                    "/server",
                    sender,
                    ServerMessage.INCORRECT_SYNTAX
            );
        }
    }

    public void startGame() {
        sendGameStarted();
        currentClientIndex = 0;
        currentTurn = clients.get(currentClientIndex);
    }


    //send these messages as notifications
    private void sendNotEnoughPlayers() {
        messageSender.sendServerMessage(
                "/server",
                ServerUtils.getClientNames(clients),
                "Wait for more players to join"
        );
    }

    private void sendNotEveryoneHasWord() {
        messageSender.sendServerMessage(
                "/server",
                ServerUtils.getClientNames(clients),
                "Everyone must be given a word"
        );
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
                if (client == currentTurn) continue;
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

    private void resetHasVoted() {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.setVoted(false);
            }
        }
    }

    private String calculateVotes() {
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
        return answer;
    }

    private void giveWord(String receiver, String word) {
        for (ClientHandler client : clients) {
            if (client.getClientName().equals(receiver)) client.setGivenWord(word);
        }
    }

    private boolean hasWord(String receiver) {
        for (ClientHandler client : clients) {
            if (client.getClientName().equals(receiver)) return client.getGivenWord() != null;
        }
        return false;
    }

    private void switchTurn() {
        synchronized (clients) {
            currentClientIndex = (currentClientIndex + 1) % clients.size();
            currentTurn = clients.get(currentClientIndex);
        }
    }


    private class GameMonitor implements Runnable {
        private boolean turnMessageSent = false;

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                gameActive = clients.size() > 1;
                everyoneHasWord = everyOneHasWord();

                if (gameActive && everyoneHasWord) {
                    if (!turnMessageSent) {
                        messageSender.sendServerNotification(
                                "/server",
                                currentTurn,
                                ServerMessage.YOUR_TURN
                        );
                        turnMessageSent = true;
                    }

                    if (everyOneVoted()) {
                        sendAnswerForAQuestionMessage(currentTurn);
                        resetVotes();
                        resetHasVoted();
                        switchTurn();
                        turnMessageSent = false;
                    }
                }
            }
        }
    }
}
