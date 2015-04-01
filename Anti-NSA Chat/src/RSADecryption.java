
public class RSADecryption {
	public String decrypt(String cypher, DKey key){
		//turn the cyphertext to a char array, then to an ascii array
		char[] cypherArray = cypher.toCharArray();
		int[] asciiArray = new int[cypherArray.length];

		for(int i = 0; i < asciiArray.length; i++) {
			asciiArray[i] = (int) cypherArray[i];
		}

		//alter the ascii integers according to the key
		for(int i = 0; i < asciiArray.length; i++){
			asciiArray[i] = convert(asciiArray[i], key);
		}

		//convert the encrypted ascii to chars, then to a string
		for(int i = 0; i < asciiArray.length; i++) {
			cypherArray[i] = (char) asciiArray[i];
		}
		String result = new String();
		for(int i = 0; i < cypherArray.length; i++) {
			result += cypherArray[i];
		}
		
		return result;

	}

	public int convert(int i, DKey key) {
		//get the encryption component of the key
		int d = key.getD();
		int n = key.getN();

		//raise the cyphertext integer to the "d" power, then mod it by n
		int result = 1;
		for(int j = 0; j < d; j++){
			result = result * i;
			result = result % n;
		}

		//return the new encrytped ascii char
		return result;
	}
}
