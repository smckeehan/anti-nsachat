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
	
	String sendMessage(String message, String recipient, Key key) {
		RSAEncryption encryption = new RSAEncryption();
		RSADecryption decryption = new RSADecryption();
		
		String time = new SimpleDateFormat("MM.dd.HH.mm.ss").format(new Date());
		message = encryption.encrypt(message, key);
		try {
			out.writeObject(new ChatMessage(time, username, message, recipient));
		} catch (IOException e) {
			System.out.println("Whoops?");
		}
		
		
		return decryption.decrypt(message, key);
	}
	
	void sendMessage(String message, String recipient) throws IOException {
		String time = new SimpleDateFormat("MM.dd.HH.mm.ss").format(new Date());
		out.writeObject(new ChatMessage(time, username, message, recipient));
	}
	public PublicKey requestKey(String target) throws IOException, ClassNotFoundException{
		EKey key = null;
		out.writeObject(target);
		Object obj = in.readObject();
		if(obj instanceof PublicKey){
			return (PublicKey)obj;
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
