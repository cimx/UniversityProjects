package org.komparator.mediator.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.xml.ws.BindingProvider;

public class MediatorApp {

	public static void main(String[] args) throws Exception {
		
		// Check arguments
		if (args.length == 0 || args.length == 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + MediatorApp.class.getName() + " wsURL OR uddiURL wsName wsURL");
			return;
		}
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;

		// Create server implementation object, according to options
		MediatorEndpointManager endpoint = null;
		if (args.length == 1) {
			wsURL = args[0];
			endpoint = new MediatorEndpointManager(wsURL);
		} else if (args.length >= 3) {
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			endpoint = new MediatorEndpointManager(uddiURL, wsName, wsURL);
			endpoint.setVerbose(true);
		}

		Timer timer = new Timer(true);
		
		try {
			endpoint.start();
			
			LifeProof lifeProof = new LifeProof(endpoint);
			timer.schedule(lifeProof, 0, 5000);
			
			endpoint.awaitConnections();
		} finally {
			timer.cancel();
			endpoint.stop();
		}

	}

}
