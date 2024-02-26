import java.io.*;
import java.net.*;

public class LoadBalancer {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java LoadBalancer <port number>");
            return;
        }
        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        // Here you would handle the operation request.
                        // For demonstration, we're just echoing back the input
                        out.println("Echo from LoadBalancer: " + inputLine);
                        // In a real scenario, you would parse the inputLine and
                        // decide to which server node to forward the request.
                    }
                } catch (IOException e) {
                    System.out.println("Exception caught when trying to listen on port "
                                       + portNumber + " or listening for a connection");
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
