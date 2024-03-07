import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ReadServer {
    private static final Map<String, String> dataStore = new HashMap<>();
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Syntax: java ReadServer <LoadBalancerHost> <LoadBalancerPort> <ThisServerPort>");
            return;
        }

        String lbHost = args[0];
        int lbPort = Integer.parseInt(args[1]);
        int thisPort = Integer.parseInt(args[2]);

        // Initialize the data store with some values for demonstration purposes
        initializeDataStore();

        System.out.println("Before thisHost");
        // Attempt to get the local address of this server
        String thisHost;
        try {
            thisHost = InetAddress.getLocalHost().getHostAddress();
            System.out.println("This read server is running on: " + thisHost);
        } catch (UnknownHostException e) {
            System.out.println("Unable to determine the host name. " + e.getMessage());
            return;
        }
        System.out.println("After thisHost");

        // Register itself with the LoadBalancer
        try (Socket socket = new Socket(lbHost, lbPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            // Send registration message with hostname and port
            out.println("REGISTER:" + thisHost + ":" + thisPort);
            System.out.println("Registered with LoadBalancer at " + lbHost + ":" + lbPort);
        } catch (IOException e) {
            System.out.println("Could not register with LoadBalancer: " + e.getMessage());
            return;
        }

        // Rest of the ReadServer logic to accept and print messages...
        try (ServerSocket serverSocket = new ServerSocket(thisPort)) {
            System.out.println("ReadServer listening on port " + thisPort);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    String key = reader.readLine();
                    System.out.println("Received message: " + key);
                    String value = dataStore.getOrDefault(key, "Key not found");
                    System.out.println("Sending back value: " + value);
                    writer.println(value); // Send the value back to the LoadBalancer
                }
            }            
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private static void initializeDataStore() {
        // Sample data for demonstration. You should populate this according to your actual data requirements.
        dataStore.put("A", "America");
        dataStore.put("B", "Bolivia");
        dataStore.put("C", "Cambodia");
        dataStore.put("D", "Denmark");
        // Add more key-value pairs as needed
    }
}
