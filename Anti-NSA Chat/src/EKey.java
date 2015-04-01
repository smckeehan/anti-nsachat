import java.io.Serializable;


public class EKey implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int n;
	private int e;
	
	public int getN(){
		return n;
	}
	public void setN(int newN){
		n = newN;
	}
	
	public int getE(){
		return e;
	}
	public void setE(int newE){
		e = newE;
	}
	
	public EKey(int n, int e){
		this.n = n;
		this.e = e;
	}
	public EKey() {	}
}
