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
    private final JTextField hostId;
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
        hostId = new JTextField("here");
        addFormattedFont(hostId);
        hostId.setBackground(Pallet.BACKGROUND.value());
        inputPanel.add(hostId);

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
                if (hostId.getText().isEmpty()) hostId.setText("here");
                if (hostPort.getText().isEmpty()) hostPort.setText("here");
            }
        });

        hostId.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (hostId.getText().equals("here")) hostId.setText("");
                if (userName.getText().isEmpty()) userName.setText("here");
                if (hostPort.getText().isEmpty()) hostPort.setText("here");
            }
        });

        hostPort.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (hostPort.getText().equals("here")) hostPort.setText("");
                if (userName.getText().isEmpty()) userName.setText("here");
                if (hostId.getText().isEmpty()) hostId.setText("here");
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
        hostId.addKeyListener(new KeyAdapter() {
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
        if (userName != null && hostId != null && hostPort != null) {
            if (!hostId.getText().equals("here") && !hostId.getText().equals("here")) {
                client.setUserName(userName.getText());
                if (client.connect(hostId.getText(), Integer.parseInt(hostPort.getText()))) {
                    client.getViewManager().showGamePanel();
                }
            }
        }

    }
}
