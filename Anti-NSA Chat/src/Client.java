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
 	
	public Client(String server, String username, EKey key) throws UnknownHostException, IOException{
		this.username = username;
		socket = new Socket(server,9898);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        out.writeObject(new ClientHeader(key,this.username));
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
		EKey recKey = null;
		try {
			recKey = requestKey(recipient);
		}
		catch(Exception e) {
			return;
		}
		
		//encrypt the message using the public key for the recipient
		message = encrypt.encrypt(message, recKey);
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
		if(obj instanceof PublicKey){
			return (Collection)obj;
		}
		return null;
	}
	public static void main(String args[]){
		try{
			Client client = new Client("127.0.0.1","TestClient", new EKey(143, 7));
			BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
			String inputLine;
			while((inputLine = sysin.readLine())!=null){
				System.out.println("Message sent:" + inputLine);
				client.sendMessage(inputLine,"Anybody");
			}
		}
		catch(Exception e){
			
		}
	}
}
