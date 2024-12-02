package ui;

import data.Pallet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConnectionView extends JPanel {
    private final JPanel inputPanel = new JPanel();
    private final JPanel placeHolder = new JPanel();

    private final JLabel titleLabel = new JLabel();
    private final JLabel userNameLabel = new JLabel();
    private final JLabel hostNameLabel = new JLabel();
    private final JLabel hostPortLabel = new JLabel();

    private final JTextField userName = new JTextField();
    private final JTextField hostName = new JTextField();
    private final JTextField hostPort = new JTextField();

    public ConnectionView() {
        configure();
        configureInputPanel();
        configureTitleLabel();
        configureUsernameLabel();
        configureHostNameLabel();
        configureHostPortLabel();
        configureUsername();
        configureHostName();
        configureHostPort();
        configurePlaceHolder();
        addAll();
    }

    private void configure() {
        setLayout(new BorderLayout());
        setBackground(Pallet.BACKGROUND.value());
    }

    private void configureInputPanel() {
        inputPanel.setLayout(new GridLayout(6, 1, 10, 10));
        inputPanel.setBackground(Pallet.BACKGROUND.value());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
    }

    private void configureTitleLabel() {
        titleLabel.setText("JOIN A NEW GAME");
        addFormattedFont(titleLabel);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setPreferredSize(new Dimension(getWidth(), 150));
    }

    private void configureUsernameLabel() {
        userNameLabel.setText("Enter Username: ");
        addFormattedFont(userNameLabel);
    }

    private void configureHostNameLabel() {
        hostNameLabel.setText("Enter Host Ip: ");
        addFormattedFont(hostNameLabel);
    }

    private void configureHostPortLabel() {
        hostPortLabel.setText("Enter Host Port: ");
        addFormattedFont(hostPortLabel);
    }

    private void configureUsername() {
        userName.setText("here");
        addFormattedFont(userName);
        userName.setBackground(Pallet.BACKGROUND.value());
    }

    private void configureHostName() {
        hostName.setText("here");
        addFormattedFont(hostName);
        hostName.setBackground(Pallet.BACKGROUND.value());
    }

    private void configureHostPort() {
        hostPort.setText("here");
        addFormattedFont(hostPort);
        hostPort.setBackground(Pallet.BACKGROUND.value());
    }

    private void configurePlaceHolder() {
        placeHolder.setPreferredSize(new Dimension(getWidth(), 150));
        placeHolder.setBackground(Pallet.BACKGROUND.value());
    }

    private void addAll() {
        add(titleLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(placeHolder, BorderLayout.SOUTH);
        addToInputPanel();
    }

    private void addToInputPanel() {
        inputPanel.add(userNameLabel);
        inputPanel.add(userName);
        inputPanel.add(hostNameLabel);
        inputPanel.add(hostName);
        inputPanel.add(hostPortLabel);
        inputPanel.add(hostPort);
    }

    private static void addFormattedFont(Component component) {
        component.setForeground(Pallet.FOREGROUND.value());
        component.setFont(new Font("Serif", Font.BOLD, 60));
    }

    public void addListenersToUserName(MouseListener mouseListener, KeyListener keyListener) {
        userName.addMouseListener(mouseListener);
        userName.addKeyListener(keyListener);
    }

    public void addListenersToHostName(MouseListener mouseListener, KeyListener keyListener) {
        hostName.addMouseListener(mouseListener);
        hostName.addKeyListener(keyListener);
    }

    public void addListenersToHostPort(MouseListener mouseListener, KeyListener keyListener) {
        hostPort.addMouseListener(mouseListener);
        hostPort.addKeyListener(keyListener);
    }

    public JTextField getUserNameField() {return userName;}
    public JTextField getHostNameField() {return hostName;}
    public JTextField getHostPortField() {return hostPort;}

    public void setDefaultTextIfEmpty(JTextField focused) {
        if (userName.getText().isEmpty() && userName != focused) userName.setText("here");
        if (hostName.getText().isEmpty() && hostName != focused) hostName.setText("here");
        if (hostPort.getText().isEmpty() && hostPort != focused) hostPort.setText("here");
    }
    public void setIncorrectHostName() {hostName.setText("Enter a valid host name");}
    public void setIncorrectHostPort() {hostPort.setText("Enter a valid host port");}
}
