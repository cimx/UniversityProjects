package org.komparator.supplier.ws;


/** Main class that starts the Supplier Web Service. */
public class SupplierApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + SupplierApp.class.getName() + " wsURL");
			return;
		}
		String wsURL = args[0];
		String uddiURL = args[1];
		String name = args[2];

		// Create server implementation object
		SupplierEndpointManager endpoint = new SupplierEndpointManager(wsURL, uddiURL, name);
		try {
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}
	}
}