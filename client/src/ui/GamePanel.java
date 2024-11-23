package ui;

import data.Pallet;
import domain.GameClient;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final GameClient client;

    private final  ChatPanel chatPanel;
    private final VotePanel votePanel;

    private JPanel clientCard;
    private JPanel currentClientCard;

    public GamePanel(GameClient client, Dimension size) {
        this.client = client;
        setPreferredSize(size);
        setLayout(new BorderLayout());

        int clientPanelHeight = 200;
        JPanel otherClient = new JPanel();
        otherClient.setPreferredSize(new Dimension(size.width, clientPanelHeight));
        otherClient.setBackground(Pallet.BACKGROUND.value());

        JPanel thisClient = new JPanel();
        thisClient.setPreferredSize(new Dimension(size.width, clientPanelHeight));
        thisClient.setBackground(Pallet.BACKGROUND.value());

        chatPanel = new ChatPanel(client, new Dimension(size.width / 2, size.height - clientPanelHeight * 2));
        votePanel = new VotePanel(client, new Dimension(size.width / 2, size.height - clientPanelHeight * 2));

        add(otherClient, BorderLayout.NORTH);
        add(chatPanel, BorderLayout.WEST);
        add(votePanel, BorderLayout.EAST);
        add(thisClient, BorderLayout.SOUTH);
    }

    public ChatPanel getChatPanel() {return chatPanel;}
    public VotePanel getVotePanel() {return votePanel;}
}
