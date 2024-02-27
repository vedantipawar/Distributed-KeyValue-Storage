import java.io.*;
import java.net.*;

public class ReadServer {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java ReadServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("ReadServer listening on port " + port);

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message = reader.readLine();
                    System.out.println("Received message: " + message);
                }
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
