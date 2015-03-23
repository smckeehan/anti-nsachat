import java.io.Serializable;
import java.security.PublicKey;


public class ClientHeader implements Serializable {
	private PublicKey publicKey;
	private String username;
	public ClientHeader(PublicKey publicKey, String username){
		this.publicKey = publicKey;
		this.username = username;
	}
	public PublicKey getKey(){
		return publicKey;
	}
	public String getUsername(){
		return username;
	}
}
