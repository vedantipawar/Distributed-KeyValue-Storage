import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    // Use InetSocketAddress to store both hostname and port
    private static List<InetSocketAddress> serverAddresses = new ArrayList<>();
    private static int currentServer = 0;
    // Initialize LFUCache with a certain capacity, e.g., 10
    private static LFUCache cache = new LFUCache(2); 

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java LoadBalancer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("LoadBalancer is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                //System.out.println("New connection");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    String key = reader.readLine();
                    if (key.startsWith("REGISTER:")) {
                        // This is a registration request from a new read server
                        String[] parts = key.split(":");
                        if (parts.length == 3) {
                            String hostname = parts[1];
                            int serverPort = Integer.parseInt(parts[2]);
                            serverAddresses.add(new InetSocketAddress(hostname, serverPort));
                            System.out.println("Registered new read server at " + hostname + ":" + serverPort);
                        }
                    } else {
                        // This is a regular client message, check cache first
                        String cachedValue = cache.get(key);
                        if (cachedValue != null) {
                            // Cache hit, return value directly to client
                            clientWriter.println(cachedValue + " From Cache");
                        } else {
                            // Cache miss, forward to read server
                            String response = forwardMessageToServerAndReceiveResponse(key);
                            cache.put(key, response); // Cache the new key-value pair
                            clientWriter.println(response); // Send the response back to the client
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String forwardMessageToServerAndReceiveResponse(String message) {
        if (serverAddresses.isEmpty()) {
            System.out.println("No read servers available.");
            return "Error: No read servers available.";
        }

        InetSocketAddress serverAddress = serverAddresses.get(currentServer);
        currentServer = (currentServer + 1) % serverAddresses.size(); // Round-robin logic

        try (Socket socket = new Socket(serverAddress.getHostName(), serverAddress.getPort());
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            writer.println(message);
            System.out.println("Forwarded message to server at: " + serverAddress);

            // Wait for the response from the ReadServer
            String response = reader.readLine();
            System.out.println("Received response from server at: " + serverAddress);
            return response; // Return the response received from the server
        } catch (IOException ex) {
            System.out.println("Could not forward message: " + ex.getMessage());
            return "Error: Could not communicate with read server.";
        }
    }
}
