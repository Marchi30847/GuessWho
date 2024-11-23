package client.ui;

import client.data.Pallet;
import client.domain.GameClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConnectionPanel extends JPanel {
    private final GameClient client;

    private final JTextField userName;
    private final JTextField hostName;
    private final JTextField hostPort;

    public ConnectionPanel(GameClient client) {
        this.client = client;
        setLayout(new BorderLayout());
        setBackground(Pallet.BACKGROUND.value());

        JLabel titleLabel = new JLabel("JOIN A NEW GAME");
        addFormattedFont(titleLabel);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setPreferredSize(new Dimension(getWidth(), 150));
        add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        add(inputPanel, BorderLayout.CENTER);
        inputPanel.setLayout(new GridLayout(6, 1, 10, 10));
        inputPanel.setBackground(Pallet.BACKGROUND.value());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel userNameLabel = new JLabel("Enter Username: ");
        addFormattedFont(userNameLabel);
        inputPanel.add(userNameLabel);
        userName = new JTextField("here");
        addFormattedFont(userName);
        userName.setBackground(Pallet.BACKGROUND.value());
        inputPanel.add(userName);

        JLabel hostIpLabel = new JLabel("Enter Host Ip: ");
        addFormattedFont(hostIpLabel);
        inputPanel.add(hostIpLabel);
        hostName = new JTextField("here");
        addFormattedFont(hostName);
        hostName.setBackground(Pallet.BACKGROUND.value());
        inputPanel.add(hostName);

        JLabel hostPortLabel = new JLabel("Enter Host Port: ");
        addFormattedFont(hostPortLabel);
        inputPanel.add(hostPortLabel);
        hostPort = new JTextField("here");
        addFormattedFont(hostPort);
        hostPort.setBackground(Pallet.BACKGROUND.value());
        inputPanel.add(hostPort);

        JPanel placeHolder = new JPanel();
        placeHolder.setPreferredSize(new Dimension(getWidth(), 150));
        placeHolder.setBackground(Pallet.BACKGROUND.value());
        add(placeHolder, BorderLayout.SOUTH);

        removeDefaultText();
        addKeyboardListeners();
    }

    private static void addFormattedFont(Component component) {
        component.setForeground(Pallet.FOREGROUND.value());
        component.setFont(new Font("Serif", Font.BOLD, 60));
    }

    private void removeDefaultText() {
        userName.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (userName.getText().equals("here")) userName.setText("");
                if (hostName.getText().isEmpty()) hostName.setText("here");
                if (hostPort.getText().isEmpty()) hostPort.setText("here");
            }
        });

        hostName.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (hostName.getText().equals("here") ||
                    hostName.getText().equals("Enter a valid host name")) hostName.setText("");
                if (userName.getText().isEmpty()) userName.setText("here");
                if (hostPort.getText().isEmpty()) hostPort.setText("here");
            }
        });

        hostPort.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (hostPort.getText().equals("here") ||
                    hostPort.getText().equals("Enter a valid host port")) hostPort.setText("");
                if (userName.getText().isEmpty()) userName.setText("here");
                if (hostName.getText().isEmpty()) hostName.setText("here");
            }
        });
    }

    private void addKeyboardListeners() {
        userName.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    connectToServer();
                }
            }
        });
        hostName.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    connectToServer();
                }
            }
        });
        hostPort.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    connectToServer();
                }
            }
        });

    }

    private void connectToServer() {
        if (userName != null && hostName != null && hostPort != null) {
            client.setUserName(userName.getText());
            if (client.connect(hostName.getText(), hostPort.getText())) {
                client.getViewManager().showGamePanel();
            }
        }
    }

    public void setIncorrectHostName() {
        hostName.setText("Enter a valid host name");
    }
    public void setIncorrectHostPort() {
        hostPort.setText("Enter a valid host port");
    }
}
