package client.ui;

import client.data.Pallet;
import client.domain.GameClient;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatPanel extends JPanel {
    private final GameClient client;

    private final JTextPane chatBody;
    private final JTextField clientMessage;
    private final JScrollPane scrollPane;

    public ChatPanel(GameClient client, Dimension size) {
        this.client = client;
        this.setPreferredSize(size);
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        this.chatBody = new JTextPane();
        this.chatBody.setBackground(Pallet.CHAT.value());
        this.chatBody.setEditable(false);

        this.clientMessage = new JTextField();
        this.clientMessage.setPreferredSize(new Dimension(size.width, 50));
        this.clientMessage.setBackground(Pallet.CHAT.value());
        this.clientMessage.setFocusable(true);
        this.clientMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !clientMessage.getText().isEmpty()) {
                    client.sendMessageToServer(clientMessage.getText());
                    clientMessage.setText("");
                }
            }
        });

        this.scrollPane = new JScrollPane(chatBody);
        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        this.add(this.scrollPane, BorderLayout.CENTER);
        this.add(this.clientMessage, BorderLayout.SOUTH);
    }

    public void addMessage(String sender, String message, Color color) {
        StyledDocument doc = chatBody.getStyledDocument();

        Style senderStyle = chatBody.addStyle("SenderStyle", null);
        StyleConstants.setForeground(senderStyle, color);
        StyleConstants.setBold(senderStyle, true);

        Style messageStyle = chatBody.addStyle("MessageStyle", null);
        StyleConstants.setForeground(messageStyle, Color.BLACK);

        try {
            doc.insertString(doc.getLength(), sender + ": ", senderStyle);
            doc.insertString(doc.getLength(), message + "\n", messageStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        chatBody.setCaretPosition(doc.getLength());
    }
}
