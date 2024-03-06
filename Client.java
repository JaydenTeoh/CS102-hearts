// Client.java
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;


public class Client {
    private static final String SERVER_HOST = "192.168.x.x"; // Replace "192.168.x.x" with the server's local IP address
    private static final int SERVER_PORT = 1234;

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                sendMessage(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void sendMessage(String message) {
        try {
            // Get the output stream of the socket
            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            
            // Write the message to the output stream
            writer.write(message);
            writer.newLine();
            writer.flush(); // Flush the stream to ensure the message is sent immediately
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateUI(String message) {
    // Update UI components with the received message
}

    private void listenForMessages() {
        try {
            // Get the input stream of the socket
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            
            String message;
            while ((message = reader.readLine()) != null) {
                // Process the received message
                handleReceivedMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle received messages
    private void handleReceivedMessage(String message) {
        // Perform actions based on the received message
        System.out.println("Received message: " + message);
        // You can add more logic here as needed
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    private void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
