import java.io.Serializable;

/**
 *The encrypt key used by the encryption object 
 * @author Kyle Timmerman
 *
 */
public class EKey implements Serializable{
	private static final long serialVersionUID = 1L;
	private int n;
	private int e;
	
	public EKey (int n, int e) {
		this.n = n;
		this.e = e;
	}
	public EKey () { }
	
	public void setN(int n) {
		this.n = n;
	}
	public int getN() {
		return n;
	}
	
	public void setE(int e) {
		this.e = e;
	}
	public int getE() {
		return e;
	}
}
