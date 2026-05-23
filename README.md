##  Authors
*   **Amaan Mithani** (am14647)
*   **Nikhil Bhise** (nb4053)
*   **Kunaal Vadgama** (kv2343)

## Loom Video Link: 
https://www.loom.com/share/226493beee944d729a8bd97f083212f4

# GoChat - Advanced Java Chat Application

## 1. Project Overview

**GoChat** is a multi-user desktop-based chat application developed entirely in **Java**, following a **client–server architecture**. The application enables users to communicate in real time through group chat, private messaging, and file sharing over a network.

The project is designed to demonstrate **advanced Java programming concepts**, including:

- Socket-based networking
- Multithreaded server and client handling
- Graphical User Interface (GUI) development using Java Swing
- Database persistence using SQLite
- File input/output and object-based communication

The system consists of:
- A **central server** that manages user connections, message routing, file transfers, and database storage
- Multiple **clients** that provide an interactive graphical interface for users

---

## 2. Features – Core Functionality

### 2.1 Real-Time Group Chat

**Description:**  
All connected users automatically join a common group chat room. Messages sent by any user are instantly delivered to all other active users.

**Working Mechanism:**
- Clients send messages to the server using TCP sockets.
- The server maintains a list of active client connections.
- Upon receiving a message, the server broadcasts it to all connected clients.
- Each client listens for incoming messages on a background thread and updates the chat window in real time.

**Java Concepts Used:**
- TCP socket communication
- Multithreaded server design
- Broadcast message handling
- Concurrent client management

---

### 2.2 Private Messaging

**Description:**  
GoChat allows users to send private one-to-one messages by selecting a specific user from the online users list.

**Working Mechanism:**
- The server maps usernames to their socket connections.
- Private messages are routed only to the intended recipient.
- Messages are visually distinguished from group chat messages in the UI.

**Java Concepts Used:**
- Targeted message routing
- Data structures for user-session mapping
- Event-driven programming in Swing

---

### 2.3 File Sharing

**Description:**  
Users can share files such as images, PDFs, and documents either with the entire group or individual users.

**Working Mechanism:**
- Users select files using a file chooser dialog.
- File metadata (name, size, sender, receiver) is sent to the server.
- File data is transmitted using buffered input/output streams.
- Received files are saved in a local `downloads` directory.

**Java Concepts Used:**
- File I/O streams
- Binary data transmission
- TCP-based reliable file transfer
- Concurrent file handling

---

### 2.4 User Presence (Online Users List)

**Description:**  
The application displays a real-time list of users currently connected to the server.

**Working Mechanism:**
- The server updates the user list when clients connect or disconnect.
- Updated lists are broadcast to all clients.
- The Swing-based UI dynamically refreshes the displayed list.

**Java Concepts Used:**
- Server-side state management
- Dynamic UI updates
- Event handling

---

## 3. Advanced Capabilities

### 3.1 Persistent Chat History (SQLite Database)

**Description:**  
All group and private chat messages are stored in a local **SQLite database (`chat.db`)**, ensuring data persistence.

**Working Mechanism:**
- Messages are inserted into the database upon receipt by the server.
- Each record includes sender, receiver, message content, and timestamp.
- The database is automatically created if it does not exist.

**Benefits:**
- Chat history is preserved even after server restarts
- Enables future enhancements like message search and analytics

**Java Concepts Used:**
- JDBC (Java Database Connectivity)
- SQL operations
- Persistent storage management

---

### 3.2 Optimistic User Interface

**Description:**  
The user interface updates immediately when a user sends a message or file, without waiting for server acknowledgment.

**Working Mechanism:**
- Messages are displayed instantly in the chat window.
- Network communication runs on background threads.
- Improves perceived performance and responsiveness.

**Java Concepts Used:**
- Multithreading
- Non-blocking UI updates
- Separation of UI and networking logic

---

### 3.3 Modern User Interface Design

**Description:**  
GoChat features a visually appealing **Modern Blue** theme, improving usability compared to basic Swing interfaces.

**UI Enhancements:**
- Dark sidebar for online users
- Message bubbles for readability
- Clean fonts and spacing
- Custom Swing renderers

**Java Concepts Used:**
- Swing layout managers
- Custom component rendering
- UI styling

---


##  Technology Stack

*   **Language**: Java (JDK 8+)
*   **Frontend**: Java Swing (JFrame, JPanel, JList, Custom Renderers)
*   **Backend**: Java Sockets (`java.net.Socket`, `ServerSocket`)
*   **Database**: SQLite (`sqlite-jdbc`)
*   **Concurrency**: Java Threads (`Thread`, `Runnable`) for handling multiple clients and background tasks.

##  Project Structure

```
javaproject1/
├── src/
│   ├── client/         # GUI and Client-side logic
│   ├── server/         # Server-side logic and Database management
│   └── common/         # Shared objects (Message.java)
├── lib/
│   └── sqlite-jdbc-*.jar  # SQLite Database Driver
├── chat.db             # Local Database file (auto-created)
└── README.md           # This file
```

##  How to Run

### Prerequisites
*   Java Development Kit (JDK) installed.
*   The `sqlite-jdbc` driver must be in the `lib` folder (included).

### 1. Compile the Project
Open a terminal in the project root directory and run:

```bash
javac -cp "src;lib/*" src/common/*.java src/server/*.java src/client/*.java
```

### 2. Start the Server
The server must be running before any clients can connect.

```bash
java -cp "src;lib/*" server.ChatServer
```
*You will see "Server started on port 12345".*

### 3. Start Clients
Open a **new terminal window** for each user you want to connect.

```bash
java -cp "src;lib/*" client.LoginGUI
```
*   Enter a unique **Username**.
*   Default IP: `localhost`
*   Default Port: `12345`
*   Click **Connect**.
