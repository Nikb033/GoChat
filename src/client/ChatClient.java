package client;

import common.Message;
import java.io.*;
import java.net.Socket;
import javax.swing.SwingUtilities;

public class ChatClient {
    private String hostname;
    private int port;
    private String username;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ChatGUI chatGUI;

    public ChatClient(String hostname, int port, String username) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
    }

    public void setChatGUI(ChatGUI chatGUI) {
        this.chatGUI = chatGUI;
    }

    public boolean connect() {
        try {
            socket = new Socket(hostname, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Send initial login message
            sendMessage(new Message(Message.MessageType.LOGIN, username, "", "All"));

            // Start listener thread
            new Thread(new IncomingReader()).start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFileMessage(Message message) {
        sendMessage(message);
    }

    private class IncomingReader implements Runnable {
        @Override
        public void run() {
            try {
                Message message;
                while ((message = (Message) in.readObject()) != null) {
                    final Message msg = message;
                    SwingUtilities.invokeLater(() -> {
                        if (msg.getType() == Message.MessageType.USER_LIST) {
                            chatGUI.updateUserList(msg.getActiveUsers());
                        } else {
                            if (!msg.getSender().equals(username) || msg.getSender().equals("Server")
                                    || msg.getSender().equals("History")) {
                                chatGUI.appendMessage(msg);
                            }
                        }
                    });
                }
            } catch (IOException | ClassNotFoundException e) {
                SwingUtilities.invokeLater(() -> chatGUI
                        .appendMessage(new Message(Message.MessageType.TEXT, "System", "Connection lost.", "All")));
            }
        }
    }
}
