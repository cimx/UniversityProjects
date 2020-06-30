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

public class AttackHandler implements SOAPHandler<SOAPMessageContext> {

	
	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public void close(MessageContext context) {}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}
    
	public boolean handleMessage(SOAPMessageContext smc) {

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			if (outboundElement.booleanValue()) {
				
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();
				SOAPHeader sh = se.getHeader();
				
				if (sh == null) { 
					sh = se.addHeader(); 
				}
				
				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				System.out.println(opn.getLocalPart());
				
				if (!opn.getLocalPart().equals("getProduct")) {
					return true;
				}
				
				NodeList children;
				try{
					children = ((Node) sb).getFirstChild().getFirstChild().getChildNodes();
				} catch (NullPointerException e){
					return true;
				}
				
				for (int i = 0; i < children.getLength(); i++) {
					if (children.item(i).getNodeName().equals("id")){
						if(!children.item(i).getTextContent().equals("ATTACK")){
							return true;
						}
					}
				}
				
				System.out.println("Trying to attack header in outbound SOAP message...");
				
				for (int i = 0; i < children.getLength(); i++) {
					if (children.item(i).getNodeName().equals("price")){
						children.item(i).setTextContent("10000000");
						msg.saveChanges();
						System.out.println("SOAPmessage atacada!");
					}
				}
				
				return true;				

			} else {
				return true;
			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}

    		return true;
    }
}
