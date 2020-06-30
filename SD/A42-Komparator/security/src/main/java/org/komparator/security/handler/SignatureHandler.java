package org.komparator.security.handler;

import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CryptoUtil;
import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class SignatureHandler implements SOAPHandler<SOAPMessageContext> {
	
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	final static String KEYSTORE_PASSWORD = "2LBjRclo";
	final static String KEY_PASSWORD = "2LBjRclo";
	final static String CERTIFICATE = "ca.cer";
	
	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
    
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("SignatureHandler: Handling message.");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			if (outboundElement.booleanValue()) {
				System.out.println("Writing header in outbound SOAP message...");
				
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				
				SOAPBody sb = se.getBody();
				if (sb == null) {
					sb = se.addBody();
				}
				
				SOAPHeader sh = se.getHeader();
				if (sh == null) { 
					sh = se.addHeader(); 
				}

				Name name = se.createName("name", "name", "http://name.komparator.org/");
				SOAPHeaderElement elname = sh.addHeaderElement(name);
				elname.addTextNode(CryptoUtil.getName());
				msg.saveChanges();
				
				Name sign = se.createName("signature", "sig", "http://signature.komparator.org/");
				SOAPHeaderElement elsign = sh.addHeaderElement(sign);
				msg.saveChanges();
				
				String timestamp = getSOAPElement(se, sh, "timestamp").getValue();
				
				String keystore = CryptoUtil.getName() + ".jks";
				String key_alias = CryptoUtil.getName().toLowerCase();
				byte[] signaturebytes = DatatypeConverter.parseBase64Binary(sb.toString() + timestamp);
				String signature = DatatypeConverter.printBase64Binary(CryptoUtil.makeDigitalSignature(signaturebytes, CryptoUtil.getPrivateKeyFromKeyStoreResource(keystore, KEYSTORE_PASSWORD.toCharArray(), key_alias, KEY_PASSWORD.toCharArray())));
				elsign.addTextNode(signature);
				msg.saveChanges();
				
				System.out.println("Mensagem pronta a enviar.");
								
				return true;				

			} else {
				System.out.println("Reading header in inbound SOAP message...");

				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();
				SOAPHeader sh = se.getHeader();
				
				// check header
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}
				
				String name = getSOAPElement(se, sh, "name").getValue();
				String timestamp = getSOAPElement(se, sh, "timestamp").getValue();
				String signature = getSOAPElement(se, sh, "signature").getValue();
				
				CAClient caClient = new CAClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");
				String cert = caClient.getCertificate(name);
				Certificate certificate = CryptoUtil.getX509CertificateFromPEMString(cert);
				
				if(!CryptoUtil.verifySignedCertificate(certificate, CryptoUtil.getX509CertificateFromResource(CERTIFICATE).getPublicKey())){
					throw new RuntimeException();
				}
				
				byte[] received = DatatypeConverter.parseBase64Binary(sb.toString() + timestamp);
				byte[] sign = DatatypeConverter.parseBase64Binary(signature);
				if(!CryptoUtil.verifyDigitalSignature(certificate, received, sign)) 
					throw new RuntimeException();
				
				System.out.println("Mensagem verificada e recebida com sucesso.");
				
				return true;
			}
			
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}

    		return true;
    }
	
	public SOAPElement getSOAPElement(SOAPEnvelope se, SOAPHeader sh, String name) throws SOAPException{
		Name sign;
		sign = se.createName(name, name, "http://" + name + ".komparator.org/");
		Iterator it = sh.getChildElements(sign);
		if (!it.hasNext()){
			throw new SOAPException();
		}
		return (SOAPElement) it.next();
	}
}
