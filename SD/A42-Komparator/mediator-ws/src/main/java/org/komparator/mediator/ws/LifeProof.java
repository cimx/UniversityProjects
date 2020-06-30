package org.komparator.mediator.ws;

import java.util.Date;
import java.util.TimerTask;

import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;

public class LifeProof extends TimerTask {
	
	private static Date lastLife = new Date();
	
	public static Date getLastLife() { return lastLife; }
	public static void setLastLife(Date lastLife) { LifeProof.lastLife = lastLife; }
	
	private static final String MEDCLIENTURL = "http://localhost:8072/mediator-ws/endpoint";
	private static final int TIMEOUT = 10000;
	
	private MediatorEndpointManager mediator = null;
	private static MediatorClient mediatorClient = null;
	
	public LifeProof(MediatorEndpointManager mediator) {
		this.mediator = mediator;
		if (mediator.getStatus()){
			try {
				mediatorClient = new MediatorClient(MEDCLIENTURL);
			}
			
			catch (MediatorClientException e){
				System.out.println("Error while creating MediatorClient");
			}
		}
	}
	
	@Override
	public void run() {
		if (mediator.getStatus()){
			if (mediatorClient != null) {
				mediatorClient.imAlive();
			}
		}
		else {
			Date now = new Date();
			if ((now.getTime() - lastLife.getTime()) > TIMEOUT){
				System.out.println("Detetada falha no primeiro mediator");
				try {
					mediator.publishToUDDI();
					System.out.println("Mediator secundario ativado com sucesso!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void updateCarts(String command, CartView cart, String requestId){
		if (mediatorClient != null)
			mediatorClient.updateCarts(command, cart, requestId);
	}
	
	public static void updateHistory(String command, ShoppingResultView shopResult, String requestId){
		if (mediatorClient != null)
			mediatorClient.updateHistory(command, shopResult, requestId);
	}
}
