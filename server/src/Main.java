import domain.GameServer;

public class Main {
    public static void main(String[] args) {
        GameServer gameServer = new GameServer("/Users/yurimarchenko/IdeaProjects/GuessWho/server/src/data/ServerConfig.txt");
        gameServer.start();
    }
}
