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
import org.komparator.supplier.ws.*;

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
		{
			ProductView product = new ProductView();
			product.setId("W4");
			product.setDesc("Rugby");
			product.setPrice(40);
			product.setQuantity(40);
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
	public void searchProductNullText() throws BadText_Exception{
		client.searchProducts(null);
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsNoText() throws BadText_Exception{
		client.searchProducts("");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductSpaceText() throws BadText_Exception{
		client.searchProducts("   ");
	}
	
	@Test(expected = BadText_Exception.class)
	public void searchProductsnText() throws BadText_Exception{
		client.searchProducts("\n");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductstText() throws BadText_Exception{
		client.searchProducts("\t");
	}


	// main tests

	@Test
	public void noResults() throws BadText_Exception{
		List products = client.searchProducts("foot");
		assertTrue(products.isEmpty());
		assertNotNull(products);
	}

	@Test
	public void hasResults() throws BadText_Exception{
		List<ProductView> products = client.searchProducts("ball");
		assertEquals(3, products.size());
		for (ProductView product : products){
			assertTrue(product.getDesc().contains("ball"));
		}
	}
}
