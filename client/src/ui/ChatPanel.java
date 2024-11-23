package ui;

import data.Pallet;
import domain.GameClient;

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
        setPreferredSize(size);
        setLayout(new BorderLayout());

        chatBody = new JTextPane();
        chatBody.setBackground(Pallet.CHAT.value());
        chatBody.setEditable(false);

        clientMessage = new JTextField();
        clientMessage.setPreferredSize(new Dimension(size.width, 50));
        clientMessage.setBackground(Pallet.CHAT.value());
        clientMessage.setForeground(Pallet.MESSAGE.value());
        clientMessage.setFocusable(true);
        clientMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !clientMessage.getText().isEmpty()) {
                    client.sendMessageToServer(clientMessage.getText());
                    clientMessage.setText("");
                }
            }
        });

        scrollPane = new JScrollPane(chatBody);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
        add(clientMessage, BorderLayout.SOUTH);
    }

    public void addMessage(StringBuilder sender, StringBuilder message, Color senderColor, Color messageColor) {
        StyledDocument doc = chatBody.getStyledDocument();

        Style senderStyle = chatBody.addStyle("SenderStyle", null);
        StyleConstants.setForeground(senderStyle, senderColor);
        StyleConstants.setBold(senderStyle, true);

        Style messageStyle = chatBody.addStyle("MessageStyle", null);
        StyleConstants.setForeground(messageStyle, messageColor);

        try {
            doc.insertString(doc.getLength(), sender + ": ", senderStyle);
            doc.insertString(doc.getLength(), message + "\n", messageStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        chatBody.setCaretPosition(doc.getLength());
    }
}
