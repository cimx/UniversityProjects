package org.komparator.mediator.ws.it;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.ItemView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;

public class addToCartTest extends BaseIT {
	
	private ProductView p1 = null;
	private ProductView p2 = null;
	private ProductView p3 = null;
	
	private ItemIdView itemId1 = null;
	private ItemIdView itemId2 = null;
	private ItemIdView itemId3 = null;
	
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception{
		//O primeiro supplier tem um produto com as caracteristicas:
		p1 = new ProductView();
		p1.setId("PC1");
		p1.setDesc("Computador1");
		p1.setQuantity(1);
		p1.setPrice(1500);
		_suppliers.get(0).createProduct(p1);
		
		itemId1 = new ItemIdView();
		itemId1.setProductId("PC1");
		itemId1.setSupplierId(_suppliers.get(0).getWsName());
		
		//O segundo supplier tem um produto com as caracteristicas:
		p2 = new ProductView();
		p2.setId("PC2");
		p2.setDesc("Computador2");
		p2.setQuantity(2);
		p2.setPrice(500);
		_suppliers.get(1).createProduct(p2);
		
		itemId2 = new ItemIdView();
		itemId2.setProductId("PC2");
		itemId2.setSupplierId(_suppliers.get(1).getWsName());
		
		//O terceiro supplier tem um produto com as caracteristicas:
		p3 = new ProductView();
		p3.setId("PC3");
		p3.setDesc("Computador3");
		p3.setQuantity(3);
		p3.setPrice(750);
		_suppliers.get(2).createProduct(p3);
		
		itemId3 = new ItemIdView();
		itemId3.setProductId("PC3");
		itemId3.setSupplierId(_suppliers.get(2).getWsName());
	}
	
	@Test
	public void sucessDiffCarts() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {		
		//Adiciona cada produto a um carro diferente
		mediatorClient.addToCart("Cart1", itemId1, 1);
		mediatorClient.addToCart("Cart2", itemId2, 2);
		mediatorClient.addToCart("Cart3", itemId3, 1);
		
		List<CartView> carts = mediatorClient.listCarts();
		//Tem de haver tres carros
		Assert.assertEquals(3, carts.size());
		
		//Obtem o id do carro
		Assert.assertEquals("Cart1", carts.get(0).getCartId());
		//Obtem a quantidade comprada
		Assert.assertEquals(1, carts.get(0).getItems().get(0).getQuantity());
		//Obtem a descricao do produto
		Assert.assertEquals("Computador1", carts.get(0).getItems().get(0).getItem().getDesc());
		//Obtem o preco do produto
		Assert.assertEquals(1500, carts.get(0).getItems().get(0).getItem().getPrice());
		//Obtem o id do produto
		Assert.assertEquals("PC1", carts.get(0).getItems().get(0).getItem().getItemId().getProductId());
		//Obtem o id do supplier
		Assert.assertEquals(_suppliers.get(0).getWsName(), carts.get(0).getItems().get(0).getItem().getItemId().getSupplierId());
		
		Assert.assertEquals("Cart2", carts.get(1).getCartId());
		Assert.assertEquals(2, carts.get(1).getItems().get(0).getQuantity());
		Assert.assertEquals("Computador2", carts.get(1).getItems().get(0).getItem().getDesc());
		Assert.assertEquals(500, carts.get(1).getItems().get(0).getItem().getPrice());
		Assert.assertEquals("PC2", carts.get(1).getItems().get(0).getItem().getItemId().getProductId());
		Assert.assertEquals(_suppliers.get(1).getWsName(), carts.get(1).getItems().get(0).getItem().getItemId().getSupplierId());
	
		Assert.assertEquals("Cart3", carts.get(2).getCartId());
		Assert.assertEquals(1, carts.get(2).getItems().get(0).getQuantity());
		Assert.assertEquals("Computador3", carts.get(2).getItems().get(0).getItem().getDesc());
		Assert.assertEquals(750, carts.get(2).getItems().get(0).getItem().getPrice());
		Assert.assertEquals("PC3", carts.get(2).getItems().get(0).getItem().getItemId().getProductId());
		Assert.assertEquals(_suppliers.get(2).getWsName(), carts.get(2).getItems().get(0).getItem().getItemId().getSupplierId());
	}
	
