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
 	ObjectInputStream in;
 	ObjectOutputStream out;
	public Client(String server, String username) throws UnknownHostException, IOException{
		this.username = username;
		socket = new Socket(server,9898);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        out.writeObject(new ClientHeader(null,this.username));
	}
	public boolean start() {
		return false;
	}
	
	void sendMessage(String message, String recipient) throws IOException {
		String time = new SimpleDateFormat("MM.dd.HH.mm.ss").format(new Date());
		out.writeObject(new ChatMessage(time, username, message, recipient));
	}
	public PublicKey requestKey(String username ) throws IOException, ClassNotFoundException{
		PublicKey key = null;
		out.writeObject(key);
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
			Client client = new Client("127.0.0.1","TestClient");
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
