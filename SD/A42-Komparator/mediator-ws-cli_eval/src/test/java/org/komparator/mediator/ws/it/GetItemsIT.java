package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.ItemView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;

public class GetItemsIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		// clear remote service state before all tests
		mediatorClient.clear();

		// fill-in test products
		// (since getItems is read-only the initialization below
		// can be done once for all tests in this suite)
		{
			ProductView prod = new ProductView();
			prod.setId("p1");
			prod.setDesc("AAA bateries (pack of 3)");
			prod.setPrice(3);
			prod.setQuantity(10);
			supplierClients[0].createProduct(prod);
		}

		{
			ProductView prod = new ProductView();
			prod.setId("p1");
			prod.setDesc("3batteries");
			prod.setPrice(4);
			prod.setQuantity(10);
			supplierClients[1].createProduct(prod);
		}

		{
			ProductView prod = new ProductView();
			prod.setId("p2");
			prod.setDesc("AAA bateries (pack of 10)");
			prod.setPrice(9);
			prod.setQuantity(20);
			supplierClients[0].createProduct(prod);
		}

		{
			ProductView prod = new ProductView();
			prod.setId("p2");
			prod.setDesc("10x AAA battery");
			prod.setPrice(8);
			prod.setQuantity(20);
			supplierClients[1].createProduct(prod);
		}

		{
			ProductView prod = new ProductView();
			prod.setId("p3");
			prod.setDesc("Digital Multimeter");
			prod.setPrice(15);
			prod.setQuantity(5);
			supplierClients[0].createProduct(prod);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// clear remote service state after all tests
		mediatorClient.clear();
		// even though mediator clear should have cleared suppliers, clear them
		// explicitly after use
		supplierClients[0].clear();
		supplierClients[1].clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	// ------ WSDL Faults (error cases) ------

	@Test(expected = InvalidItemId_Exception.class)
	public void testNullItem() throws InvalidItemId_Exception {
		mediatorClient.getItems(null);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void testEmptyItemId() throws InvalidItemId_Exception {
		mediatorClient.getItems("");
	}

	// not used for evaluation
	// @Test(expected = InvalidItemId_Exception.class)
	public void testSpace() throws InvalidItemId_Exception {
		mediatorClient.getItems(" ");
	}

	// not used for evaluation
	// @Test(expected = InvalidItemId_Exception.class)
	public void testTab() throws InvalidItemId_Exception {
		mediatorClient.getItems("\t");
	}

	// not used for evaluation
	// @Test(expected = InvalidItemId_Exception.class)
	public void testNewline() throws InvalidItemId_Exception {
		mediatorClient.getItems("\n");
	}

	// not used for evaluation
	// @Test(expected = InvalidItemId_Exception.class)
	public void testDot() throws InvalidItemId_Exception {
		mediatorClient.getItems(".");
	}

	// ------ Normal cases ------

	@Test
	public void testInexistingItem1() throws InvalidItemId_Exception {
		List<ItemView> items = mediatorClient.getItems("p4");
		// desired output is empty, but null is also accepted (case not
		// specified in project statement)
		assertTrue(items == null || items.size() == 0);
	}

	@Test
	public void testInexistingItem2() throws InvalidItemId_Exception {
		// There is no such item, but there is an item with the description
		// "3batteries"
		List<ItemView> items = mediatorClient.getItems("3batteries");
		// desired output is empty, but null is also accepted (case not
		// specified in project statement)
		assertTrue(items == null || items.size() == 0);
	}

	@Test
	public void testSingleExistingItem() throws InvalidItemId_Exception {
		// Testing all item properties only in this test
		List<ItemView> items = mediatorClient.getItems("p3");
		assertEquals(1, items.size());

		assertEquals("p3", items.get(0).getItemId().getProductId());
		assertEquals("Digital Multimeter", items.get(0).getDesc());
		assertEquals(15, items.get(0).getPrice());
		assertEquals(supplierNames[0], items.get(0).getItemId().getSupplierId());
	}

	@Test
	public void testMultipleExistingItems() throws InvalidItemId_Exception {
		List<ItemView> items = mediatorClient.getItems("p1");
		assertEquals(2, items.size());
	}

	@Test
	public void testOrder1() throws InvalidItemId_Exception {
		// First supplier has the cheapest price
		List<ItemView> items = mediatorClient.getItems("p1");
		assertEquals(2, items.size());
		assertTrue(items.get(0).getPrice() <= items.get(1).getPrice());
	}

	@Test
	public void testOrder2() throws InvalidItemId_Exception {
		// First supplier has the most expensive price
		List<ItemView> items = mediatorClient.getItems("p2");
		assertEquals(2, items.size());
		assertTrue(items.get(0).getPrice() <= items.get(1).getPrice());
	}

}
