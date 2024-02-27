import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    private static List<Integer> serverPorts = new ArrayList<>();
    private static int currentServer = 0;

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

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    String message = reader.readLine();
                    if (message.startsWith("REGISTER:")) {
                        // This is a registration request from a new read server
                        int serverPort = Integer.parseInt(message.split(":")[1]);
                        serverPorts.add(serverPort);
                        System.out.println("Registered new read server on port: " + serverPort);
                    } else {
                        // This is a regular client message
                        forwardMessageToServer(message);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void forwardMessageToServer(String message) {
        if (serverPorts.isEmpty()) {
            System.out.println("No read servers available.");
            return;
        }

        int serverPort = serverPorts.get(currentServer);
        currentServer = (currentServer + 1) % serverPorts.size(); // Round-robin logic

        try (Socket socket = new Socket("localhost", serverPort)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(message);
            System.out.println("Forwarded message to server on port: " + serverPort);
        } catch (IOException ex) {
            System.out.println("Could not forward message: " + ex.getMessage());
        }
    }
}
