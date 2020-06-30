package org.komparator.mediator.ws.it;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;

public class SearchItemsTest extends BaseIT {
	
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception{
		ProductView p1 = new ProductView();
		p1.setId("PCB");
		p1.setDesc("ComputadorB");
		p1.setQuantity(1);
		p1.setPrice(1500);
		_suppliers.get(0).createProduct(p1);
		
		ProductView p2 = new ProductView();
		p2.setId("PCA");
		p2.setDesc("ComputadorA");
		p2.setQuantity(2);
		p2.setPrice(750);
		_suppliers.get(0).createProduct(p2);
		
		ProductView p3 = new ProductView();
		p3.setId("PCA");
		p3.setDesc("ComputadorD");
		p3.setQuantity(3);
		p3.setPrice(500);
		_suppliers.get(1).createProduct(p3);
		
		ProductView p4 = new ProductView();
		p4.setId("PCC");
		p4.setDesc("ComputadorE");
		p4.setQuantity(4);
		p4.setPrice(900);
		_suppliers.get(2).createProduct(p4);
		
		ProductView p5 = new ProductView();
		p5.setId("PCC");
		p5.setDesc("ComputadorC");
		p5.setQuantity(3);
		p5.setPrice(100);
		_suppliers.get(0).createProduct(p5);
		
		ProductView p6 = new ProductView();
		p6.setId("PCC");
		p6.setDesc("Radio");
		p6.setQuantity(3);
		p6.setPrice(100);
		_suppliers.get(1).createProduct(p6);
	}
	
	@Test
	public void sucess() throws InvalidText_Exception {
		List<ItemView> items = mediatorClient.searchItems("Computador");
		 
		Assert.assertEquals("PCA", items.get(0).getItemId().getProductId());
		Assert.assertEquals("ComputadorD", items.get(0).getDesc());
		
		Assert.assertEquals("PCA", items.get(1).getItemId().getProductId());
		Assert.assertEquals("ComputadorA", items.get(1).getDesc());
		
		Assert.assertEquals("PCB", items.get(2).getItemId().getProductId());
		Assert.assertEquals("ComputadorB", items.get(2).getDesc());
		
		Assert.assertEquals("PCC", items.get(3).getItemId().getProductId());
		Assert.assertEquals("ComputadorC", items.get(3).getDesc());
		
		Assert.assertEquals("PCC", items.get(4).getItemId().getProductId());
		Assert.assertEquals("ComputadorE", items.get(4).getDesc());
		
		//checking if it didn't also search for products with a different description than the desired one
		Assert.assertEquals(5, items.size());
	}
	
	//Searching for non existent description
	@Test 
	public void badDesc() throws InvalidText_Exception{
		Assert.assertEquals(0, mediatorClient.searchItems("Telefone").size());
	}
	
	//Invalid descriptions
	@Test (expected = InvalidText_Exception.class)
	public void nullDesc() throws InvalidText_Exception{
		mediatorClient.searchItems(null);
	}
	
	@Test (expected = InvalidText_Exception.class)
	public void emptyString() throws InvalidText_Exception{
		mediatorClient.searchItems("");
	}
	
	@Test (expected = InvalidText_Exception.class)
	public void spaceDesc() throws InvalidText_Exception{
		mediatorClient.searchItems(" ");
	}
	
	@Test (expected = InvalidText_Exception.class)
	public void nlDesc() throws InvalidText_Exception{
		mediatorClient.searchItems("\n");
	}
	@Test (expected = InvalidText_Exception.class)
	public void tabDesc() throws InvalidText_Exception{
		mediatorClient.searchItems("\t");
	}
	
	@After
	public void tearDown(){
		mediatorClient.clear();
	}
}
