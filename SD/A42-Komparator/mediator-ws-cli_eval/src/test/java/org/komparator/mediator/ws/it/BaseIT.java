package org.komparator.mediator.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.supplier.ws.cli.SupplierClient;

public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static MediatorClient mediatorClient;

	private static final int NR_SUPPLIERS = 2;
	protected static SupplierClient[] supplierClients = new SupplierClient[NR_SUPPLIERS];
	protected static String[] supplierNames = new String[NR_SUPPLIERS];
	protected static String[] supplierUrls = new String[NR_SUPPLIERS];

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

		String supplierNameBase = testProps.getProperty("supplier.ws.name");
		String supplierUrlBase = testProps.getProperty("supplier.ws.url");
		for (int i = 0; i < NR_SUPPLIERS; i++) {
			// add 1 to i because supplier names start with 1 not 0
			supplierNames[i] = supplierNameBase + (i + 1);
			supplierUrls[i] = supplierUrlBase.replaceAll("\\$i", Integer.toString(i + 1));
		}

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			mediatorClient = new MediatorClient(uddiURL, wsName);
			for (int i = 0; i < NR_SUPPLIERS; i++)
				supplierClients[i] = new SupplierClient(uddiURL, supplierNames[i]);
		} else {
			mediatorClient = new MediatorClient(wsURL);
			for (int i = 0; i < NR_SUPPLIERS; i++)
				supplierClients[i] = new SupplierClient(supplierUrls[i]);
		}

	}

	@AfterClass
	public static void cleanup() {
		for (int i = 0; i < NR_SUPPLIERS; i++)
			supplierClients[i] = null;
	}

}
