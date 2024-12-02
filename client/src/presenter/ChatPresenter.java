package presenter;

import data.ChatModelInterface;
import ui.ChatView;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatPresenter {
    private final ChatView view;
    private final ChatModelInterface model;

    public ChatPresenter(ChatView view, ChatModelInterface model) {
        this.view = view;
        this.model = model;
        initListeners();
    }

    private void initListeners() {
        view.addSendMessageListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                String clientMessageText = view.getClientMessageText();
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !clientMessageText.isEmpty()) {
                        model.sendMessage(clientMessageText);
                        view.setClientMessageText("");
                }
            }
        });

    }

    public void addIncomingMessage(StringBuilder sender, StringBuilder message, Color senderColor, Color messageColor) {
        view.addMessage(sender, message, senderColor, messageColor);
    }
}
