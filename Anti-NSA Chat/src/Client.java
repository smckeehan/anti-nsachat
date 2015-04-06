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


public class Client {
	Socket socket;
	String username;
	EKey encryptKey;
	DKey decryptKey;
	ClientGUI clientGUI;
	ObjectInputStream in;
	ObjectOutputStream out;
	RSAEncryption encrypt = new RSAEncryption();
	RSADecryption decrypt = new RSADecryption();
	EKey publicKey;
	ChatMessage storedMessage;

	MessageReceiver messageReceiver;

	public Client(String server, String username, EKey publicKey, DKey decryptKey, ClientGUI gui) throws UnknownHostException, IOException{
		this.username = username; //This clients username
		socket = new Socket(server,9898); //The socket this client communicates on.
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
			System.out.println("Closing failed...?");
		}

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
		if(encryptKey == null) {
			storedMessage = new ChatMessage(time, username, message, recipient);
			try {
				requestKey(recipient);
			}
			catch(Exception e) {
				return;
			}
		}

		else {
			storedMessage = null;
			//encrypt the message using the public key for the recipient
			message = encrypt.encrypt(message, encryptKey);

			out.writeObject(new ChatMessage(time, username, message, recipient));
		}
	}

	public void requestKey(String target) throws IOException, ClassNotFoundException{
		out.writeObject(target);
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

	public void recieved(ChatMessage message){
		String text = message.getMessage();
		message.setMessage(decrypt.decrypt(text, decryptKey));
		clientGUI.printMessage(message);
	}

	public void setKey(EKey key) {
		encryptKey = key;
		try {
			encryptAndSend();
		} catch (IOException e) {
			System.out.println("The encrypt method failed...");
		}
	}

	public void encryptAndSend() throws IOException {
		String cypher = encrypt.encrypt(storedMessage.getMessage(), encryptKey);
		out.writeObject(new ChatMessage(storedMessage.getTime(), username, cypher, storedMessage.getRecipient()));
	}
	public static void main(String args[]){
		try{
			Client client = new Client("127.0.0.1","Anybody", new EKey(143, 7), new DKey(143, 103), null);
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
					System.out.println(message);
					//why not try to give the client the message?
					client.recieved(message);
				}
				else if(obj instanceof EKey) {
					client.setKey((EKey) obj);
				}
			}
		}
		catch(Exception e){
			if(run) {
				System.out.println("Message Receiver intterrupted.");
				e.printStackTrace();
			}
			else {
				System.out.println("Message Reciever logged out.");
			}
		}
	}

	public void close(){
		in = null;
		run = false;
	}
}
