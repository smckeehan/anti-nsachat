import java.io.Serializable;

/**
 * Object sent to server that is a key request,
 * allows for the server to send a message to user if the recipient doesn't exist
 * 
 * @author Kyle Timmerman
 *
 */
public class KeyRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	private String username;
	private String recipient;
	
	public KeyRequest (String user, String rec) {
		username = user;
		recipient = rec;
	}
	
	public String getRecipient() {
		return recipient;
	}
	
	public String getUsername() {
		return username;
	}
	
}
