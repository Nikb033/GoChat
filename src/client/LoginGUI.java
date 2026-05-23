package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JTextField ipField;
    private JTextField portField;
    private JButton connectButton;

    public LoginGUI() {
        super("Chat Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 5, 5));

        add(new JLabel("Username:"));
        usernameField = new JTextField("User" + (int) (Math.random() * 1000));
        add(usernameField);

        add(new JLabel("Server IP:"));
        ipField = new JTextField("localhost");
        add(ipField);

        add(new JLabel("Port:"));
        portField = new JTextField("12345");
        add(portField);

        connectButton = new JButton("Connect");
        add(connectButton);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String ip = ipField.getText();
                int port;
                try {
                    port = Integer.parseInt(portField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LoginGUI.this, "Invalid Port Number");
                    return;
                }

                ChatClient client = new ChatClient(ip, port, username);
                if (client.connect()) {
                    ChatGUI chatGUI = new ChatGUI(client, username);
                    client.setChatGUI(chatGUI);
                    chatGUI.setVisible(true);
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(LoginGUI.this, "Could not connect to server.");
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}
