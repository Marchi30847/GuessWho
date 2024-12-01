package ui;

import data.Pallet;

import javax.swing.*;
import java.awt.*;

public class GameView extends JPanel {
    private final ChatView chatView;
    private final VoteView voteView;

    private final JPanel clientCard = new JPanel();
    private final JPanel currentClientCard = new JPanel();

    public GameView(Dimension size) {
        configure(size);
        configureClientCard(new Dimension(size.width, 200));
        configureCurrentClientCard(new Dimension(size.width, 200));

        chatView = new ChatView(new Dimension(size.width / 2, size.height - 200 * 2));
        voteView = new VoteView(new Dimension(size.width / 2, size.height - 200 * 2));

        addAll();
    }

    private void configure(Dimension size) {
        setPreferredSize(size);
        setLayout(new BorderLayout());
    }
    private void configureClientCard(Dimension size) {
        clientCard.setPreferredSize(size);
        clientCard.setBackground(Pallet.BACKGROUND.value());
    }

    private void configureCurrentClientCard(Dimension size) {
        currentClientCard.setPreferredSize(size);
        currentClientCard.setBackground(Pallet.BACKGROUND.value());
    }

    private void addAll() {
        add(currentClientCard, BorderLayout.NORTH);
        add(chatView, BorderLayout.WEST);
        add(voteView, BorderLayout.EAST);
        add(clientCard, BorderLayout.SOUTH);
    }

    public ChatView getChatView() {return chatView;}
    public VoteView getVoteView() {return voteView;}
}