	@Test
	public void sucessSameCart() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {		
		//Adiciona ao mesmo carro tres produtos diferentes
		mediatorClient.addToCart("Cart1", itemId1, 1);
		mediatorClient.addToCart("Cart1", itemId2, 2);
		mediatorClient.addToCart("Cart1", itemId3, 1);
		
		List<CartView> carts = mediatorClient.listCarts();
		
		//Confere se apenas ha um carro
		Assert.assertEquals(1, carts.size());
		//Confere se esse carro tem tres produtos
		Assert.assertEquals(3, carts.get(0).getItems().size());
		
		Assert.assertEquals("Cart1", carts.get(0).getCartId());
		Assert.assertEquals(1, carts.get(0).getItems().get(0).getQuantity());
		Assert.assertEquals("Computador1", carts.get(0).getItems().get(0).getItem().getDesc());
		Assert.assertEquals(1500, carts.get(0).getItems().get(0).getItem().getPrice());
		Assert.assertEquals("PC1", carts.get(0).getItems().get(0).getItem().getItemId().getProductId());
		Assert.assertEquals(_suppliers.get(0).getWsName(), carts.get(0).getItems().get(0).getItem().getItemId().getSupplierId());
		
		Assert.assertEquals("Cart1", carts.get(0).getCartId());
		Assert.assertEquals(2, carts.get(0).getItems().get(1).getQuantity());
		Assert.assertEquals("Computador2", carts.get(0).getItems().get(1).getItem().getDesc());
		Assert.assertEquals(500, carts.get(0).getItems().get(1).getItem().getPrice());
		Assert.assertEquals("PC2", carts.get(0).getItems().get(1).getItem().getItemId().getProductId());
		Assert.assertEquals(_suppliers.get(1).getWsName(), carts.get(0).getItems().get(1).getItem().getItemId().getSupplierId());
	
		Assert.assertEquals("Cart1", carts.get(0).getCartId());
		Assert.assertEquals(1, carts.get(0).getItems().get(2).getQuantity());
		Assert.assertEquals("Computador3", carts.get(0).getItems().get(2).getItem().getDesc());
		Assert.assertEquals(750, carts.get(0).getItems().get(2).getItem().getPrice());
		Assert.assertEquals("PC3", carts.get(0).getItems().get(2).getItem().getItemId().getProductId());
		Assert.assertEquals(_suppliers.get(2).getWsName(), carts.get(0).getItems().get(2).getItem().getItemId().getSupplierId());
	}
	
	@Test (expected=InvalidCartId_Exception.class)
	public void nullCartId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart(null, itemId1, 1);
	}
	
	@Test (expected=InvalidCartId_Exception.class)
	public void emptyStringCartId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart(" ", itemId1, 1);
	}
	
	@Test (expected=InvalidCartId_Exception.class)
	public void spaceCartId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart(" ", itemId1, 1);
	}
	
	@Test (expected=InvalidCartId_Exception.class)
	public void blankCartId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("      ", itemId1, 1);
	}
	
	@Test (expected=InvalidCartId_Exception.class)
	public void tabCartId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("\t", itemId1, 1);
	}
	
	@Test (expected=InvalidCartId_Exception.class)
	public void nlCartId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("\n", itemId1, 1);
	}
	
	@Test (expected=InvalidItemId_Exception.class)
	public void nullItemId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		ItemIdView itemId = new ItemIdView();
		itemId.setProductId(null);
		itemId.setSupplierId(_suppliers.get(0).getWsName());
		
		mediatorClient.addToCart("Cart1", itemId, 1);
	}
	
	@Test (expected=InvalidItemId_Exception.class)
	public void emptyStringItemId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		ItemIdView itemId = new ItemIdView();
		itemId.setProductId("");
		itemId.setSupplierId(_suppliers.get(0).getWsName());
		
		mediatorClient.addToCart("Cart1", itemId, 1);
	}
	
	@Test (expected=InvalidItemId_Exception.class)
	public void spaceItemId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		ItemIdView itemId = new ItemIdView();
		itemId.setProductId(" ");
		itemId.setSupplierId(_suppliers.get(0).getWsName());
		
		mediatorClient.addToCart("Cart1", itemId, 1);
	}
	
	@Test (expected=InvalidItemId_Exception.class)
	public void blankItemId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		ItemIdView itemId = new ItemIdView();
		itemId.setProductId("       ");
		itemId.setSupplierId(_suppliers.get(0).getWsName());
		
		mediatorClient.addToCart("Cart1", itemId, 1);
	}
	
	@Test (expected=InvalidItemId_Exception.class)
	public void tabItemId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		ItemIdView itemId = new ItemIdView();
		itemId.setProductId("\t");
		itemId.setSupplierId(_suppliers.get(0).getWsName());
		
		mediatorClient.addToCart("Cart1", itemId, 1);
	}
	
	@Test (expected=InvalidItemId_Exception.class)
	public void nlItemId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		ItemIdView itemId = new ItemIdView();
		itemId.setProductId("\n");
		itemId.setSupplierId(_suppliers.get(0).getWsName());
		
		mediatorClient.addToCart("Cart1", itemId, 1);
	}
	
	@Test (expected=InvalidQuantity_Exception.class)
	public void negativeQuantity() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("Cart1", itemId1, -1);
	}
	
	@Test (expected=InvalidQuantity_Exception.class)
	public void zeroQuantity() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("Cart1", itemId1, 0);
	}
	
	@Test (expected=NotEnoughItems_Exception.class)
	public void notEnoughItems() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("Cart1", itemId1, 100);
	}
	
	@Test (expected=NotEnoughItems_Exception.class)
	public void notEnoughItemsAdding() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("Cart1", itemId1, 1);
		mediatorClient.addToCart("Cart1", itemId1, 1);
	}
	
	@After
	public void tearDown(){
		mediatorClient.clear();
	}

}
