package ui;

import data.Pallet;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyListener;

public class ChatView extends JPanel {
    private final JTextPane chatBody = new JTextPane();
    private final JTextField clientMessage =  new JTextField();
    private final JScrollPane scrollPane = new JScrollPane(chatBody);

    public ChatView(Dimension size) {
        configure(size);
        configureChatBody();
        configureClientMessage(size);
        configureScrollPane();
        addAll();
    }

    private void configure(Dimension size) {
        setPreferredSize(size);
        setLayout(new BorderLayout());
    }

    private void configureChatBody() {
        chatBody.setBackground(Pallet.CHAT.value());
        chatBody.setEditable(false);
    }

    private void configureClientMessage(Dimension size) {
        clientMessage.setPreferredSize(new Dimension(size.width, 50));
        clientMessage.setBackground(Pallet.CHAT.value());
        clientMessage.setForeground(Pallet.MESSAGE.value());
        clientMessage.setFocusable(true);
    }

    private void configureScrollPane() {
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    private void addAll() {
        add(scrollPane, BorderLayout.CENTER);
        add(clientMessage, BorderLayout.SOUTH);
    }

    public void addSendMessageListener(KeyListener keyListener) {clientMessage.addKeyListener(keyListener);}

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

    public String getClientMessageText() {return this.clientMessage.getText();}

    public void setClientMessageText(String clientMessageText) {this.clientMessage.setText(clientMessageText);}
}
