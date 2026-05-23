package server;

import common.Message;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static final int PORT = 12345;
    // Thread-safe map for username -> handler
    private Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private DatabaseManager dbManager;

    public ChatServer() {
        dbManager = new DatabaseManager();
    }

    public void start() {
        System.out.println("Server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New connection: " + socket.getInetAddress());
                ClientHandler handler = new ClientHandler(socket, this);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addClient(String username, ClientHandler handler) {
        clients.put(username, handler);
        broadcastUserList();
    }

    public synchronized void removeClient(String username) {
        if (username != null) {
            clients.remove(username);
            broadcastUserList();
            System.out.println(username + " disconnected.");
        }
    }

    public synchronized void broadcastUserList() {
        ArrayList<String> userList = new ArrayList<>(clients.keySet());
        Message userListMsg = new Message(Message.MessageType.USER_LIST, userList);
        for (ClientHandler client : clients.values()) {
            try {
                client.sendMessage(userListMsg);
            } catch (IOException e) {
            }
        }
    }

    public synchronized void broadcast(Message message) {
        // Log group messages (Text and File)
        if (message.getRecipient() == null || message.getRecipient().equals("All")) {
            String logContent = message.getContent();
            if (message.getType() == Message.MessageType.FILE) {
                logContent = "File: " + message.getFileName();
            }
            if (logContent != null) {
                dbManager.logMessage(message.getSender(), logContent);
            }
        }

        for (ClientHandler client : clients.values()) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
            }
        }
    }

    public synchronized void sendPrivateMessage(Message message) {
        // Log private message
        String logContent = message.getContent();
        if (message.getType() == Message.MessageType.FILE) {
            logContent = "File: " + message.getFileName();
        }
        if (logContent != null) {
            dbManager.logMessage(message.getSender(), "[Private to " + message.getRecipient() + "] " + logContent);
        }

        ClientHandler recipientHandler = clients.get(message.getRecipient());
        ClientHandler senderHandler = clients.get(message.getSender());

        // Send to recipient
        if (recipientHandler != null) {
            try {
                recipientHandler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (senderHandler != null) {
            try {
                senderHandler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}
