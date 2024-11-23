package domain;

import data.ChatMessage;
import data.Command;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class GameServer {
    private ServerSocket serverSocket;
    private final int port;
    private final String serverName;

    private final List<String> bannedPhrases = new ArrayList<>();
    private final List<ClientHandler> clients = new LinkedList<>();
    private final ArrayList<ChatMessage> chatHistory = new ArrayList<>();

    private ClientHandler currentTurn;
    private int currentClientIndex;

    private final Map<String, Integer> votes = new HashMap<>();

    public GameServer(String configPath) {
        try {
            BufferedReader configReader = new BufferedReader(new FileReader(configPath));
            port = Integer.parseInt(configReader.readLine());
            serverName = configReader.readLine();
            String bannedPhrase;
            while ((bannedPhrase = configReader.readLine()) != null) {
                bannedPhrases.add(bannedPhrase);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        votes.put("YES", 0);
        votes.put("NO", 0);
        votes.put("IDK", 0);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
        }
        while (true) {
            try {
                System.out.println("Waiting for clients...");
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(this, socket);
                new Thread(client).start();
                if (clients.size() == 2) startGame();
            } catch (IOException e) {
                System.err.println("Accept failed");
            }
        }
    }

    private void startGame() {
        currentClientIndex = 0;
        currentTurn = clients.get(currentClientIndex);
    }

    public void addClient(ClientHandler client) {
        synchronized (clients) {
            clients.add(client);
            sendChatHistory(client);
            sendClientConnectedMessage(client);
            sendClientList(client);
            sendHelpList(client);
        }
    }

    public boolean clientHasRepeatingName(ClientHandler client) {
        for (ClientHandler c : clients) {
            if (c.getClientName().equals(client.getClientName())) return true;
        }
        return false;
    }

    public void sendMessageToAllClients(ClientHandler sender, StringBuilder message) {
        synchronized (clients) {
            String bannedPhrase = containsBannedPhrase(message.toString());
            if (bannedPhrase != null) {
                sendContainsBannedPhraseMessage(sender, bannedPhrase);
                return;
            }
            for (ClientHandler client : clients) {
                if (client == sender) {
                    sender.getOut().println(formatMessage(
                                    "/client",
                                    "Me",
                                    message.toString()
                            )
                    );
                    continue;
                }
                client.getOut().println(formatMessage(
                                "/client",
                                sender.getClientName(),
                                message.toString()
                        )
                );
            }
            addMessageToHistory("/client", sender.getClientName(), message.toString());
        }
    }

    public void sendMessageToClients(ClientHandler sender, ArrayList<String> clientNames, StringBuilder message) {
        synchronized (clients) {
            String bannedPhrase = containsBannedPhrase(message.toString());
            if (bannedPhrase != null) {
                sendContainsBannedPhraseMessage(sender, bannedPhrase);
                return;
            }
            for (ClientHandler client : clients) {
                if (client == sender) {
                    sender.getOut().println(formatMessage(
                                    "/client",
                                    "Me",
                                    message.toString()
                            )
                    );
                    continue;
                }
                if (clientNames.contains(client.getClientName())) {
                    client.getOut().println(formatMessage(
                                    "/client",
                                     "(Personal) " + sender.getClientName(),
                                    message.toString()
                            )
                    );
                }
            }
        }
    }

    public void sendMessageExceptClients(ClientHandler sender, ArrayList<String> clientNames, StringBuilder message) {
        synchronized (clients) {
            String bannedPhrase = containsBannedPhrase(message.toString());
            if (bannedPhrase != null) {
                sendContainsBannedPhraseMessage(sender, bannedPhrase);
                return;
            }
            for (ClientHandler client : clients) {
                if (client == sender) {
                    sender.getOut().println(formatMessage(
                                    "/client",
                                    "Me",
                                    message.toString()
                            )
                    );
                    continue;
                }
                if (clientNames.contains(client.getClientName())) continue;
                client.getOut().println(formatMessage(
                                "/client",
                                "(Personal) " + sender.getClientName(),
                                message.toString()
                        )
                );
            }
        }
    }

    public void sendClientList(ClientHandler sender) {
        synchronized (clients) {
            StringBuilder clientNames = new StringBuilder();
            for (ClientHandler client : clients) {
                clientNames.append(client.getClientName());
                if (client != clients.getLast()) clientNames.append(", ");
            }
            sender.getOut().println(formatMessage(
                            "/server",
                            serverName,
                            "[" + clientNames + "]"
                    )
            );
        }
    }

    public void sendHelpList(ClientHandler sender) {
        synchronized (clients) {
            for (Command cmd : Command.values()) {
                sender.getOut().println(formatMessage(
                                "/help",
                                serverName,
                                cmd.getDescription()
                        )
                );
            }
        }
    }

    public void sendBannedPhrasesList(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/ban",
                            serverName,
                            bannedPhrases.toString()
                    )
            );
        }
    }

    public void sendClientConnectedMessage(ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.getOut().println(formatMessage(
                                "/server",
                                serverName,
                                "client " + sender.getClientName() + " connected"
                        )
                );
            }
            addMessageToHistory("/server", serverName, "client " + sender.getClientName() + " connected");
        }
    }

    public void sendClientDisconnectedMessage(ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.getOut().println(formatMessage(
                                "/server",
                                serverName,
                                "client " + sender.getClientName() + " disconnected"
                        )
                );
            }
            addMessageToHistory("/server", serverName, "client " + sender.getClientName() + " disconnected");
        }
    }

    public void sendChatHistory (ClientHandler sender) {
        synchronized (chatHistory) {
            for (ChatMessage chatMessage : chatHistory) {
                sender.getOut().println(formatMessage(chatMessage));
            }
        }
    }


    public void sendUnknownCommandMessage(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/server",
                            serverName,
                            "Unknown command, for more info use " + Command.HELP.getCommand()
                    )
            );
        }
    }

    public void sendIncorrectSyntaxMessage(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/server",
                            serverName,
                            "Incorrect syntax, for more info use " + Command.HELP.getCommand()
                    )
            );

        }
    }

    public void sendContainsBannedPhraseMessage(ClientHandler sender, String bannedPhrase) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/server",
                            serverName,
                            "Your message contains banned phrase \"" + bannedPhrase + "\", " +
                                     "for more info use " + Command.BAN.getCommand()
                    )
            );
        }
    }

    public void sendChangeNameMessage(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/server",
                            serverName,
                            "Your username is already in use, please use another one"
                    )
            );
        }
    }











    public void sendSuccessfullyVotedMessage(ClientHandler sender, String option) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/voted",
                            serverName,
                            "You successfully voted for " + option
                    )
            );
        }
    }

    public void sendAlreadyVotedMessage(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/server",
                            serverName,
                            "You have already voted"
                    )
            );
        }
    }

    //add sync on currentTurn
    //perhaps add to the history
    public void sendQuestionMessage(ClientHandler sender, StringBuilder question) {
        if (currentTurn != sender) {
            sendWaitForYourTurnMessage(sender);
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

    public void sendWaitForYourTurnMessage(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/server",
                            serverName,
                            "Wait for your turn"
                    )
            );
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

    public void sendYourTurnMessage(ClientHandler sender) {
        synchronized (clients) {
            sender.getOut().println(formatMessage(
                            "/server",
                            serverName,
                            "It's your turn to ask questions"
                    )
            );
        }
    }

    public void sendGiveAWordMessage(ClientHandler sender, String receiver, StringBuilder word) {
        if (sender.getClientName().equals(receiver)) {}
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











    private String formatMessage(String command, String sender, String message) {
       return command + "\n" + sender + "\n" + message;
    }

    private String formatMessage(ChatMessage chatMessage) {
        return chatMessage.command() + "\n" + chatMessage.sender() + "\n" + chatMessage.message();
    }

    public String containsBannedPhrase(String message) {
        message = message.toLowerCase();
        for (String bannedPhrase : bannedPhrases) {
            if(message.contains(bannedPhrase)) return bannedPhrase;
        }
        return null;
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
            sendClientDisconnectedMessage(client);
        }
    }

    private void addMessageToHistory(String command, String sender, String message) {
        synchronized (chatHistory) {
            chatHistory.add(new ChatMessage(command, sender, message));
        }
    }










    //handle a case when someone leaves during a voting process
    public void addVoteFor(ClientHandler sender, String option) {
        if (sender.hasVoted()) {
            sendAlreadyVotedMessage(sender);
            return;
        }
        if (clients.size() < 2) return;
        if (sender == currentTurn) return;

        if (votes.containsKey(option)) {
            synchronized (votes) {
                votes.put(option, votes.get(option) + 1);
            }
            sender.setVoted(true);
            sendSuccessfullyVotedMessage(sender, option);
        } else {
            sendIncorrectSyntaxMessage(sender);
        }
    }

    private void checkGameState(ClientHandler sender) {
        if (everyOneHasWord()) {
            if (everyOneVoted()) {
                sendAnswerForAQuestionMessage(sender);
                resetVotes();
                switchTurn();
                sendYourTurnMessage(currentTurn);
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
            for (String option : votes.keySet()) {
                votes.put(option, 0);
            }
        }
    }
    private void switchTurn() {
        synchronized (clients) {
            currentClientIndex = (currentClientIndex + 1) % clients.size();
            currentTurn = clients.get(currentClientIndex);
        }
    }
}