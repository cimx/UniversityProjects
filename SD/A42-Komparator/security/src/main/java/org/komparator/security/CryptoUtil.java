package org.komparator.security;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.security.*;
import javax.crypto.*;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;

public class CryptoUtil {

	private static String _name;
	private static final String ASYM_CIPHER = "RSA/ECB/PKCS1Padding";
	private static final String SIGNATURE_ALGO = "SHA256withRSA";
	
	public static void setName(String name){ _name = name; }
	public static String getName() { return _name; }

	public static byte[] asymCipher(byte[] bytes, Key key) throws InvalidKeyException, IllegalBlockSizeException,
	 											 BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException{		 
		 
		 Cipher cipher = Cipher.getInstance(ASYM_CIPHER);
		 
		 cipher.init(Cipher.ENCRYPT_MODE, key);
		
		 byte[] cipherBytes = cipher.doFinal(bytes);
		 
		 return cipherBytes;
	 }
	 
	public static byte[] asymDecipher(byte[] bytes, Key key) throws InvalidKeyException, NoSuchAlgorithmException,
	 												NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{

		 Cipher cipher = Cipher.getInstance(ASYM_CIPHER);
		 
		 cipher.init(Cipher.DECRYPT_MODE, key);
		 
		 byte[] decipheredBytes = cipher.doFinal(bytes);
		 
		 return decipheredBytes;
	 }
	
	/**
	 * Signs the input bytes with the private key and returns the bytes. If
	 * anything goes wrong, null is returned (swallows exceptions).
	 * 
	 * @param privateKey
	 * @param bytesToSign
	 * @return bytes of resulting signature
	 */
	public static byte[] makeDigitalSignature(final byte[] bytesToSign, final PrivateKey privateKey) {
		try {
			Signature sig = Signature.getInstance(SIGNATURE_ALGO);
			sig.initSign(privateKey);
			sig.update(bytesToSign);
			return sig.sign();
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			if (outputFlag) {
				System.err.println("Caught exception while making signature: " + e);
				System.err.println("Returning null.");
			}
			return null;
		}
	}
	
	
	/**
	 * Verify signature of bytes with the public key. If anything goes wrong,
	 * returns false (swallows exceptions).
	 * 
	 * @param publicKey
	 * @param bytesToVerify
	 * @param signature
	 * @return
	 */
	public static boolean verifyDigitalSignature(PublicKey publicKey, byte[] bytesToVerify, byte[] signature) {
		try {
			Signature sig = Signature.getInstance(SIGNATURE_ALGO);
			sig.initVerify(publicKey);
			sig.update(bytesToVerify);
			return sig.verify(signature);
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			if (outputFlag) {
				System.err.println("Caught exception while verifying signature " + e);
				System.err.println("Returning false.");
			}
			return false;
		}
	}
	
	/**
	 * Verify signature of bytes with the public key contained in the
	 * certificate. If anything goes wrong, returns false (swallows exceptions).
	 * 
	 * @param publicKeycertificate
	 * @param bytesToVerify
	 * @param signature
	 * @return
	 */
	public static boolean verifyDigitalSignature(Certificate publicKeyCertificate, byte[] bytesToVerify, byte[] signature) {
		return verifyDigitalSignature(publicKeyCertificate.getPublicKey(), bytesToVerify, signature);
	}
	
	/**
	 * Checks if the certificate was properly signed by the CA with the provided
	 * public key.
	 * 
	 * @param certificate
	 *            Certificate to be verified
	 * @param caPublicKey
	 *            CA public key certificate
	 * @return true if properly signed
	 */
	public static boolean verifySignedCertificate(Certificate certificate, PublicKey caPublicKey) {
		try {
			certificate.verify(caPublicKey);
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException e) {
			if (outputFlag) {
				System.err.println("Caught exception while verifying certificate with CA public key : " + e);
				System.err.println("Returning false.");
			}
			return false;
		}
		return true;
	}

		
	/** print some error messages to standard error. */
	public static boolean outputFlag = true;

