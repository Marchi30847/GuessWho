package client.ui;

import client.data.Pallet;
import client.domain.GameClient;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    GameClient client;

    ChatPanel chatPanel;
    VotePanel votePanel;

    JPanel clientCard;
    JPanel currentClientCard;

    public GamePanel(GameClient client) {
        this.client = client;


        setLayout(new BorderLayout());

        JPanel otherClient = new JPanel();
        otherClient.setPreferredSize(new Dimension(getWidth(), 240));
        otherClient.setBackground(Pallet.BACKGROUND.value());

        JPanel thisClient = new JPanel();
        thisClient.setPreferredSize(new Dimension(getWidth(), 240));
        thisClient.setBackground(Pallet.BACKGROUND.value());

        JPanel interaction = new JPanel();
        interaction.setPreferredSize(new Dimension(getWidth(), 600));
        interaction.setBackground(Pallet.FOREGROUND.value());
        interaction.setLayout(new BorderLayout());
        chatPanel = new ChatPanel();
        votePanel = new VotePanel();

        //interaction.add(chatPanel, BorderLayout.WEST);
        //interaction.add(votePanel, BorderLayout.EAST);


        add(otherClient, BorderLayout.NORTH);
        add(interaction, BorderLayout.CENTER);
        add(thisClient, BorderLayout.SOUTH);
    }
}
