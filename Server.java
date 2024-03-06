// Server.java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static final int PORT = 1234;
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clientHandlers;
    //private Game game;

    public Server() {
        clientHandlers = new ArrayList<>();
        //game = new Game(); // Assuming Game class exists and is appropriately implemented
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(socket, this);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcastGameState() {
        //String gameState = game.getState(); // Assuming Game class has a method to get the game state
        // for (ClientHandler clientHandler : clientHandlers) {
        //     clientHandler.sendMessage(gameState);
        // }
    }

    public synchronized void handleClientMove(ClientHandler clientHandler, String move) {
        // Process the move received from the client and update the game state accordingly
        //game.processMove(move); // Assuming Game class has a method to process moves
        broadcastGameState();
    }

    public synchronized void removeClientHandler(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
