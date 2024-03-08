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

        // Register itself with the LoadBalancer
        registerWithLoadBalancer(lbHost, lbPort, thisPort);

        // Start the server to accept connections and commands
        startServer(thisPort);
    }

    private static void registerWithLoadBalancer(String lbHost, int lbPort, int thisPort) {
        String thisHost;
        try {
            thisHost = InetAddress.getLocalHost().getHostAddress();
            System.out.println("This read server is running on: " + thisHost);
        } catch (UnknownHostException e) {
            System.out.println("Unable to determine the host name. " + e.getMessage());
            return;
        }

        try (Socket socket = new Socket(lbHost, lbPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("REGISTER:" + thisHost + ":" + thisPort);
            System.out.println("Registered with LoadBalancer at " + lbHost + ":" + lbPort);
        } catch (IOException e) {
            System.out.println("Could not register with LoadBalancer: " + e.getMessage());
        }
    }

    private static void startServer(int thisPort) {
        try (ServerSocket serverSocket = new ServerSocket(thisPort)) {
            System.out.println("ReadServer listening on port " + thisPort);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    String message = reader.readLine();

                    if ("REQUEST_STATE_SNAPSHOT".equals(message)) {
                        //System.out.println("inside request state snapshot");
                        // Handle request for state snapshot
                        String snapshot = serializeDataStore();
                        //System.out.println("Data sereialized");
                        writer.println(snapshot);
                    } else if (message.startsWith("STATE_SNAPSHOT:")) {
                        // Deserialize and store the received state snapshot
                        //System.out.println("Inside state snahop");
                        String snapshot = message.substring("STATE_SNAPSHOT:".length());
                        //System.out.println("Snapshot received now desrializing");
                        deserializeAndStoreSnapshot(snapshot);
                        System.out.println("Desrialized");
                    } else if (message.startsWith("Write:")) {
                        // Existing logic to handle write requests
                        handleWriteRequest(message, writer);
                    } else {
                        // Existing logic to handle read requests
                        handleReadRequest(message, writer);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String serializeDataStore() {
        // Simple serialization logic. 
        return dataStore.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((e1, e2) -> e1 + "," + e2)
                .orElse("");
    }

    private static void deserializeAndStoreSnapshot(String snapshot) {
        // Simple deserialization logic. Match your serialization logic.
        dataStore.clear(); // Clear current dataStore before restoring snapshot
        String[] entries = snapshot.split(",");
        for (String entry : entries) {
            String[] keyValue = entry.split("=");
            if (keyValue.length == 2) {
                dataStore.put(keyValue[0], keyValue[1]);
            }
        }
    }

    private static void handleWriteRequest(String message, PrintWriter writer) {
        String[] parts = message.split(":", 3); // Split into Write, key, and value
        if (parts.length == 3) {
            String key = parts[1];
            String value = parts[2];
            dataStore.put(key, value); // Update the data store
            System.out.println("Written key '" + key + "' with value '" + value + "'");
            writer.println("Write successful"); // Confirm write success
        } else {
            writer.println("Write format error");
        }
    }

    private static void handleReadRequest(String message, PrintWriter writer) {
        String value = dataStore.getOrDefault(message, "Key not found");
        System.out.println("Sending value: "+ value );
        writer.println(value); // Send the value back to the client/LoadBalancer
    }

    private static void initializeDataStore() {
        // Sample data for demonstration. You should populate this according to your actual data requirements.
        dataStore.put("A", "America");
        dataStore.put("B", "Bolivia");

        // Add more key-value pairs as needed
    }
}
