import java.io.Serializable;


public class ChatMessage implements Serializable{
	private String username;
	private String recipient;
	private String message;
	private String time;
	public ChatMessage(String time, String username, String message, String recipient){
		this.username = username;
		this.message = message;
		this.time = time;
		this.recipient = recipient;
	}
	
	public String getMessage(){
		return message;
	}
	public String getTime(){
		return time;
	}
	public String getUsername(){
		return username;
	}
	public String getRecipient(){
		return recipient;
	}
	@Override
	public String toString(){
		return time + " " + username + ": " + message;
	}
	
}
