package org.komparator.supplier.ws.cli;

/** Main class that starts the Supplier Web Service client. */
public class SupplierClientApp {

	public static void main(String[] args) throws Exception {
        
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + SupplierClientApp.class.getName() + " wsURL");
			return;
		}
		

		String uddiURL = null;
        String wsname = null;
        String wsURL = null;
        
        SupplierClient client = null;
        
        
		if (args.length == 1) {
			wsURL = args[0];
		}
		else if (args.length == 2) {
			wsname = args[0];
			uddiURL = args[1];
		}

		// Create client
		if (wsURL != null) {
			System.out.printf("Creating client for server at %s%n", wsURL);
			client = new SupplierClient(wsURL);
		}
		else if (wsname != null && uddiURL != null){
			System.out.printf("Creating client using UDDI at %s for %s%n",  uddiURL, wsname);
			client = new SupplierClient(wsname, uddiURL);
		}

		// the following remote invocations are just basic examples
		// the actual tests are made using JUnit

		System.out.println("Invoke ping()...");
		String result = client.ping("client");
		System.out.print("Result: ");
		System.out.println(result);
	}

}
