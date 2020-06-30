package org.komparator.mediator.ws.it;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.supplier.ws.cli.SupplierClient;


public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static MediatorClient mediatorClient;
	
	protected static List<SupplierClient> _suppliers = new ArrayList<SupplierClient>();

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		String uddiEnabled = testProps.getProperty("uddi.enabled");
		String uddiURL = testProps.getProperty("uddi.url");
		String wsName = testProps.getProperty("ws.name");
		String wsURL = testProps.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			mediatorClient = new MediatorClient(uddiURL, wsName);
		} else {
			mediatorClient = new MediatorClient(wsURL);
		}
		
		//Testes sempre a correr com 3 suppliers
		for (int i = 1; i <= 3; i++){
			SupplierClient client = new SupplierClient("A42_Supplier" + Integer.toString(i), uddiURL);
			_suppliers.add(client);
		}
	}

	@AfterClass
	public static void cleanup() {
		_suppliers.get(0).clear();
		_suppliers.get(1).clear();
		_suppliers.get(2).clear();
		_suppliers.clear();
	}
}
