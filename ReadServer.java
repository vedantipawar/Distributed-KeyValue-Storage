import java.io.*;
import java.net.*;

public class ReadServer {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Syntax: java ReadServer <LoadBalancerPort> <ThisServerPort>");
            return;
        }

        int lbPort = Integer.parseInt(args[0]);
        int thisPort = Integer.parseInt(args[1]);

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
                }
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
