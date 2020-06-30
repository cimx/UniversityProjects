package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;

/**
 * Test suite
 */
public class BuyProductIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
	}

	@AfterClass
	public static void oneTimeTearDown() {
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception {
		// clear remote service state before each test
		client.clear();

		// fill-in test products
		// (since buyProduct is a read/write operation
		// the initialization below is done for each test)
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

	@After
	public void tearDown() {
		// clear remote service state after each test
		client.clear();
	}

	// tests
	// assertEquals(expected, actual);

	// public String buyProduct(String productId, int quantity)
	// throws BadProductId_Exception, BadQuantity_Exception,
	// InsufficientQuantity_Exception {

	// bad input tests

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNullTest() throws Exception {
		client.buyProduct(null, 10);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductEmptyTest() throws Exception {
		client.buyProduct("", 10);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void buyProductNegativeQuantityTest() throws Exception {
		client.buyProduct("X1", -1);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void buyProductZeroQuantityTest() throws Exception {
		client.buyProduct("X1", 0);
	}

	// main tests

	@Test
	public void buyProductResultTest() throws Exception {
		String result = client.buyProduct("X1", 1);
		assertNotNull(result);
	}

	@Test
	public void buyProductOneTest() throws Exception {
		assertEquals(10, client.getProduct("X1").getQuantity());

		String result = client.buyProduct("X1", 1);
		assertNotNull(result);

		assertEquals(9, client.getProduct("X1").getQuantity());
	}

	@Test
	public void buyProductHalfTest() throws Exception {
		assertEquals(20, client.getProduct("Y2").getQuantity());

		String result = client.buyProduct("Y2", 10);
		assertNotNull(result);

		assertEquals(10, client.getProduct("Y2").getQuantity());
	}

	@Test
	public void buyProductAllTest() throws Exception {
		assertEquals(30, client.getProduct("Z3").getQuantity());

		String result = client.buyProduct("Z3", 30);
		assertNotNull(result);

		assertEquals(0, client.getProduct("Z3").getQuantity());
	}

	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductOneTooManyTest() throws Exception {
		client.buyProduct("Z3", 31);
	}

	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductTooManyTest() throws Exception {
		client.buyProduct("Z3", 40);
	}

}
