import java.io.Serializable;

/**
 * A class that wraps each message sent, so that we can attach the recipient,
 * sender, and a timestamp along with the message. Implements Serializable so that it can be sent via an ObjectStream
 */
public class ChatMessage implements Serializable{
	private String username;
	private String recipient;
	private String message;
	private String time;
	/**
	 * Constructor for creating a ChatMessage.
	 * 
	 * @param time The time the message was sent. Automatically is given within the client's sendmessage function using SimpleDateFormat
	 * @param username The username of the person who sent the message.
	 * @param message The message that is to be sent(will be an encrypted string, encrypted via the recipients public key)
	 * @param recipient The recipient of the message.
	 * @see Client#sendMessage(String, String)
	 */
	public ChatMessage(String time, String username, String message, String recipient){
		this.username = username;
		this.message = message;
		this.time = time;
		this.recipient = recipient;
	}
	
	/**
	 * Returns the string held within this ChatMessage.
	 * 
	 * @return the encrypted message string.
	 */
	public String getMessage(){
		return message;
	}
	
	/**
	 * Sets the message of this chat object(in case it needs to be changed or set  after the object is made)
	 * 
	 * @param newMessage The new message that this ChatMessage should contain.
	 */
	public void setMessage(String newMessage) {
		message = newMessage;
	}
	/**
	 * Returns the time stamp of this message
	 * 
	 * @return The time stamp of the message, the format of which is dependent on how the client sets it.
	 */
	public String getTime(){
		return time;
	}
	/**
	 * Returns the username of the person who is sending the ChatMessage
	 * 
	 * @return this ChatMessage's sender
	 */
	public String getUsername(){
		return username;
	}
	/**
	 * Returns the username of the person who this ChatMessage is intended to go to.
	 * 
	 * @return the intended recipient of the message.
	 */
	public String getRecipient(){
		return recipient;
	}
	
	/**
	 * Override the typical toString so that the message can easily be converted to a format displayable by clients.
	 */
	@Override
	public String toString(){
		return time + " " + username + ": " + message;
	}
	
}
