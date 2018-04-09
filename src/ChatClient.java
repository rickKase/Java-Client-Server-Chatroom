import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    /* ChatClient constructor
     * @param server - the ip address of the server as a string
     * @param port - the port number the server is hosted on
     * @param username - the username of the user connecting
     */
    private ChatClient(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    /**
     * Attempts to establish a connection with the server
     * @return boolean - false if any errors occur in startup, true if successful
     test change
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput.writeObject(username);
            sOutput.flush();
        } catch (IOException e) {
            System.out.println("Client could not be started");
            System.exit(1);
            return false;
        }
        // Create client thread to listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();
        // After starting, send the clients username to the server.
        return true;
    }

    /*
     * Sends a string to the server
     * @param msg - the message to be sent
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String username = "Anonymous";
        int portNumber = 1500;
        String serverAddress = "localhost";

        // Get proper arguments and override defaults
        if (args.length > 0)
            username = args[0];
        if (args.length > 1)
            portNumber = Integer.parseInt(args[1]);
        if (args.length > 2)
            serverAddress = args[2];
        // Create your client and start it
        ChatClient client = new ChatClient(serverAddress, portNumber, username);
        try {
            client.start();
        }catch (NullPointerException a){
            System.out.println("shit");
        }
        System.out.println("Connected to " + serverAddress + "/" + portNumber + " as " + username);
        // Send an empty message to the server
        while (true) {
            String message = scan.nextLine();

            client.sendMessage(createMessage(message));
        }

    }

    /**
     * Turns the Users input into an object representing a message to the server
     * @param message
     * @return
     */
    private static ChatMessage createMessage(String message) {
        String[] command = message.split(" ");
        if (command.length == 0)
            return new ChatMessage(0, "","");
        switch (command[0]) {
            case "/logout":
                return new ChatMessage(1, "","");
            case "/msg":
                if (command.length < 2)
                    return new ChatMessage(2, "", "");
                //start after the space after the second word
                String dm = message.substring(command[0].length()
                        + command[1].length() + 2);
                return new ChatMessage(2, dm, command[1]);
            case "/ttt":
                if(command.length < 3) {
                    return new ChatMessage(4, "", command[1]);
                }
                //when user enters move, will process it
                ChatMessage moves = new ChatMessage(4,"", command[1]);
                moves.setMessage(command[2]);
                return moves;
            case "/list":
                return new ChatMessage(3, "","");
        }
        return new ChatMessage(0, message, "");
    }

    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */

    /**
     * This class allows each user to recieve infomation and send information
     * asynchronously. Messages to and from the server don't have to come in
     * any set order because all messages to the Server are processed on the
     * main thread while those coming from the server are process right here.
     */
    private final class ListenFromServer implements Runnable {
        Scanner serverInput = new Scanner(sInput);
        public void run() {
            try {
                while (true) {
                    String msg = (String) sInput.readObject();
                    System.out.println(msg);
                }
            } catch (EOFException e) {
                System.exit(0);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
