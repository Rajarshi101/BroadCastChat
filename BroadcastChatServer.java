import java.io.*;
import java.net.*;
import java.util.*;

public class BroadcastChatServer {
    private static Map<PrintWriter, String> clients = new HashMap<>();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv("PORT")); // Get the port dynamically from Render
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is running on port: " + port);

        try {
            while (true) {
                new Handler(serverSocket.accept()).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private PrintWriter writer;
        private BufferedReader reader;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                System.out.println("Client connected: " + socket);

                String name = reader.readLine();
                clients.put(writer, name);

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received message from " + name + ": " + message);
                    broadcast(name + ": " + message);
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (writer != null) {
                    clients.remove(writer);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }

        private void broadcast(String message) {
            synchronized (clients) {
                for (PrintWriter clientWriter : clients.keySet()) {
                    if (clientWriter != writer) {
                        clientWriter.println(message);
                    }
                }
            }
        }
    }
}
