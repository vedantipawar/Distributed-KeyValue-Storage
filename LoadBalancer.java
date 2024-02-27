import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;

public class LoadBalancer {
    private static List<Integer> serverPorts = new ArrayList<>();
    private static int currentServer = 0;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java LoadBalancer <port>");
            return;
        }

        // Initialize server ports
        serverPorts.add(8081); // Port of the first read server
        serverPorts.add(8082); // Port of the second read server

        int port = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("LoadBalancer is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    String message = reader.readLine();
                    System.out.println("The message recieved that will be forwarded is:"+ message);
                    forwardMessageToServer(message);
                }
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void forwardMessageToServer(String message) {
        int serverPort = serverPorts.get(currentServer);
        currentServer = (currentServer + 1) % serverPorts.size(); // Round-robin logic

        try (Socket socket = new Socket("localhost", serverPort)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Inside forward function");
            writer.println(message);
            System.out.println("Forwarded message to server on port: " + serverPort);
        } catch (IOException ex) {
            System.out.println("Could not forward message: " + ex.getMessage());
        }
    }
}
