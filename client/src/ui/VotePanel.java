package ui;

import data.Pallet;
import domain.GameClient;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VotePanel extends JPanel {
    private final GameClient client;
    
    private final JTextPane question;

    public VotePanel(GameClient client, Dimension size) {
        this.client = client;
        setPreferredSize(size);
        setLayout(null);
        setBackground(Pallet.CHAT.value());
        
        question = new JTextPane();
        question.setEditable(false);
        question.setForeground(Pallet.QUESTION.value());
        question.setBackground(Pallet.BACKGROUND.value());
        question.setBounds(10, 10, size.width - 20, 100);

        CustomButton yesButton = new CustomButton("YES", Pallet.YES_BUTTON.value(), 50, 150, size.width - 100, 50);
        CustomButton noButton = new CustomButton("NO", Pallet.NO_BUTTON.value(), 50, 220, size.width - 100, 50);
        CustomButton idkButton = new CustomButton("IDK", Pallet.IDK_BUTTON.value(), 50, 290, size.width - 100, 50);

        yesButton.addMouseListener(new ButtonClickListener("/vote YES"));
        noButton.addMouseListener(new ButtonClickListener("/vote NO"));
        idkButton.addMouseListener(new ButtonClickListener("/vote IDK"));

        add(question);
        add(yesButton);
        add(noButton);
        add(idkButton);
    }

    public void setQuestion(StringBuilder word, StringBuilder message, Color wordColor, Color questionColor) {
        question.setText("");
        StyledDocument doc = question.getStyledDocument();

        Style wordStyle = question.addStyle("WordStyle", null);
        StyleConstants.setForeground(wordStyle, wordColor);
        StyleConstants.setBold(wordStyle, true);

        Style questionStyle = question.addStyle("QuestionStyle", null);
        StyleConstants.setForeground(questionStyle, questionColor);

        try {
            doc.insertString(doc.getLength(), word + ": ", wordStyle);
            doc.insertString(doc.getLength(), message + "", questionStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private static class CustomButton extends JLabel {
        public CustomButton(String text, Color backgroundColor, int x, int y, int width, int height) {
            super(text, SwingConstants.CENTER);
            setOpaque(true);
            setBackground(backgroundColor);
            setForeground(Pallet.FOREGROUND.value());
            setFont(new Font("Serif", Font.BOLD, 20));
            setBounds(x, y, width, height);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    }

    private class ButtonClickListener extends MouseAdapter {
        private final String message;

        public ButtonClickListener(String message) {
            this.message = message;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            client.sendMessageToServer(message);
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