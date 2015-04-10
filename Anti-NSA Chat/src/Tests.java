import static org.junit.Assert.*;

import org.junit.Test;

/*
 * A test class that tests the classes and their methods
 */

public class Tests {

	/*
	 * test the encryption
	 */
	@Test
	public void testEncryption() {
		RSAEncryption encryption = new RSAEncryption();
		String temp = "Test";
		String eTemp;
		EKey ekey = new EKey(143, 7);

		eTemp = encryption.encrypt(temp, ekey);

		if(eTemp.equals(temp)) {
			fail("Did not encrypt properly");
		}
	}

	/*
	 * Test the decryption
	 */
	@Test
	public void testDecryption(){
		RSAEncryption encryption = new RSAEncryption(); 
		RSADecryption decryption = new RSADecryption();
		String temp = "Test";
		String eTemp;
		String dTemp;
		EKey ekey = new EKey(143, 7);
		DKey dkey = new DKey(143, 103);

		eTemp = encryption.encrypt(temp, ekey);
		dTemp = decryption.decrypt(eTemp, dkey);
		
		if(!dTemp.equals(temp)){
			fail("Did not decrypt properly");
		}
	}
	
	/*
	 * test the client message
	 */
	@Test
	public void testClientMessage(){
		ChatMessage message = new ChatMessage(null, "TestSender", "Test Message", "TestRecipient");
		
		if(!message.getUsername().equals("TestSender")){
			fail("Did not return correct username");
		}
		
		if(!message.getMessage().equals("Test Message")){
			fail("Did not return correct message");
		}
		
		if(!message.getRecipient().equals("TestRecipient")){
			fail("Did not return correct message");
		}
	}
	
	/*
	 * test the client header
	 */
	@Test
	public void testClientHeader(){
		
		EKey ekey = new EKey(143, 7);
		ClientHeader header = new ClientHeader(ekey, "TestSender");
		
		if(header.getKey()!=ekey){
			fail("Did not return correct EKey");
		}
		
		if(!header.getUsername().equals("TestSender")){
			fail("Did not reutnr correct Usernamer");
		}
	}
	
	/*
	 * test the key request
	 */
	@Test
	public void testKeyRequest(){
		KeyRequest request = new KeyRequest("TestSender", "TestRecipient");
		
		if(!request.getUsername().equals("TestSender")){
			fail("Did not reutrn correct Username");
		}
		
		if(!request.getRecipient().equals("TestRecipient")){
			fail("Did not reutrn correct Recipient");
		}
	}
	
	@Test
	public void testKey(){
		Key key = new Key(143, 7, 103);
		
		if(key.getN() != 143){
			fail("Returned wrong Nkey");
		}
		
		if(key.getE() != 7){
			fail("Returned wrong EKey");
		}
		
		if(key.getD() != 103){
			fail("REturned wrong DKey");
		}
	}
}
