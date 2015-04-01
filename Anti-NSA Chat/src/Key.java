
public class Key {
	private int n;
	private int e;
	private int d;
	
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
	
	public int getD(){
		return d;
	}
	public void setD(int newD){
		d = newD;
	}
	
	public Key(int n, int e, int d){
		this.n = n;
		this.e = e;
		this.d = d;
	}
	
	public Key(){}
}
