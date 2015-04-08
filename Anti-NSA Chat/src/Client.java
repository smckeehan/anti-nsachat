import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Essentially the controller that allows clients to interact with the view(ClientGui) and the(kind of) model(Sever). Contains
 * all of the necessary methods for sending and receiving information from the server, and passing it along to the gui.
 */
public class Client {
	Socket socket;//The socket the client connects on
	String username;//The clients username
	String recipient;//The recipient of messages.
	EKey encryptKey;//The key used for encryption
	DKey decryptKey;//The key used for decryption
	ClientGUI clientGUI;//The GUI paired with this client.
	ObjectInputStream in;//The stream used to received information from the server
	ObjectOutputStream out;//The stream used to send information to the server
	RSAEncryption encrypt = new RSAEncryption();//The encryptor
	RSADecryption decrypt = new RSADecryption();//The decryptor
	EKey publicKey;//The encryption key
	ChatMessage storedMessage;//temporary message storage

	MessageReceiver messageReceiver;//A Thread who's focus is to receive messages
	
	/**
	 * Constructor for creating a Client. If a exception is thrown(I.e. a connection couldn't be established) then
	 * returns a null.
	 * 
	 * @param server The IP Address of the server
	 * @param username Username for the client.
	 * @param publicKey Clients public key
	 * @param decryptKey Cilents private key
	 * @param gui The clients gui interface
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Client(String server, int port, String username, EKey publicKey, DKey decryptKey, ClientGUI gui) throws UnknownHostException, IOException{
		this.username = username; //This clients username
		socket = new Socket(server,port); //The socket this client communicates on.
		this.publicKey = publicKey;
		this.decryptKey = decryptKey;
		clientGUI = gui;
		
		//initializing object streams on the socket, and write ClientHeader.
		//ClientHeader contains information that is required for the server to accept the connection, so if this doesn't happen
		//the client won't be able to connect.
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
		out.writeObject(new ClientHeader(publicKey,this.username));

		//Starts thread to begin receiving messages, once the connection to the server has been established.
		messageReceiver = new MessageReceiver(in, this);
		messageReceiver.start();
	}

	public boolean start() {
		return true;
	}

	public void end(){
		messageReceiver.close();
		messageReceiver = null;
		try {
			out.close();
			in.close();
		} catch (IOException e) {
			System.out.println("Closing failed.");
		}

	}
	
	/**
	 * The method used to send messages to the server, this allows for changing recipients by re-requesting keys
	 * @param message, the chat message to send out
	 * @param recipient, the target of the message
	 * @throws IOException if there is a problem sending
	 * 
	 */
	void sendMessage(String message, String recipient) throws IOException {
		String time = new SimpleDateFormat("MM.dd.HH.mm.ss").format(new Date());

		//get the encryption key from the server
		if(encryptKey == null || !recipient.equals(this.recipient)) {
			//store the message to be sent later
			storedMessage = new ChatMessage(time, username, message, recipient);
			
			//try to request the key of the recipient
			try {
				requestKey(recipient);
				this.recipient = recipient;
			}
			catch(Exception e) {
				return;
			}
			//there is nothing more to do here, wait for the key
		}

		else {
			//clear out the stored message
			storedMessage = null;
			
			//encrypt the message using the public key for the recipient
			message = encrypt.encrypt(message, encryptKey);

			out.writeObject(new ChatMessage(time, username, message, recipient));
		}
	}

	/**
	 * Sends out a KeyRequest object to the server, in order to get the public key for the target
	 * @param target, the target of the message
	 * @throws IOException if something goes wrong
	 * @throws ClassNotFoundException
	 */
	public void requestKey(String target) throws IOException, ClassNotFoundException{
		KeyRequest request = new KeyRequest(username, target);
		out.writeObject(request);
	}
	
	/**
	 * A message has been recieved from the server, should decrypt and send to the gui
	 * @param message, the message recieved from the server
	 * 
	 */
	public void recieved(ChatMessage message){
		
		//get the message out of the object and decrypt it
		String text = message.getMessage();
		message.setMessage(decrypt.decrypt(text, decryptKey));
		
		//send the newly decrypted message object to the gui
		clientGUI.printMessage(message);
	}
	
	/**
	 * The server sent a message directly, no need to decrypt it, just send straight to the gui
	 * @param message, the string to send to the gui
	 */
	public void serverMessage(String message) {
		clientGUI.serverMessage(message);
	}

	/**
	 * Key request recieved from the server
	 * @param key
	 */
	public void setKey(EKey key) throws IOException {
		//first set the public key as the recieved key
		encryptKey = key;
		
		//now encrypt and send it out
		String cypher = encrypt.encrypt(storedMessage.getMessage(), encryptKey);
		out.writeObject(new ChatMessage(storedMessage.getTime(), username, cypher, storedMessage.getRecipient()));
	}

	public static void main(String args[]){
		try{
			Client client = new Client("127.0.0.1",9898,"Anybody", new EKey(143, 7), new DKey(143, 103), null);
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
	ObjectInputStream in;//The input stream
	Client client;
	static boolean run;
	
	public MessageReceiver(ObjectInputStream in,Client client){
		this.in = in;
		this.client = client;
		run = true;
	}
	
	public void run() {
		Object obj;
		try {
			//Continously reads in from object stream and handles the incoming messages from the server
			while((obj = in.readObject())!=null){
				if(obj instanceof ChatMessage){
					ChatMessage message = (ChatMessage)obj;
					//just send out the message
					client.recieved(message);
				}
				else if(obj instanceof EKey) {
					client.setKey((EKey) obj);
				}
				else if(obj instanceof String) {
					client.serverMessage((String)obj);
				}
			}
		}
		catch(Exception e){
			//this depends on if the thread is meant to run or not
			if(run) {
				//the thread should be running, there's something wrong
				System.out.println("Message Receiver intterrupted.");
				e.printStackTrace();
			}
			else {
				//the thread shouldn't be running, all is well
				System.out.println("Message Reciever logged out.");
			}
		}
	}

	/**
	 * close the thread
	 */
	public void close(){
		in = null;
		run = false;
	}
}
