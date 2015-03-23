import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
/**
 * Server that accepts multiple clients
 */
public class Server {

    public static void main(String[] args) throws Exception {
    	//Initialize listener and start thread that keeps track of what clients are connected.
    	ServerSocket listener = new ServerSocket(9898);
    	ClientsListing clients = new ClientsListing();
    	clients.start();
    	System.out.println("Server now accepting clients");
    	
        try {
            while (true) {
				Socket socket = listener.accept();
				
				ClientConnection client = new ClientConnection(socket);
				System.out.println("Client " + client.getUsername() + " has connected.");
				
				//Make sure the username doesn't exist already, then add to listing
				if((clients.getConnection(client.getName())) == null){
					clients.addConnection(client);
					client.start();
				}
				else{
					socket.close();
				}
            }
        } 
        finally {
            listener.close();
        }
    }
    
    /**
     * Threaded class that contains a listing of all of the clients currently connected.
     * Refreshes every 5 seconds with updated listing when clients lose their connection.
     */
    private static class ClientsListing extends Thread{
    	//Map that contains all client connections, used to lookup recipient sockets
    	public static Map<String,ClientConnection> listing;
    	
    	public ClientsListing(){
    		listing = new HashMap<String,ClientConnection>();
	    }
    	
    	public void run(){
    		while(true){
    			//Periodically check if someone has disconnected. Remove them if they have.
    			for(ClientConnection client : listing.values()){
    				if(!client.isAlive()){
    					listing.remove(client.getUsername());
    					System.out.println("Client " + client.getUsername() + " has disconnected.");
    				}
    			}
    			
    			try {
					sleep(5000);
				} catch (InterruptedException e) {
					
				}
    		}
    	}
    	public ClientConnection getConnection(String username){
    		return listing.get(username);
    	}
    	public void addConnection(ClientConnection client){
    		listing.put(client.getUsername(),client);
    	}
    }
    /**
     * Class that handles Communication between clients. We use objects rather
     * than Strings to communicate with clients so that we can transfer information easily.
     */
    private static class ClientConnection extends Thread {
        private Socket socket;
        private String username;
        private PublicKey publicKey;
   	 	ObjectInputStream in;
   	 	ObjectOutputStream out;
   	 	
   	 	
        public ClientConnection(Socket socket) throws IOException, ClassNotFoundException {
            this.socket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());
            //Object streams transmit some initial data so we flush it first thing.
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            //The second the client connects we need to get its username and public key.
            //If the client doesn't provide these then the connection was either lost or the client isn't a actual chat client.
            Object obj = in.readObject();
            if(obj instanceof ClientHeader){
            	ClientHeader header = (ClientHeader)obj;
            	this.username = header.getUsername();
            	this.publicKey = header.getKey();
            }
            else{
            	throw new IOException("Received incorrect object type.");
            }
        }
        
        public void run() {
        	//Read messages from client and print to system out.
        	Object obj;
        	try {
				while((obj = in.readObject())!=null){
					if(obj instanceof ChatMessage){
						System.out.println((ChatMessage)obj);
						/*ChatMessage message = (ChatMessage)obj;
						String recipient = message.getRecipient();*/
						
					}
					/**else if(obj instanceof PublicKeyRequest){
						
					}**/
				}
			}
        	catch(Exception e){
        		
        	}
        	
        }
        public void sendMessage(ChatMessage m){
        	try{
        		out.writeObject(m);        		
        	} catch (IOException e) {
        		
			}
        }
        public String getUsername(){
        	return username;
        }
        
        public PublicKey getPublicKey(){
        	return publicKey;
        }
        
        public Socket getSocket(){
        	return socket;
        }
    }
}