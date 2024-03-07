import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    private static List<Integer> serverPorts = new ArrayList<>();
    private static int currentServer = 0;
    // Initialize LFUCache with a certain capacity, e.g., 10
    private static LFUCache cache = new LFUCache(10); 

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
                System.out.println("New connection");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    String key = reader.readLine();
                    if (key.startsWith("REGISTER:")) {
                        // This is a registration request from a new read server
                        int serverPort = Integer.parseInt(key.split(":")[1]);
                        serverPorts.add(serverPort);
                        System.out.println("Registered new read server on port: " + serverPort);
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
        if (serverPorts.isEmpty()) {
            System.out.println("No read servers available.");
            return "Error: No read servers available.";
        }

        int serverPort = serverPorts.get(currentServer);
        currentServer = (currentServer + 1) % serverPorts.size(); // Round-robin logic

        try (Socket socket = new Socket("localhost", serverPort);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            writer.println(message);
            System.out.println("Forwarded message to server on port: " + serverPort);
            
            // Wait for the response from the ReadServer
            String response = reader.readLine();
            System.out.println("Received response from server on port: " + serverPort);
            return response; // Return the response received from the server
        } catch (IOException ex) {
            System.out.println("Could not forward message: " + ex.getMessage());
            return "Error: Could not communicate with read server.";
        }
    }
}
