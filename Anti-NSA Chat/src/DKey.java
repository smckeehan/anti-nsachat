
public class DKey {
	private int n;
	private int d;
	
	public int getN(){
		return n;
	}
	public void setN(int newN){
		n = newN;
	}
	
	public int getD(){
		return d;
	}
	public void setD(int newD){
		d = newD;
	}
	
	public DKey(int n, int d){
		this.n = n;
		this.d = d;
	}
	
	public DKey(){}
}
