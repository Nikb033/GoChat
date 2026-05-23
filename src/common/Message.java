package common;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MessageType {
        TEXT, FILE, LOGIN, LOGOUT, USER_LIST
    }

    private MessageType type;
    private String sender;
    private String recipient; // null or "All" for group chat
    private String content;
    private String fileName;
    private byte[] fileData;
    private ArrayList<String> activeUsers; // For USER_LIST type
    private String timestamp;

    // Standard text message
    public Message(MessageType type, String sender, String content, String recipient) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.recipient = recipient;
        this.timestamp = new SimpleDateFormat("HH:mm").format(new Date());
    }

    // File message
    public Message(MessageType type, String sender, String fileName, byte[] fileData, String recipient) {
        this.type = type;
        this.sender = sender;
        this.fileName = fileName;
        this.fileData = fileData;
        this.recipient = recipient;
        this.timestamp = new SimpleDateFormat("HH:mm").format(new Date());
    }

    // User list update
    public Message(MessageType type, ArrayList<String> activeUsers) {
        this.type = type;
        this.activeUsers = activeUsers;
        this.timestamp = new SimpleDateFormat("HH:mm").format(new Date());
    }

    public MessageType getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public ArrayList<String> getActiveUsers() {
        return activeUsers;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
