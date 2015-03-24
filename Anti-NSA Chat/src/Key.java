
public class Key {
	private int n;
	private int e;
	private int d;
	
	public int getN(){
		return n;
	}
	public void setN(String newN){
		char[] chars = newN.toCharArray();
		int result = 0;
		for(int i = 0; i < chars.length; i++){
			int num = ((int)chars[i]) - 48;
			result += num * Math.pow(10, chars.length - 1 - i);
		}
		n = result;
	}
	
	public int getE(){
		return e;
	}
	public void setE(String newE){
		char[] chars = newE.toCharArray();
		int result = 0;
		for(int i = 0; i < chars.length; i++){
			int num = ((int)chars[i]) - 48;
			result += num * Math.pow(10, chars.length - 1 - i);
		}
		e = result;
	}
	
	public int getD(){
		return d;
	}
	public void setD(String newD){
		char[] chars = newD.toCharArray();
		int result = 0;
		for(int i = 0; i < chars.length; i++){
			int num = ((int)chars[i]) - 48;
			result += num * Math.pow(10, chars.length - 1 - i);
		}
		d = result;
	}
	
	public Key(int n, int e, int d){
		this.n = n;
		this.e = e;
		this.d = d;
	}
	
	public Key(){}
}
