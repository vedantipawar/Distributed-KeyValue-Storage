import java.io.*;
import java.net.*;
import java.util.Scanner; // Import the Scanner class to read text

public class Client {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Syntax: java Client <host> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        
        try (Scanner scanner = new Scanner(System.in)) {
            while(true){
                System.out.println("Enter your message: ");
            String message = scanner.nextLine(); // Read user input

            try (Socket socket = new Socket(hostname, port)) {
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                writer.println(message);
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String response = reader.readLine(); // Read the response from the server
                System.out.println("Server's response: " + response);

                // System.out.println("Message sent to the load balancer");
            } catch (UnknownHostException ex) {
                System.out.println("Server not found: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }
            }
        }
    }
}