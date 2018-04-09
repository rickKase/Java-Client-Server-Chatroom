import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

final class ChatServer {
    private static int uniqueId = 0;
    // Data structure to hold all of the connected clients
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;			// port the server is hosted on

    /**
     * ChatServer constructor
     * @param port - the port the server is being hosted on
     */
    private ChatServer(int port) {
        this.port = port;
    }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println(getTimeStamp() + " Server waiting for Clients on port " + port);
            acceptClients(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts loop to listen for incoming client connections
     * @param serverSocket
     * @throws IOException
     */
    private void acceptClients(ServerSocket serverSocket) throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            ClientThread r = new ClientThread(socket, uniqueId++);
            Thread t = new Thread(r);

            // check and terminate any duplicate usernames
            if (isUsernameTaken(r.username)) {
                try {
                    r.sOutput.writeObject("\"" + r.username + "\" is already taken");
                    r.closeThisConnection();
                    System.out.println(getTimeStamp() + " Another User tried to join with taken username: " + r.username);
                    continue;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            clients.add(r);
            System.out.println(getTimeStamp() + " " + r.username + " just connected.");
            t.start(); // call run() in ClientThread
        }
    }

    /*
     *	Sample code to use as a reference for Tic Tac Toe
     *
     * directMessage - sends a message to a specific username, if connected
     * @param message - the string to be sent
     * @param username - the user the message will be sent to
     */
    /*private synchronized void directMessage(String message, String username) {
        String time = sdf.format(new Date());
        String formattedMessage = time + " " + message + "\n";
        System.out.print(formattedMessage);

        for (ClientThread clientThread : clients) {
            if (clientThread.username.equalsIgnoreCase(username)) {
                clientThread.writeMsg(formattedMessage);
            }
        }
    }*/

    /**

     * Helper method to quickly get proper Timestamp
     * @return
     */
    private static String getTimeStamp() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        return sdfTime.format(now);
    }

    private boolean isUsernameTaken(String username) {
        for (ClientThread clientThread : clients)
            if (clientThread.username.equals(username))
                return true;
        return false;
    }

    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        int portNumber = 1500;
        if (args.length > 0)
            portNumber = Integer.parseInt(args[0]);
        ChatServer server = new ChatServer(portNumber);
        server.start();
    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;                  // The socket the client is connected to
        ObjectInputStream sInput;       // Input stream to the server from the client
        ObjectOutputStream sOutput;     // Output stream to the client from the server
        String username;                // Username of the connected client
        int id;

