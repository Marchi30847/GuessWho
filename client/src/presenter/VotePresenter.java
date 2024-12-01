package presenter;

import data.VoteModelInterface;
import ui.VoteView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VotePresenter {
    private final VoteView view;
    private final VoteModelInterface model;

    public VotePresenter(VoteView view, VoteModelInterface model) {
        this.view = view;
        this.model = model;
        initListeners();
    }

    private void initListeners() {
        view.addListenerToYesButton(new ButtonClickListener("/vote YES"));
        view.addListenerToNoButton(new ButtonClickListener("/vote NO"));
        view.addListenerToIdkButton(new ButtonClickListener("/vote IDK"));
    }

    public void addIncomingMessage(StringBuilder word, StringBuilder question, Color wordColor, Color questionColor) {
        view.setQuestion(word, question, wordColor, questionColor);
    }

    private class ButtonClickListener extends MouseAdapter {
        private final String message;

        public ButtonClickListener(String message) {
            this.message = message;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            model.sendMessage(message);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            ((JLabel) e.getSource()).setBackground(((JLabel) e.getSource()).getBackground().darker());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            ((JLabel) e.getSource()).setBackground(((JLabel) e.getSource()).getBackground().brighter());
        }
    }
}
