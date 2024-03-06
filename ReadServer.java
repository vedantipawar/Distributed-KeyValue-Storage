import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ReadServer {
    private static final Map<String, String> dataStore = new HashMap<>();
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Syntax: java ReadServer <LoadBalancerPort> <ThisServerPort>");
            return;
        }

        int lbPort = Integer.parseInt(args[0]);
        int thisPort = Integer.parseInt(args[1]);

        // Initialize the data store with some values for demonstration purposes
        initializeDataStore();

        // Register itself with the LoadBalancer
        try (Socket socket = new Socket("localhost", lbPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("REGISTER:" + thisPort);
            System.out.println("Registered with LoadBalancer on port: " + lbPort);
        } catch (IOException e) {
            System.out.println("Could not register with LoadBalancer: " + e.getMessage());
            return;
        }

        // Rest of the ReadServer logic to accept and print messages...
        try (ServerSocket serverSocket = new ServerSocket(thisPort)) {
            System.out.println("ReadServer listening on port " + thisPort);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    String message = reader.readLine();
                    System.out.println("Received message: " + message);
                    String value = dataStore.getOrDefault(message, "Key not found");
                    System.out.println("Value in storage: " + value);
                }
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    private static void initializeDataStore() {
        // Sample data for demonstration. You should populate this according to your actual data requirements.
        dataStore.put("key1", "value1");
        dataStore.put("key2", "value2");
        dataStore.put("key3", "value3");
        // Add more key-value pairs as needed
    }
}