        ChatMessage cm;                 // Helper variable to manage messages
        TicTacToeGame game = new TicTacToeGame();             // Helper variable to manage ticTacToe
        String starter;                 // Helper variable to manage starting player
        String opponent;                // Helper variable to manage opponent
        /*
         * socket - the socket the client is connected to
         * id - id of the connection
         */
        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                // Read the username sent to you by client
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Main loop executed in the client Thread. Branch paths often
         * in different methods
         */
        @Override
        public void run() {
            while (true) {
                // process client messages
                try {
                    cm = (ChatMessage) sInput.readObject();
                    switch (cm.getMessageType()) {
                        case 0:
                            processAsStandardMessage();
                            break;
                        case 1:
                            processAsLogout();
                            return;
                        case 2:
                            try{
                                processAsDM();
                            }catch (NullPointerException s){
                                System.out.println("shit");
                            }
                            break;
                        case 3:
                            processAsList();
                            break;
                        case 4:
                            processAsTicTacToe();
                            break;
                    }
                } catch (SocketException exc) {
                    // triggered when client disconnects abruptly
                    System.out.println(username + " disconnected");
                    closeThisConnection();
                    return;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Helper Method to send messages to client
         * @param message
         */
        private void sendMessageToClient(String message) {
            try {
                sOutput.writeObject(message);
            } catch (SocketException exc) {
                // triggered when client disconnects abruptly
                System.out.println(username + " disconnected");
                closeThisConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * used to disconnect from server properly
         */
        private void closeThisConnection() {
            try {
                clients.remove(this);
                sOutput.close();
                sInput.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Parses all messages that don't have a specialized tag
         * broadcasts message to every client
         * logs message to server console
         * also sends and logs timestamp
         */
        private void processAsStandardMessage() {
            System.out.println(getTimeStamp() + " " + username + ": " + cm.getMessage());
            for (ClientThread clientThread : clients)
            clientThread.sendMessageToClient(getTimeStamp() + " " +
                    username + ": " + cm.getMessage());
        }

        /**
         * Parses Logout command
         * triggered when a command begins with "/logout"
         * prints a time stamp and notice of a successful logout to the client
         * logs the logout in the server console
         */
        private void processAsLogout() {
            sendMessageToClient(getTimeStamp() + " Logout successful");
            System.out.println(getTimeStamp() + " User left: " + username);
            closeThisConnection();
        }

        /**
         * Processes DM commands
         * is triggered when a user starts a command with "/msg"
         * if the command doesn't have enough arguments or the recipient
         *   specified is not connected then the sender is notified of the
         *   error.
         * Message containing timestamp, sender, reciever, and message is
         *   sent to sender, reciever, and printed to server log.
         */
        private void processAsDM() {
            if (cm.getRecipient().equals("")) {
                sendMessageToClient("you must include a recipient and a message");
                return;
            }
            ClientThread recipient = null;
            for (ClientThread clientThread : clients)
                if (clientThread.username.equals(cm.getRecipient()))
                    recipient = clientThread;
            if (recipient == null) {
                sendMessageToClient(cm.getRecipient() + " is not connected");
                return;
            }
            String message = getTimeStamp() + " " + username + " -> "
                    + recipient.username + ": " + cm.getMessage();
            sendMessageToClient(message);
            recipient.sendMessageToClient(message);
            System.out.println(message);
        }

        /**
         * Parses the List command.
         * Is triggered when a command starts with "/list"
         * sends a list of Users to the user who typed the command that does
         *   excludes that user.
         * Logs timestamp, command, and user who typed it in server console
         */
        private void processAsList() {
            StringBuilder build = new StringBuilder();
            build.append(getTimeStamp() + " List of Users: ");
            for (ClientThread client : clients)
                if (!client.username.equals(username))
                    build.append("\t" + client.username);
            sendMessageToClient(build.toString());
            System.out.println(getTimeStamp() + " Sent List of Users to " + username);
        }

        /**
         * TODO: this
         */
        private void processAsTicTacToe() {
            if (cm.getRecipient().equals("")) {
                sendMessageToClient("you must include a recipient and a message");
                return;
            }
            if (cm.getRecipient().equals(username)) {
                sendMessageToClient("Invalid move in game against " + username);
                return;
            }
            ClientThread recipient = null;
            for (ClientThread clientThread : clients)
                if (clientThread.username.equals(cm.getRecipient()))
                    recipient = clientThread;
            if (recipient == null) {
                sendMessageToClient(cm.getRecipient() + " is not connected");
                return;
            }
            if(cm.getMessage().equals("")) {
                starter = username;
                opponent = cm.getRecipient();

                String messageStarter = "Started TicTacToe with " + opponent;
                String messageOpponent = "Started TicTacToe with " + starter;
                sendMessageToClient(messageStarter);
                recipient.sendMessageToClient(messageOpponent);
                game.newBoard();
                /*System.out.println(game.stringBoard());
                sendMessageToClient(game.stringBoard());
                recipient.sendMessageToClient(game.stringBoard());*/
                return;
            }
            if(cm.getMessage().equals("0") || cm.getMessage().equals("1") || cm.getMessage().equals("2") || cm.getMessage().equals("3") ||
                    cm.getMessage().equals("4") || cm.getMessage().equals("5") || cm.getMessage().equals("6") || cm.getMessage().equals("7") || cm.getMessage().equals("8")){
                int position = Integer.parseInt(cm.getMessage());
                if(username.equals(starter)) {
                    if(!game.updateBoard(position, "X")){
                        sendMessageToClient("Invalid move in game against " + starter);
                        return;
                    }
                    sendMessageToClient(game.stringBoard());
                    recipient.sendMessageToClient(game.stringBoard());
                } else if(username.equals(opponent)) {
                    if(!game.updateBoard(position, "O")){
                        sendMessageToClient("Invalid move in game against " + opponent);
                        return;
                    }
                    sendMessageToClient(game.stringBoard());
                    recipient.sendMessageToClient(game.stringBoard());
                }
            }else if(username.equals(starter)){
                sendMessageToClient("Invalid move in game against " + starter);
            }else if(username.equals(opponent)){
                sendMessageToClient("Invalid move in game against " + opponent);

            }
        }
    }
}