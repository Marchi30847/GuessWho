package ui;

import data.Pallet;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseListener;

public class VoteView extends JPanel {
    private final JTextPane question = new JTextPane();

    private CustomButton yesButton;
    private CustomButton noButton;
    private CustomButton idkButton;

    public VoteView(Dimension size) {
        configure(size);
        configureQuestion(size);
        configureYesButton(size);
        configureNoButton(size);
        configureIdkButton(size);
        addAll();
    }

    private void configure(Dimension size) {
        setPreferredSize(size);
        setLayout(null);
        setBackground(Pallet.CHAT.value());
    }

    private void configureQuestion(Dimension size) {
        question.setEditable(false);
        question.setForeground(Pallet.QUESTION.value());
        question.setBackground(Pallet.BACKGROUND.value());
        question.setBounds(10, 10, size.width - 20, 100);
    }

    private void configureYesButton(Dimension size) {
        yesButton = new CustomButton("YES", Pallet.YES_BUTTON.value(), 50, 150, size.width - 100, 50);
    }

    private void configureNoButton(Dimension size) {
        noButton = new CustomButton("NO", Pallet.NO_BUTTON.value(), 50, 220, size.width - 100, 50);
    }

    private void configureIdkButton(Dimension size) {
        idkButton = new CustomButton("IDK", Pallet.IDK_BUTTON.value(), 50, 290, size.width - 100, 50);
    }

    private void addAll() {
        add(question);
        add(yesButton);
        add(noButton);
        add(idkButton);
    }

    public void addListenerToYesButton(MouseListener mouseListener) {yesButton.addMouseListener(mouseListener);}
    public void addListenerToNoButton(MouseListener mouseListener) {noButton.addMouseListener(mouseListener);}
    public void addListenerToIdkButton(MouseListener mouseListener) {idkButton.addMouseListener(mouseListener);}

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
}