	/**
	 * Returns the public key from a certificate.
	 * 
	 * @param certificate
	 * @return public key
	 */
	public static PublicKey getPublicKeyFromCertificate(Certificate certificate) {
		return certificate.getPublicKey();
	}

	/**
	 * Gets a Certificate object from an input stream.
	 * 
	 * @param in
	 *            InputStream
	 * @return the certificate
	 * @throws CertificateException
	 */
	public static Certificate getX509CertificateFromStream(InputStream in) throws CertificateException {
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			Certificate cert = certFactory.generateCertificate(in);
			return cert;
		} finally {
			closeStream(in);
		}
	}

	/**
	 * Converts a byte array to a Certificate object. Returns null if the bytes
	 * do not correspond to a certificate.
	 * 
	 * @param bytes
	 *            the byte array to convert
	 * @return the certificate
	 * @throws CertificateException
	 */
	public static Certificate getX509CertificateFromBytes(byte[] bytes) throws CertificateException {
		InputStream in = new ByteArrayInputStream(bytes);
		return getX509CertificateFromStream(in);
	}

	/**
	 * Returns a Certificate object given a string with a certificate in the PEM
	 * format.
	 * 
	 * @param certificateString
	 *            the String with the certificate in PEM format.
	 * @return the Certificate
	 * @throws CertificateException
	 */
	public static Certificate getX509CertificateFromPEMString(String certificateString) throws CertificateException {
		byte[] bytes = certificateString.getBytes(StandardCharsets.UTF_8);
		return getX509CertificateFromBytes(bytes);
	}

	/**
	 * Reads a certificate from a resource (included in the application
	 * package).
	 * 
	 * @param certificateResourcePath
	 * @return the Certificate
	 * @throws IOException
	 * @throws CertificateException
	 */
	public static Certificate getX509CertificateFromResource(String certificateResourcePath)
			throws IOException, CertificateException {
		InputStream is = getResourceAsStream(certificateResourcePath);
		return getX509CertificateFromStream(is);
	}


	// (private) key store ----------------------------------------------------

	/**
	 * Reads a PrivateKey from a key-store.
	 * 
	 * @param keyStore
	 *            key store
	 * @param keyAlias
	 *            name of the key to retrieve
	 * @param keyPassword
	 *            key password
	 * @return The PrivateKey
	 * @throws FileNotFoundException
	 * @throws KeyStoreException
	 */
	public static PrivateKey getPrivateKeyFromKeyStore(String keyAlias, char[] keyPassword, KeyStore keystore)
			throws KeyStoreException, UnrecoverableKeyException {
		PrivateKey key;
		try {
			key = (PrivateKey) keystore.getKey(keyAlias, keyPassword);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException(e);
		}
		return key;
	}

	/**
	 * Reads a PrivateKey from a key-store resource.
	 * 
	 * @param keyStoreResourcePath
	 *            key store resource path
	 * @param keyStorePassword
	 *            key store password
	 * @param keyAlias
	 *            name of the key to retrieve
	 * @param keyPassword
	 *            key password
	 * @return The PrivateKey
	 * @throws FileNotFoundException
	 * @throws KeyStoreException
	 */
	public static PrivateKey getPrivateKeyFromKeyStoreResource(String keyStoreResourcePath, char[] keyStorePassword,
			String keyAlias, char[] keyPassword)
			throws FileNotFoundException, KeyStoreException, UnrecoverableKeyException {
		KeyStore keystore = readKeystoreFromResource(keyStoreResourcePath, keyStorePassword);
		return getPrivateKeyFromKeyStore(keyAlias, keyPassword, keystore);
	}

	/**
	 * Reads a PrivateKey from a key-store file.
	 * 
	 * @param keyStoreFile
	 *            key store file
	 * @param keyStorePassword
	 *            key store password
	 * @param keyAlias
	 *            name of the key to retrieve
	 * @param keyPassword
	 *            key password
	 * @return The PrivateKey
	 * @throws FileNotFoundException
	 * @throws KeyStoreException
	 */
	public static PrivateKey getPrivateKeyFromKeyStoreFile(File keyStoreFile, char[] keyStorePassword, String keyAlias,
			char[] keyPassword) throws FileNotFoundException, KeyStoreException, UnrecoverableKeyException {
		KeyStore keystore = readKeystoreFromFile(keyStoreFile, keyStorePassword);
		return getPrivateKeyFromKeyStore(keyAlias, keyPassword, keystore);
	}

	/**
	 * Reads a PrivateKey from a key store in given file path.
	 * 
	 * @param keyStoreFilePath
	 *            path to key store file
	 * @param keyStorePassword
	 *            key store password
	 * @param keyAlias
	 *            name of the key to retrieve
	 * @param keyPassword
	 *            key password
	 * @return The PrivateKey
	 * @throws FileNotFoundException
	 * @throws KeyStoreException
	 */
	public static PrivateKey getPrivateKeyFromKeyStoreFile(String keyStoreFilePath, char[] keyStorePassword,
			String keyAlias, char[] keyPassword)
			throws FileNotFoundException, KeyStoreException, UnrecoverableKeyException {
		return getPrivateKeyFromKeyStoreFile(new File(keyStoreFilePath), keyStorePassword, keyAlias, keyPassword);
	}

	/**
	 * Reads a KeyStore from a stream.
	 * 
	 * @param keyStoreInputStream
	 *            key store stream
	 * @param keyStorePassword
	 *            key store password
	 * @return The read KeyStore
	 * @throws FileNotFoundException
	 * @throws KeyStoreException
	 */
	private static KeyStore readKeystoreFromStream(InputStream keyStoreInputStream, char[] keyStorePassword)
			throws KeyStoreException {
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		try {
			keystore.load(keyStoreInputStream, keyStorePassword);
		} catch (Exception e) {
			throw new KeyStoreException("Could not load key store", e);
		} finally {
			closeStream(keyStoreInputStream);
		}
		return keystore;
	}

	/**
	 * Reads a KeyStore from a resource.
	 * 
	 * @param keyStoreResourcePath
	 *            key store resource path
	 * @param keyStorePassword
	 *            key store password
	 * @return The read KeyStore
	 * @throws FileNotFoundException
	 * @throws KeyStoreException
	 */
	public static KeyStore readKeystoreFromResource(String keyStoreResourcePath, char[] keyStorePassword)
			throws KeyStoreException {
		InputStream is = getResourceAsStream(keyStoreResourcePath);
		return readKeystoreFromStream(is, keyStorePassword);
	}

	/**
	 * Reads a KeyStore from a file.
	 * 
	 * @param keyStoreFile
	 *            key store file
	 * @param keyStorePassword
	 *            key store password
	 * @return The read KeyStore
	 * @throws FileNotFoundException
	 * @throws KeyStoreException
	 */
	private static KeyStore readKeystoreFromFile(File keyStoreFile, char[] keyStorePassword)
			throws FileNotFoundException, KeyStoreException {
		FileInputStream fis = new FileInputStream(keyStoreFile);
		return readKeystoreFromStream(fis, keyStorePassword);
	}

	/**
	 * Reads a KeyStore from a file path.
	 * 
	 * @param keyStoreFilePath
	 *            path to key store file
	 * @param keyStorePassword
	 *            key store password
	 * @return The read KeyStore
	 * @throws FileNotFoundException
	 * @throws KeyStoreException
	 */
	public static KeyStore readKeystoreFromFile(String keyStoreFilePath, char[] keyStorePassword)
			throws FileNotFoundException, KeyStoreException {
		return readKeystoreFromFile(new File(keyStoreFilePath), keyStorePassword);
	}

	
	// resource stream helpers ------------------------------------------------

		/** Method used to access resource. */
		private static InputStream getResourceAsStream(String resourcePath) {
			// uses current thread's class loader to also work correctly inside
			// application servers
			// reference: http://stackoverflow.com/a/676273/129497
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
			return is;
		}

		/** Do the best effort to close the stream, but ignore exceptions. */
		private static void closeStream(InputStream in) {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				// ignore
			}
		}
}
