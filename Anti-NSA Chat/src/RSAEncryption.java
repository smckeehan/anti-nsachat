import java.security.PublicKey;


public class RSAEncryption {
	public String encrypt(String message, Key key) {
		
		//convert the string to an array of characters, then to an array of ascii integers
		char[] messageArray = message.toCharArray();
		int[]asciiArray = new int[messageArray.length];
		
		for(int i = 0; i < messageArray.length; i++){
			asciiArray[i] = (int) messageArray[i];
		}
		
		//alter the ascii integers according to the key
		for(int i = 0; i < asciiArray.length; i++){
			asciiArray[i] = convert(asciiArray[i], key);
		}
		
		//convert the encrypted ascii to chars, then to a string
		for(int i = 0; i < asciiArray.length; i++) {
			messageArray[i] = (char) asciiArray[i];
		}
		String result = "";
		for(int i = 0; i < messageArray.length; i++) {
			result += messageArray[i];
		}
		return result;
	}
	
	public int convert(int i, Key key) {
		//get the encryption component of the key
		int e = key.getE();
		int n = key.getN();
		
		//raise the message integer to the "e" power, then mod it by 128, the size of the ascii table
		int result = 1;
		for(int j = 0; j < e; j++) {
			result = result * i;
			result = result % n;
		}
		
		//return the new encrytped ascii char
		return result;
	}
}
