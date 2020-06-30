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
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;

public class SearchItemsIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		// clear remote service state before all tests
		mediatorClient.clear();

		// fill-in test products
		// (since searchItems is read-only the initialization below
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

		{
			ProductView prod = new ProductView();
			prod.setId("p4");
			prod.setDesc("very cheap batteries");
			prod.setPrice(2);
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

	@Test(expected = InvalidText_Exception.class)
	public void testNullText() throws InvalidText_Exception {
		mediatorClient.searchItems(null);
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

	// ------ Normal cases ------

	@Test
	public void testInexistingText1() throws InvalidText_Exception {
		List<ItemView> items = mediatorClient.searchItems("porsche");
		// desired output is empty, but null is also accepted (case not
		// specified in project statement)
		assertTrue(items == null || items.size() == 0);
	}

	@Test
	public void testInexistingText2() throws InvalidText_Exception {
		// There is no item containing this string in its description.
		// There is one that contains it in its ID though,
		// but this operation is not supposed to search by ID.
		List<ItemView> items = mediatorClient.searchItems("p4");
		// desired output is empty, but null is also accepted (case not
		// specified in project statement)
		assertTrue(items == null || items.size() == 0);
	}

	@Test
	public void testSingleExistingText() throws InvalidText_Exception {
		List<ItemView> items = mediatorClient.searchItems("battery");
		assertEquals(1, items.size());

		assertEquals("p2", items.get(0).getItemId().getProductId());
		assertEquals("10x AAA battery", items.get(0).getDesc());
		assertEquals(8, items.get(0).getPrice());
		assertEquals(supplierNames[1], items.get(0).getItemId().getSupplierId());
	}

	@Test
	public void testWithoutOrder() throws InvalidText_Exception {
		List<ItemView> items = mediatorClient.searchItems("bat");
		assertEquals(5, items.size());
	}

	@Test
	public void testWithOrder() throws InvalidText_Exception {
		List<ItemView> items = mediatorClient.searchItems("bat");
		assertEquals(5, items.size());

		// Check order criteria two by two
		for (int i = 0; i < items.size() - 1; i++) {
			// Check the first order criterion: product id
			final String firstProductId = items.get(i).getItemId().getProductId();
			final String secondProductId = items.get(i + 1).getItemId().getProductId();
			assertTrue(firstProductId.compareTo(secondProductId) <= 0);
			// Check the second order criterion: price
			if (firstProductId.equals(secondProductId)) {
				final int firstPrice = items.get(i).getPrice();
				final int secondPrice = items.get(i + 1).getPrice();
				assertTrue(firstPrice <= secondPrice);
			}
		}
	}

	@Test
	public void testCaseSensitivity() throws InvalidText_Exception {
		{
			List<ItemView> items = mediatorClient.searchItems("digital");
			assertEquals(0, items.size());
		}
		{
			List<ItemView> items = mediatorClient.searchItems("Digital");
			assertEquals(1, items.size());
		}
	}

}
