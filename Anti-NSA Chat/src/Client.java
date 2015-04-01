import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;


public class Client {
	Socket socket;
	String username;
	EKey key;
	ObjectInputStream in;
	ObjectOutputStream out;
	RSAEncryption encrypt = new RSAEncryption();
	RSADecryption decrypt = new RSADecryption();
	EKey publicKey = null;

 	MessageReceiver messageReceiver;

	public Client(String server, String username, EKey key) throws UnknownHostException, IOException{
		this.username = username; //This clients username
		socket = new Socket(server,9898); //The socket this client communicates on.
		
		//initializing object streams on the socket, and write ClientHeader.
		//ClientHeader contains information that is required for the server to accept the connection, so if this doesn't happen
		//the client won't be able to connect.
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
		out.writeObject(new ClientHeader(key,this.username));
		
		//Starts thread to begin receiving messages, once the connection to the server has been established.
		messageReceiver = new MessageReceiver(in, this);
		messageReceiver.start();
	}

	public boolean start() {
		return true;
	}
	/**
	 * This is the testing version of the sendMessage method, 
	 * used until we have working client to client message sending
	 */
	String sendMessage(String message, String recipient, EKey key, DKey dkey) {
		String time = new SimpleDateFormat("MM.dd.HH.mm.ss").format(new Date());
		message = encrypt.encrypt(message, key);
		try {
			out.writeObject(new ChatMessage(time, username, message, recipient));
		} catch (IOException e) {
			System.out.println("Whoops?");
		}


		return decrypt.decrypt(message, dkey);
	}

	void sendMessage(String message, String recipient) throws IOException {
		String time = new SimpleDateFormat("MM.dd.HH.mm.ss").format(new Date());

		//get the encryption key from the server
		/**TODO: This will need to wait for message receival
		 * if(publicKey == null) {
			try {
				publicKey = requestKey(recipient);
			}
			catch(Exception e) {
				return;
			}
		}**/
		
		//encrypt the message using the public key for the recipient
		
		/*TODO: This will need to wait until message receival is sorted out.
		 * message = encrypt.encrypt(message, publicKey);
		 */
		out.writeObject(new ChatMessage(time, username, message, recipient));
	}

	public EKey requestKey(String target) throws IOException, ClassNotFoundException{
		out.writeObject(target);
		Object obj = in.readObject();
		if(obj instanceof EKey){
			System.out.println("Key recieved, n = " + ((EKey)obj).getN() + " and e = " + ((EKey)obj).getE());
			return (EKey)obj;
		}
		return null;
	}
	public Collection getRecipientList() throws ClassNotFoundException, IOException{
		Collection clients = null;
		out.writeObject(clients);
		Object obj = in.readObject();
		if(obj instanceof Collection){
			return (Collection)obj;
		}
		return null;
	}
	public static void main(String args[]){
		try{
			Client client = new Client("127.0.0.1","Anybody", new EKey(143, 7));
			BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
			String inputLine;
			while((inputLine = sysin.readLine())!=null){
				System.out.println("Message sent:" + inputLine);
				client.sendMessage(inputLine,"TestClient");
			}
		}
		catch(Exception e){

		}
	}
}
/* Class that extends thread and just sits listening for something to come in on the object stream.
 * once something comes in it prints to system out.
 */
class MessageReceiver extends Thread{
		ObjectInputStream in;
		Client client;
		public MessageReceiver(ObjectInputStream in,Client client){
			this.in = in;
			this.client = client;
		}
	    public void run() {
	    	Object obj;
	    	try {
	    		//Continously reads in from object stream and handles the incoming messages from the server
				while((obj = in.readObject())!=null){
					if(obj instanceof ChatMessage){
						ChatMessage message = (ChatMessage)obj;
						System.out.println(message);						
					}
				}
			}
	    	catch(Exception e){
	    		System.out.println("Message Receiver intterrupted.");
	    		e.printStackTrace();
	    	}
	    }
	}
