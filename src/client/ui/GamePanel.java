package client.ui;

import client.data.Pallet;
import client.domain.GameClient;

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
        this.setPreferredSize(size);
        setLayout(new BorderLayout());

        int clientPanelHeight = 200;
        JPanel otherClient = new JPanel();
        otherClient.setPreferredSize(new Dimension(size.width, clientPanelHeight));
        otherClient.setBackground(Pallet.BACKGROUND.value());

        JPanel thisClient = new JPanel();
        thisClient.setPreferredSize(new Dimension(size.width, clientPanelHeight));
        thisClient.setBackground(Pallet.BACKGROUND.value());

        this.chatPanel = new ChatPanel(client, new Dimension(size.width / 2, size.height - clientPanelHeight * 2));
        this.votePanel = new VotePanel(new Dimension(size.width / 2, size.height - clientPanelHeight * 2));

        this.add(otherClient, BorderLayout.NORTH);
        this.add(chatPanel, BorderLayout.WEST);
        this.add(votePanel, BorderLayout.EAST);
        this.add(thisClient, BorderLayout.SOUTH);
    }

    public ChatPanel getChatPanel() {return chatPanel;}
}
