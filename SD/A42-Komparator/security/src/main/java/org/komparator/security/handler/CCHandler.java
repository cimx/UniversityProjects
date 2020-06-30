package org.komparator.security.handler;

import java.io.ByteArrayOutputStream;
import java.security.cert.Certificate;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
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

public class CCHandler implements SOAPHandler<SOAPMessageContext> {

	final static String CERTIFICATE = "ca.cer";
	
	final static String KEYSTORE_PASSWORD = "2LBjRclo";
	final static String KEY_PASSWORD = "2LBjRclo";
	
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
		System.out.println("CreditCardHandler: Handling message.");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			if (outboundElement.booleanValue()) {
				System.out.println("Writing header in outbound SOAP message...");
				
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();
				SOAPHeader sh = se.getHeader();
				
				if (sh == null) { 
					sh = se.addHeader(); 
				}
				
				//QName svcn = (QName) smc.get(MessageContext.WSDL_SERVICE);
				
				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				
				if (!opn.getLocalPart().equals("buyCart")) {
					return true;
				}
			
				
				NodeList children = sb.getFirstChild().getChildNodes();
				
				for (int i = 0; i < children.getLength(); i++) {
					Node argument = (Node) children.item(i);					
					if (argument.getNodeName().equals("creditCardNr")) {
						String secretArgument = argument.getTextContent();
						// cipher message
						byte[] sa = DatatypeConverter.parseBase64Binary(secretArgument);
						
						CAClient caClient = new CAClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");
						String cert = caClient.getCertificate("A42_Mediator");
						Certificate certificate = CryptoUtil.getX509CertificateFromPEMString(cert);
						
						if(!CryptoUtil.verifySignedCertificate(certificate, CryptoUtil.getX509CertificateFromResource(CERTIFICATE).getPublicKey())){
							throw new RuntimeException();
						}
						
						byte[] cipheredArgument = CryptoUtil.asymCipher(sa, certificate.getPublicKey());
						String encodedSecretArgument = printBase64Binary(cipheredArgument);
						argument.setTextContent(encodedSecretArgument);
						msg.saveChanges();
						
						System.out.println("Numero de cartao de credito encriptado.");
					}
				}
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
				
				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				
				if (!opn.getLocalPart().equals("buyCart")) {
					return true;
				}
				
				NodeList children = sb.getFirstChild().getChildNodes();
				
				for (int i = 0; i < children.getLength(); i++) {
					Node argument = (Node) children.item(i);					
					if (argument.getNodeName().equals("creditCardNr")) {
						String encodedSecretArgument = argument.getTextContent();
						// cipher message
						byte[] sa = DatatypeConverter.parseBase64Binary(encodedSecretArgument);
						String keystore = CryptoUtil.getName() + ".jks";
						String key_alias = CryptoUtil.getName().toLowerCase();
						byte[] decipheredArgument = CryptoUtil.asymDecipher(sa, CryptoUtil.getPrivateKeyFromKeyStoreResource(keystore, KEYSTORE_PASSWORD.toCharArray(), key_alias, KEY_PASSWORD.toCharArray()));
						String SecretArgument = printBase64Binary(decipheredArgument);
						System.out.println("Numero de cartao de credito desencriptado.");
						argument.setTextContent(SecretArgument);
						msg.saveChanges();
					}
				}
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
