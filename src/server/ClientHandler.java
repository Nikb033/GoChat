package server;

import common.Message;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            Message message;
            while ((message = (Message) in.readObject()) != null) {
                if (message.getType() == Message.MessageType.LOGIN) {
                    this.username = message.getSender();
                    server.addClient(username, this);
                    System.out.println(username + " logged in.");
                    server.broadcast(
                            new Message(Message.MessageType.TEXT, "Server", username + " has joined the chat.", "All"));

                    // Send history
                    for (String historyLog : new DatabaseManager().getChatHistory()) {
                        sendMessage(new Message(Message.MessageType.TEXT, "History", historyLog, "All"));
                    }

                } else if (message.getType() == Message.MessageType.LOGOUT) {
                    server.removeClient(username);
                    server.broadcast(
                            new Message(Message.MessageType.TEXT, "Server", username + " has left the chat.", "All"));
                    break;
                } else if (message.getType() == Message.MessageType.TEXT
                        || message.getType() == Message.MessageType.FILE) {
                    if (message.getRecipient() == null || message.getRecipient().equals("All")
                            || message.getRecipient().equals("Group Chat")) {
                        server.broadcast(message);
                    } else {
                        server.sendPrivateMessage(message);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Client disconnected
        } finally {
            server.removeClient(username);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }
}
