import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
    public static void main(String[] args) throws Exception {
        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(9898);
        try {
            while (true) {
                new ChatServer(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class ChatServer extends Thread {
        private Socket socket;
        private int clientNumber;

        public ChatServer(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
        }
        
        public void run() {
            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Welcome to Anti-NSA Chat! You are client #" + clientNumber + ".");

            } catch (IOException e) {
                
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    
                }
                
                
            }
        }
    }
}