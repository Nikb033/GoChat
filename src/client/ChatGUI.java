package client;

import common.Message;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatGUI extends JFrame {
    private ChatClient client;
    private String username;

    // UI Components
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JPanel chatPanel;
    private JScrollPane chatScrollPane;
    private JTextField messageField;
    private JButton sendButton;
    private JButton fileButton;
    private JButton logoutButton;
    private JLabel currentChatLabel;

    private Map<String, List<Message>> conversationHistory;
    private String currentRecipient = "Group Chat";

    public ChatGUI(ChatClient client, String username) {
        super("GoChat - " + username);
        this.client = client;
        this.username = username;
        this.conversationHistory = new HashMap<>();
        this.conversationHistory.put("Group Chat", new ArrayList<>());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(44, 62, 80));
        leftPanel.setPreferredSize(new Dimension(250, 0));

        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(new Color(52, 73, 94));
        profilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel profileLabel = new JLabel("Logged in as: " + username);
        profileLabel.setForeground(Color.WHITE);
        profilePanel.add(profileLabel, BorderLayout.CENTER);

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(192, 57, 43));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 10));
        logoutButton.addActionListener(e -> logout());
        profilePanel.add(logoutButton, BorderLayout.EAST);

        leftPanel.add(profilePanel, BorderLayout.NORTH);
        userListModel = new DefaultListModel<>();
        userListModel.addElement("Group Chat");
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setSelectedIndex(0);
        userList.setFixedCellHeight(50);
        userList.setBackground(new Color(44, 62, 80));
        userList.setForeground(Color.WHITE);
        userList.setCellRenderer(new UserListRenderer());
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = userList.getSelectedValue();
                if (selected != null) {
                    switchChat(selected);
                }
            }
        });
        leftPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JPanel chatHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chatHeader.setBackground(new Color(52, 152, 219));
        chatHeader.setPreferredSize(new Dimension(0, 60));
        currentChatLabel = new JLabel("Group Chat");
        currentChatLabel.setForeground(Color.WHITE);
        currentChatLabel.setFont(new Font("Arial", Font.BOLD, 18));
        chatHeader.add(currentChatLabel);
        rightPanel.add(chatHeader, BorderLayout.NORTH);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(236, 240, 241));
        chatScrollPane = new JScrollPane(chatPanel);
        rightPanel.add(chatScrollPane, BorderLayout.CENTER);
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(Color.WHITE);

        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(messageField, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);

        fileButton = new JButton("Upload");
        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(52, 152, 219));
        sendButton.setForeground(Color.WHITE);

        btnPanel.add(fileButton);
        btnPanel.add(sendButton);
        inputPanel.add(btnPanel, BorderLayout.EAST);

        rightPanel.add(inputPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
        fileButton.addActionListener(e -> uploadFile());
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            client.sendMessage(new Message(Message.MessageType.LOGOUT, username, "", "All"));
            System.exit(0);
        }
    }

    private void switchChat(String target) {
        currentRecipient = target;
        currentChatLabel.setText(target);
        chatPanel.removeAll();

        if (!conversationHistory.containsKey(target)) {
            conversationHistory.put(target, new ArrayList<>());
        }

        for (Message msg : conversationHistory.get(target)) {
            addMessageBubble(msg);
        }

        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();
    }

    public void updateUserList(ArrayList<String> users) {
        String selected = userList.getSelectedValue();
        userListModel.clear();
        userListModel.addElement("Group Chat");
        for (String user : users) {
            if (!user.equals(username)) {
                userListModel.addElement(user);
            }
        }
        if (selected != null && userListModel.contains(selected)) {
            userList.setSelectedValue(selected, true);
        } else {
            userList.setSelectedIndex(0);
        }
    }

    private void sendMessage() {
        String content = messageField.getText();
        if (!content.isEmpty()) {
            String recipient = currentRecipient.equals("Group Chat") ? "All" : currentRecipient;
            Message msg = new Message(Message.MessageType.TEXT, username, content, recipient);

            appendMessage(msg);

            client.sendMessage(msg);
            messageField.setText("");
        }
    }

    private void uploadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.canRead()) {
                JOptionPane.showMessageDialog(this, "Error: Cannot read file. It might be locked by another process.",
                        "File Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                byte[] fileData = new byte[(int) selectedFile.length()];
                FileInputStream fis = new FileInputStream(selectedFile);
                fis.read(fileData);
                fis.close();

                String recipient = currentRecipient.equals("Group Chat") ? "All" : currentRecipient;
                Message msg = new Message(Message.MessageType.FILE, username, selectedFile.getName(), fileData,
                        recipient);

                appendMessage(msg);

                client.sendMessage(msg);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage(), "File Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public void appendMessage(Message message) {
        String key;
        if (message.getRecipient() == null || message.getRecipient().equals("All")
                || message.getRecipient().equals("Group Chat")) {
            key = "Group Chat";
        } else {
            if (message.getSender().equals(username)) {
                key = message.getRecipient();
            } else {
                key = message.getSender();
            }
        }

        if (!conversationHistory.containsKey(key)) {
            conversationHistory.put(key, new ArrayList<>());
        }
        conversationHistory.get(key).add(message);

        if (currentRecipient.equals(key)) {
            addMessageBubble(message);
            scrollToBottom();
        }

        if (message.getType() == Message.MessageType.FILE && !message.getSender().equals(username)) {
            saveFile(message);
        }
    }

    private void addMessageBubble(Message message) {
        boolean isMe = message.getSender().equals(username);
        JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));

        if (isMe) {
            bubble.setBackground(new Color(52, 152, 219));
        } else {
            bubble.setBackground(Color.WHITE);
        }
        bubble.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String headerText = message.getSender() + "  " + (message.getTimestamp() != null ? message.getTimestamp() : "");
        JLabel senderLabel = new JLabel(headerText);
        senderLabel.setFont(new Font("Arial", Font.BOLD, 11));
        if (isMe) {
            senderLabel.setForeground(new Color(236, 240, 241));
        } else {
            senderLabel.setForeground(Color.GRAY);
        }
        bubble.add(senderLabel);

        if (message.getType() == Message.MessageType.TEXT) {
            JTextArea text = new JTextArea(message.getContent());
            text.setWrapStyleWord(true);
            text.setLineWrap(true);
            text.setOpaque(false);
            text.setEditable(false);
            text.setFont(new Font("Arial", Font.PLAIN, 14));
            if (isMe) {
                text.setForeground(Color.WHITE);
            } else {
                text.setForeground(Color.BLACK);
            }
            text.setSize(new Dimension(300, Short.MAX_VALUE));
            bubble.add(text);
        } else if (message.getType() == Message.MessageType.FILE) {
            JLabel fileLabel = new JLabel("[File] " + message.getFileName());
            fileLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            if (isMe) {
                fileLabel.setForeground(Color.WHITE);
            } else {
                fileLabel.setForeground(Color.BLACK);
            }
            bubble.add(fileLabel);
        }

        JPanel wrapper = new JPanel(new FlowLayout(isMe ? FlowLayout.RIGHT : FlowLayout.LEFT));
        wrapper.setBackground(new Color(236, 240, 241));
        wrapper.add(bubble);

        chatPanel.add(wrapper);
        chatPanel.add(Box.createVerticalStrut(10));
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private void saveFile(Message message) {
        try {
            File downloadsDir = new File("downloads_" + username);
            if (!downloadsDir.exists())
                downloadsDir.mkdir();

            File file = new File(downloadsDir, message.getFileName());
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(message.getFileData());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class UserListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            if (isSelected) {
                panel.setBackground(new Color(52, 152, 219));
                panel.setForeground(Color.WHITE);
            } else {
                panel.setBackground(new Color(44, 62, 80));
                panel.setForeground(Color.WHITE);
            }

            JLabel nameLabel = new JLabel(value.toString());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            if (isSelected) {
                nameLabel.setForeground(Color.WHITE);
            } else {
                nameLabel.setForeground(Color.WHITE);
            }

            // Avatar placeholder
            JLabel avatar = new JLabel("[User]");
            avatar.setFont(new Font("Arial", Font.BOLD, 12));
            if (isSelected) {
                avatar.setForeground(Color.WHITE);
            } else {
                avatar.setForeground(Color.LIGHT_GRAY);
            }

            panel.add(avatar, BorderLayout.WEST);
            panel.add(nameLabel, BorderLayout.CENTER);

            return panel;
        }
    }
}
