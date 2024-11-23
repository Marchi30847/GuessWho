package server;

import server.domain.GameServer;

public class Main {
    public static void main(String[] args) {
        GameServer gameServer = new GameServer("/Users/yurimarchenko/IdeaProjects/GuessWho/src/server/data/ServerConfig.txt");
        gameServer.start();
    }
}
