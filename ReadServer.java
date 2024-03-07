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
                try (Socket lbSocket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(lbSocket.getInputStream()));
                     PrintWriter writer = new PrintWriter(lbSocket.getOutputStream(), true)) { // PrintWriter to write back to the client
                    String key = reader.readLine();
                    System.out.println("Received message: " + key);
                    String value = dataStore.getOrDefault(key, "Key not found");
                    System.out.println("Sending back value: " + value);
                    writer.println(value); // Send the value back to the loadbalancer
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