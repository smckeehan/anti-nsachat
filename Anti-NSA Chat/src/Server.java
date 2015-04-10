import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
/**
 * A Server that accepts multiple clients
 */
public class Server {
	//Listing of all of the clients currently connected to the server
	public static ClientsListing clients = new ClientsListing();
	
    public static void main(String[] args) throws Exception {
    	int port = 9898;
    	if(args.length>0){
    		try{
    			port = Integer.parseInt(args[0]);
    		}
    		catch(Exception e){
    			System.out.println("Port could not be parsed, defaulting to port 9898");
    		}
    	}
    	//Initialize listener and start thread that keeps track of what clients are connected.
    	ServerSocket listener = new ServerSocket(port);
    	clients.start();
    	System.out.println("Anti NSA Chat Server Now Running on port " + port);
    	System.out.println("Server now accepting clients");
    	
        try {
            while (true) {
				Socket socket = listener.accept();
				
				ClientConnection client = new ClientConnection(socket);
				
				//Make sure the username doesn't exist already, then add to listing and start the client thread.
				if((clients.getConnection(client.getUsername())) == null){
					System.out.println("Client " + client.getUsername() + " has connected with public key information n = " + client.getPublicKey().getN() + " and e = " + client.getPublicKey().getE() + ".");
					clients.addConnection(client);
					client.start();
					client.out.writeObject("Logged in, welcome.");
				}
				else{
					//If the user name already exists we just drop the connection. If time we should have this send a reply to client letting them know they were dropped
					System.out.println("Duplicate username, aborting");
					String output = "This username is already in use, please select a different name.";
					client.out.writeObject(output);

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
    			//Periodically check if someone has disconnected. Remove them if they have. Currently refreshes listing every five seconds.
    			Iterator<Entry<String,ClientConnection>> it = listing.entrySet().iterator();
    			while(it.hasNext()){
    				ClientConnection client = it.next().getValue();
    				if(!client.isAlive()){
    					it.remove();
    					System.out.println("Client " + client.getUsername() + " has disconnected.");
    				}
    			}
    			try {
					sleep(5000);
				} catch (InterruptedException e) {
					System.out.println("Sleep was interrupted");
				}
    		}
    	}
    	 /**
    	  * Returns a ClientConnection from the map when provided with a username.
    	  * 
    	  * @param username The username of the client who's connection is being request.
    	  * @return a ClientConnection for the client with the provided username. Note that we use the default
    	  *  behavior of HashMap's get function, so if the client wasn't in the listing then null is returned.
    	  * @see HashMap
    	  **/
    	public ClientConnection getConnection(String username){
    		return listing.get(username);
    	}
    	/**
    	 * Adds a client to the connection listing.
    	 * 
    	 * @param client The ClientConnection that is getting added to the client listing.
    	 */
    	public void addConnection(ClientConnection client){
    		listing.put(client.getUsername(),client);
    	}
    }
    
    /**
     * Class that handles Communication between clients. We use objects rather
     * than explicit Strings to communicate with clients so that we can transfer information easily.
     */
    private static class ClientConnection extends Thread {
        private Socket socket;//socket the client is connected on
        private String username;//the clients username
        private EKey publicKey;//the clients public key
   	 	ObjectInputStream in;//the input stream the server receives information from the client on/
   	 	ObjectOutputStream out;//the stream information is sent to the client on.
   	 	
   	 	/**
   	 	 * Constructs the client listing provided the socket for the client. Note that the constructor can throw an exception.
   	 	 *  When this occurs it returns a null, meaning that if what we receive on the socket isn't in line with what we expect,
   	 	 *  then we can avoid trying to create the connection at all by returning null and letting the main function know that 
   	 	 *  something went wrong.
   	 	 * 
   	 	 * @param socket The socket to initialize the ClientConnection on.
   	 	 * @throws IOException	gets thrown by input/output streams
   	 	 * @throws ClassNotFoundException Intentionally when we don't receive a client header, since it is a MUST in order for a client
   	 	 * to connect properly.
   	 	 */
        public ClientConnection(Socket socket) throws IOException, ClassNotFoundException {
            this.socket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());	
            out.flush();//Object streams transmit some initial data so we flush it first thing.
            in = new ObjectInputStream(socket.getInputStream());
            
            //The second the client connects we need to get its username and public key.
            //If the client doesn't provide these then the connection was either lost or the client isn't a actual chat client,
            //and null gets "returned" by the constructor.
            Object obj = in.readObject();
            if(obj instanceof ClientHeader){
            	ClientHeader header = (ClientHeader)obj;
            	this.username = header.getUsername();
            	this.publicKey = header.getKey();
            }
            else{
            	throw new ClassNotFoundException("Received incorrect object type.");
            }
        }
        
        public void run() {
        	//Read messages from client and pass along to the recipient
        	Object obj;
        	try {
				while((obj = in.readObject())!=null){
					/*When a message is received, we just pass it along*/
					if(obj instanceof ChatMessage){
						ChatMessage message = (ChatMessage)obj;
						System.out.println(message);
						sendMessage(message);
					}
					/*When a key request is received, we find it in our listing and send out the key, otherwise we let the client know the client wasn't here*/
					else if(obj instanceof KeyRequest){
						ClientConnection recipient = clients.getConnection(((KeyRequest)obj).getRecipient());
						if(recipient!=null){
							out.writeObject(recipient.getPublicKey());
						}
						else{
							//We don't have a key for the requested recipient, assume they are not connected
							System.out.println("Request for " + ((KeyRequest)obj).getRecipient() + "'s public key could not be completed");
							ClientConnection outCon = clients.getConnection(((KeyRequest)obj).getUsername());
		        			outCon.out.writeObject("Recipient " + ((KeyRequest)obj).getRecipient() + " is not connected, " + 
							"above message was not sent.");
						}
					}
					else if (obj instanceof String) {
						System.out.println("Dead end code to check something");
					}
				}
			}
        	catch(Exception e){
        	}
        	
        }
        /**
         * Sends ChatMessage objects out to their intended recipient.
         * 
         * @param m The message to pass along to the recipient.
         */
        public void sendMessage(ChatMessage m){
        	try{
        		ClientConnection outCon = clients.getConnection(m.getRecipient());
        		//Verify something actually got retrieved, and since listing only refreshes every five seconds, verify the thread still lives
        		//(The ClientConnection threads die once the socket closes)
        		if(outCon != null && outCon.isAlive()){
        			outCon.out.writeObject(m);
        		}
        		else{
        			/*Recipient wasn't found, so let the sender know*/
        			System.out.println("Recipient " + m.getRecipient() + " is not Connected");
        			outCon = clients.getConnection(m.getUsername());
        			outCon.out.writeObject("Recipient " + m.getRecipient() + " is not connected, " + 
        			"above message was not sent.");
        		}
        	} catch (IOException e) {
        		System.out.println("Something went wrong while sending a message");
			}
        }
        /**
         * Returns the username attached to this ClientConnection.
         * 
         * @return The username attached to this ClientConnection.
         */
        public String getUsername(){
        	return username;
        }
        
        /**
         * Returns the public key attached to this ClientConnection.
         * 
         * @return The public key of this ClientConnection.
         */
        public EKey getPublicKey(){
        	return publicKey;
        }
        
        /**
         * Returns the socket that this client is currently connected on.
         * 
         * @return The socket this client is currently connected on.
         */
        public Socket getSocket(){
        	return socket;
        }
    }
}
