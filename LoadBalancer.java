import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    // Use InetSocketAddress to store both hostname and port
    private static List<InetSocketAddress> serverAddresses = new ArrayList<>();
    private static int currentServer = 0;
    // Initialize LFUCache with a certain capacity, e.g., 10
    private static LFUCache cache = new LFUCache(3); 

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
                            InetSocketAddress newServerAddress = new InetSocketAddress(hostname, serverPort);
                            serverAddresses.add(newServerAddress);
                            System.out.println("Registered new read server at " + hostname + ":" + serverPort);

                            // If it's not the first server, request state snapshot from the first server
                            if (serverAddresses.size() > 1) {
                                requestAndSendStateSnapshot(newServerAddress);
                        }
                        }
                    } else {
                        // This is a regular client message, check cache first
                        String cachedValue = cache.get(key);
                        if (cachedValue != null) {
                            // Cache hit, return value directly to client
                            clientWriter.println(cachedValue + " From Cache");
                        } else if (key.startsWith("Write:")) {
                            // It's a write request, broadcast it
                            broadcastMessageToServers(key);
                            clientWriter.println("Write operation broadcasted to all servers.");
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

    private static void requestAndSendStateSnapshot(InetSocketAddress newServerAddress) {
        // Address of the first server to request the snapshot from
        InetSocketAddress firstServerAddress = serverAddresses.get(0);
        //System.out.println("Inside requestAndSEnf function");
    
        
            // Connect to the first read server to request the state snapshot
            try (Socket firstServerSocket = new Socket(firstServerAddress.getHostName(), firstServerAddress.getPort());
                 PrintWriter firstServerWriter = new PrintWriter(firstServerSocket.getOutputStream(), true);
                 BufferedReader firstServerReader = new BufferedReader(new InputStreamReader(firstServerSocket.getInputStream()))) {
                
                //System.out.println("Sending request fpr a state snapshot");
                // Send a request for the state snapshot. Adjust the message as necessary based on your protocol
                firstServerWriter.println("REQUEST_STATE_SNAPSHOT");
                
                // Assume the first server responds with the state snapshot as a serialized string
                String stateSnapshot = firstServerReader.readLine();
                System.out.println("State snapshot received");
                // Now connect to the newly registered read server to send the snapshot
                try (Socket newServerSocket = new Socket(newServerAddress.getHostName(), newServerAddress.getPort());
                     PrintWriter newServerWriter = new PrintWriter(newServerSocket.getOutputStream(), true)) {
       
                    // Send the state snapshot to the new server. Adjust the message as necessary based on your protocol
                    newServerWriter.println("STATE_SNAPSHOT:" + stateSnapshot);
                    System.out.println("State snapshot sent to new server at " + newServerAddress);
                } catch (IOException ex) {
                    System.out.println("Could not send state snapshot to new server at " + newServerAddress + ": " + ex.getMessage());
                }
            }
            catch (IOException ex) {
            System.out.println("Could not request state snapshot from first server at " + firstServerAddress + ": " + ex.getMessage());
        }
    }
    
    

    private static void broadcastMessageToServers(String message) {
        for (InetSocketAddress serverAddress : serverAddresses) {
            try (Socket socket = new Socket(serverAddress.getHostName(), serverAddress.getPort());
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                writer.println(message);
                System.out.println("Broadcasted write message to server at: " + serverAddress);
            } catch (IOException ex) {
                System.out.println("Could not send write message to server at " + serverAddress + ": " + ex.getMessage());
            }
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
