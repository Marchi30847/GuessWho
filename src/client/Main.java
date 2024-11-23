package client;

import client.domain.GameClient;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameClient());
    }
}
