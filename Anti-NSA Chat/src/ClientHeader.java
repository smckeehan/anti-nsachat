import java.io.Serializable;
import java.security.PublicKey;


public class ClientHeader implements Serializable {
	private EKey publicKey;
	private String username;
	public ClientHeader(EKey publicKey, String username){
		this.publicKey = publicKey;
		this.username = username;
	}
	public EKey getKey(){
		return publicKey;
	}
	public String getUsername(){
		return username;
	}
}
