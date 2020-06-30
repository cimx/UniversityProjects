package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.*;

/**
 * Test suite
 */
public class BuyProductIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		// clear remote service state before all tests
		client.clear();

		// fill-in test products
		// (since getProduct is read-only the initialization below
		// can be done once for all tests in this suite)
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Y2");
			product.setDesc("Baseball");
			product.setPrice(20);
			product.setQuantity(20);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z3");
			product.setDesc("Soccer ball");
			product.setPrice(30);
			product.setQuantity(30);
			client.createProduct(product);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// clear remote service state after all tests
		client.clear();
	}


	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	// tests
	// assertEquals(expected, actual);
	
	

	// bad input tests

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNullTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(null, 20);
	}
	@Test(expected = BadProductId_Exception.class)
	public void buyProductSpacesId() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("              ", 25);
	}
	@Test(expected = BadProductId_Exception.class)
	public void buyProductEmptyId() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("", 25);
	}
	@Test(expected = BadProductId_Exception.class)
	public void buyProductNewlineId() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\n", 25);
	}
	@Test(expected = BadProductId_Exception.class)
	public void buyProductTabId() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\t", 25);
	}
	@Test(expected = BadQuantity_Exception.class)
	public void buyProductNegativeQuantityTest() throws BadQuantity_Exception, BadProductId_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Y2", -1);
	}
	@Test(expected = BadQuantity_Exception.class)
	public void buyProductZeroQuantityTest() throws BadQuantity_Exception, BadProductId_Exception, InsufficientQuantity_Exception{
		client.buyProduct("Y2", 0);
	}
	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductBiggerQuantityTest() throws InsufficientQuantity_Exception, BadProductId_Exception, BadQuantity_Exception {
		client.buyProduct("Y2", 25);
	}
	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductTwiceTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String purchaseId1 = client.buyProduct("Y2",2);
		String purchaseId2 = client.buyProduct("Y2",19);
	}
	// main tests

	@Test
	public void buyProductExistsTest1() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String purchaseId = client.buyProduct("X1",7);
		assertEquals(3, client.getProduct("X1").getQuantity());
		assertEquals("1",purchaseId);
	}
	
	@Test
	public void buyProductExistsTest2() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String purchaseId1 = client.buyProduct("Z3",15);
		assertEquals(15, client.getProduct("Z3").getQuantity());
		assertEquals("2",purchaseId1);
		String purchaseId2 = client.buyProduct("Z3",14);
		assertEquals(1, client.getProduct("Z3").getQuantity());
		assertEquals("3",purchaseId2);
	}	
}
