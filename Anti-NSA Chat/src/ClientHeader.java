import java.io.Serializable;
import java.security.PublicKey;

/**
 * A Client Header object, which is the first thing any client will send to the server upon connection.
 * This gives the server pertinent information that is necessary to establish a connection. If a client sends
 * something else initially, the server drops the connection immediately.
 */
public class ClientHeader implements Serializable {
	private EKey publicKey;//The clients public key
	private String username;//The clients username
	
	/**
	 * Instantiates a client header so that it contains the clients public key and username to be sent off to the server.
	 * 
	 * @param publicKey The RSA public key of the client who is connecting to the server.
	 * @param username The username of the client that is connecting to the server.
	 */
	public ClientHeader(EKey publicKey, String username){
		this.publicKey = publicKey;
		this.username = username;
	}
	
	/**
	 * Returns the public key of this client.
	 * 
	 * @return This client's public key.
	 */
	public EKey getKey(){
		return publicKey;
	}
	
	/**
	 * Returns the username of this client.
	 * 
	 * @return This client's username.
	 */
	public String getUsername(){
		return username;
	}
}
