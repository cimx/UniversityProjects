package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.ProductView;

/**
 * Test suite
 */
public class SearchProductsIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		// clear remote service state before all tests
		client.clear();

		// fill-in test products
		// (since searchProducts is read-only the initialization below
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
		{
			ProductView product = new ProductView();
			product.setId("K4");
			product.setDesc("T-shirt");
			product.setPrice(10);
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

	// public List<ProductView> searchProducts(String descText) throws
	// BadText_Exception

	// bad input tests

	@Test(expected = BadText_Exception.class)
	public void searchProductsNullTest() throws BadText_Exception {
		client.searchProducts(null);
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsEmptyTest() throws BadText_Exception {
		client.searchProducts("");
	}

	// main tests

	@Test
	public void searchProductsOneMatchTest() throws BadText_Exception {
		List<ProductView> products = client.searchProducts("Soccer ball");
		assertNotNull(products);
		assertEquals(1, products.size());

		ProductView product = products.get(0);
		assertEquals("Z3", product.getId());
		assertEquals(30, product.getPrice());
		assertEquals(30, product.getQuantity());
		assertEquals("Soccer ball", product.getDesc());
	}

	@Test
	public void searchProductsAllMatchTest() throws BadText_Exception {
		List<ProductView> products = client.searchProducts("ball");
		assertNotNull(products);
		assertEquals(3, products.size());

		// no ordering is imposed on results
		// check if descriptions all contain the search term
		for (ProductView product : products) {
			assertTrue(product.getDesc().indexOf("ball") >= 0);
		}
	}

	@Test
	public void searchProductsSomeMatchTest() throws BadText_Exception {
		List<ProductView> products = client.searchProducts("Bas");
		assertNotNull(products);
		assertEquals(2, products.size());

		// no ordering is imposed on results
		// check if descriptions all contain the search term
		for (ProductView product : products) {
			assertTrue(product.getDesc().indexOf("Bas") >= 0);
		}
	}

	@Test
	public void searchProductsNoMatchTest() throws BadText_Exception {
		List<ProductView> products = client.searchProducts("nOTtHERE");
		// when products are not found,
		// an empty list should be returned (not null)
		assertNotNull(products);
		assertEquals(0, products.size());
	}

	@Test
	public void searchProductsCaseTest() throws BadText_Exception {
		// product descriptions are case sensitive,
		// so "BALL" is not the same as "ball"
		List<ProductView> products = client.searchProducts("BALL");
		assertNotNull(products);
		assertEquals(0, products.size());
	}

	@Test
	public void searchProductsPriceQtyTest() throws BadText_Exception {
		// assure price and quantity are not swapped
		List<ProductView> products = client.searchProducts("T-shirt");
		assertNotNull(products);
		assertEquals(1, products.size());

		ProductView product = products.get(0);
		assertEquals("K4", product.getId());
		assertEquals(10, product.getPrice());
		assertEquals(30, product.getQuantity());
		assertEquals("T-shirt", product.getDesc());
	}

}
