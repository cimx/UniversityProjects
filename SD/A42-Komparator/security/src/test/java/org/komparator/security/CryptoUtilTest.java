package org.komparator.security;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;

import javax.crypto.*;

import org.junit.*;

import static org.junit.Assert.*;

public class CryptoUtilTest {
	
	/** Plain text to digest. */
	private final String plainText = "This is the plain text!";
	/** Plain text bytes. */
	private final byte[] plainBytes = plainText.getBytes();

	/** Asymmetric cryptography algorithm. */
	private static final String ASYM_ALGO = "RSA";
	/** Asymmetric cryptography key size. */
	private static final int ASYM_KEY_SIZE = 2048;
	
	final static String CERTIFICATE = "example.cer";

	final static String KEYSTORE = "example.jks";
	final static String KEYSTORE_PASSWORD = "1nsecure";

	final static String KEY_ALIAS = "example";
	final static String KEY_PASSWORD = "ins3cur3";
	
	private static PrivateKey privateKey;
	private static PublicKey publicKey;
	
    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() throws UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
    	privateKey = CryptoUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE, KEYSTORE_PASSWORD.toCharArray(), 
    															KEY_ALIAS, KEY_PASSWORD.toCharArray());
    	publicKey = CryptoUtil.getX509CertificateFromResource(CERTIFICATE).getPublicKey();

    }

    // tests
    @Test
	public void testCipherPrivateDecipherPublic() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		// generate an RSA key pair
		byte[] cipherBytes = CryptoUtil.asymCipher(plainBytes,privateKey);	
		
		byte[] decipherBytes = CryptoUtil.asymDecipher(cipherBytes,publicKey);		

		assertEquals(new String(decipherBytes), plainText);
	}
    
    @Test
	public void testCipherPublicDecipherPrivate() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		// generate an RSA key pair
		
		byte[] cipherBytes = CryptoUtil.asymCipher(plainBytes,publicKey);	
		
		byte[] decipherBytes = CryptoUtil.asymDecipher(cipherBytes,privateKey);
		
		assertEquals(new String(decipherBytes), plainText);	
	}
    
    @Test(expected = BadPaddingException.class)
	public void wrongKey1() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		// generate an RSA key pair
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ASYM_ALGO);
		keyGen.initialize(ASYM_KEY_SIZE);
		KeyPair keyPair = keyGen.generateKeyPair();

		byte[] cipherBytes = CryptoUtil.asymCipher(plainBytes,publicKey);	
		
		CryptoUtil.asymDecipher(cipherBytes,keyPair.getPrivate());
	}
	@Test(expected = BadPaddingException.class)
	public void wrongKey2() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		// generate an RSA key pair
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ASYM_ALGO);
		keyGen.initialize(ASYM_KEY_SIZE);
		KeyPair keyPair = keyGen.generateKeyPair();

		byte[] cipherBytes = CryptoUtil.asymCipher(plainBytes,keyPair.getPublic());	
		
		CryptoUtil.asymDecipher(cipherBytes,privateKey);
	}
}
