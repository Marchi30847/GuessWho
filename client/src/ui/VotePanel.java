package ui;

import data.Pallet;
import domain.GameClient;

import javax.swing.*;
import java.awt.*;

public class VotePanel extends JPanel {
    private final GameClient client;

    public VotePanel(GameClient client, Dimension size) {
        this.client = client;
        this.setPreferredSize(size);
        this.setLayout(null);
        this.setBackground(Pallet.CHAT.value());
    }
}
