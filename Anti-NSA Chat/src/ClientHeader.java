import java.io.Serializable;
import java.security.PublicKey;


public class ClientHeader implements Serializable {
	private EKey publicKey;
	private String username;
	public ClientHeader(EKey key, String username){
		this.publicKey = key;
		this.username = username;
	}
	public EKey getKey(){
		return publicKey;
	}
	public String getUsername(){
		return username;
	}
}
