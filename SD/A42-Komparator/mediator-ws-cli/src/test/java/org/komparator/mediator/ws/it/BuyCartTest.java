package org.komparator.mediator.ws.it;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.mediator.ws.Result;
import org.komparator.mediator.ws.ShoppingResultView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;

public class BuyCartTest extends BaseIT {
	private ProductView p1 = null;
	private ProductView p2 = null;
	private ProductView p3 = null;
	
	private ItemIdView itemId1 = null;
	private ItemIdView itemId2 = null;
	private ItemIdView itemId3 = null;

	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.clear();
		
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
		p2.setQuantity(3);
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
	public void success() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		//Adiciona carrinhos de produtos disponiveis em quantidades suficientes
		mediatorClient.addToCart("Cart1", itemId1, 1);
		mediatorClient.addToCart("Cart2", itemId2, 2);
		mediatorClient.addToCart("Cart2", itemId3, 3);
		
		//Efetua a compra dos carrinhos
		ShoppingResultView shop1 = mediatorClient.buyCart("Cart1", "4024007102923926");
		ShoppingResultView shop2 = mediatorClient.buyCart("Cart2", "4024007102923926");
		
		//Verifica ids de compra
		Assert.assertEquals("SHOPPING1", shop1.getId());
		Assert.assertEquals("SHOPPING2", shop2.getId());
		
		//Verifica resultado da compra
		Assert.assertEquals(Result.COMPLETE, shop1.getResult());
		Assert.assertEquals(Result.COMPLETE, shop2.getResult());		
		
		//Verifica o numero de produtos comprados
		Assert.assertEquals(1, shop1.getPurchasedItems().size());
		Assert.assertEquals(2, shop2.getPurchasedItems().size());
		
		//Verifica o id dos produtos e dos fornecedores
		Assert.assertEquals(itemId1.getProductId(), shop1.getPurchasedItems().get(0).getItem().getItemId().getProductId());
		Assert.assertEquals(itemId2.getProductId(), shop2.getPurchasedItems().get(0).getItem().getItemId().getProductId());
		Assert.assertEquals(itemId3.getProductId(), shop2.getPurchasedItems().get(1).getItem().getItemId().getProductId());		

		Assert.assertEquals(itemId1.getSupplierId(), shop1.getPurchasedItems().get(0).getItem().getItemId().getSupplierId());
		Assert.assertEquals(itemId2.getSupplierId(), shop2.getPurchasedItems().get(0).getItem().getItemId().getSupplierId());
		Assert.assertEquals(itemId3.getSupplierId(), shop2.getPurchasedItems().get(1).getItem().getItemId().getSupplierId());
		
		//Verifica que nao houve erro com nenhum produto
		Assert.assertEquals(0, shop1.getDroppedItems().size());
		Assert.assertEquals(0, shop2.getDroppedItems().size());
		
	}
	
	@Test
	public void partialResultTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		//Cria tres carros onde a quantidade do item 2 carro 1 + item 2 carro 2 supera a oferecida pelo fornecedor
		mediatorClient.addToCart("Cart1", itemId2, 1);
		mediatorClient.addToCart("Cart1", itemId3, 2);
		mediatorClient.addToCart("Cart2", itemId2, 3);
		
		//Efetua a compra primeiro do carro 2, que deve conseguir todos os produtos
		//Depois do carro 1, que nao deve conseguir comprar o item 2
		ShoppingResultView shop1 = mediatorClient.buyCart("Cart2", "4024007102923926");
		ShoppingResultView shop2 = mediatorClient.buyCart("Cart1", "4024007102923926");
		
		//Verifica ids das compras
		Assert.assertEquals("SHOPPING1", shop1.getId());
		Assert.assertEquals("SHOPPING2", shop2.getId());
		
		//Verifica que o segundo carro obteve todos os produtos
		Assert.assertEquals(Result.COMPLETE, shop1.getResult());
		//Verifica que o primeiro carro nao conseguiu comprar tudo mas comprou algo
		Assert.assertEquals(Result.PARTIAL, shop2.getResult());

		//Verifica que cada carro comprou um produto, tal como esperado
		Assert.assertEquals(1, shop1.getPurchasedItems().size());
		Assert.assertEquals(1, shop2.getPurchasedItems().size());
		
		//Verifica que apenas o carro um teve um produto nao comprado 
		Assert.assertEquals(0, shop1.getDroppedItems().size());
		Assert.assertEquals(1, shop2.getDroppedItems().size());
		
		//Verifica os ids dos produtos e dos fornecedores
		Assert.assertEquals(itemId2.getProductId(), shop1.getPurchasedItems().get(0).getItem().getItemId().getProductId());
		Assert.assertEquals(itemId3.getProductId(), shop2.getPurchasedItems().get(0).getItem().getItemId().getProductId());		

		Assert.assertEquals(itemId2.getSupplierId(), shop1.getPurchasedItems().get(0).getItem().getItemId().getSupplierId());
		Assert.assertEquals(itemId3.getSupplierId(), shop2.getPurchasedItems().get(0).getItem().getItemId().getSupplierId());		

		Assert.assertEquals(itemId2.getProductId(), shop2.getDroppedItems().get(0).getItem().getItemId().getProductId());
		Assert.assertEquals(itemId2.getSupplierId(), shop2.getDroppedItems().get(0).getItem().getItemId().getSupplierId());
		
	}
	
	@Test
	public void emptyResultTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		//Costroi carros com o mesmo produto em diferentes quantidades
		mediatorClient.addToCart("Cart1", itemId2, 1);
		mediatorClient.addToCart("Cart2", itemId2, 3);
		
		//O carro 2 e comprado primeiro. E esperado que o carro 2 esgote o stock
		//O carro 1 nao deve conseguir comprar nenhum produto
		ShoppingResultView shop1 = mediatorClient.buyCart("Cart2", "4024007102923926");
		ShoppingResultView shop2 = mediatorClient.buyCart("Cart1", "4024007102923926");
		
		//Verifica os ids das compras
		Assert.assertEquals("SHOPPING1", shop1.getId());
		Assert.assertEquals("SHOPPING2", shop2.getId());
		
		//Verifica que o carrinho 2 obteve o produto
		Assert.assertEquals(Result.COMPLETE, shop1.getResult());
		//Verifica que o carrinho 1 nao comprou nada
		Assert.assertEquals(Result.EMPTY, shop2.getResult());

		//Verifica listas de comprados e nao comprados
		Assert.assertEquals(1, shop1.getPurchasedItems().size());
		Assert.assertEquals(0, shop2.getPurchasedItems().size());
		
		Assert.assertEquals(0, shop1.getDroppedItems().size());
		Assert.assertEquals(1, shop2.getDroppedItems().size());
		
		//Verifica ids de produto e suppliers
		Assert.assertEquals(itemId2.getProductId(), shop1.getPurchasedItems().get(0).getItem().getItemId().getProductId());
		Assert.assertEquals(itemId2.getSupplierId(), shop1.getPurchasedItems().get(0).getItem().getItemId().getSupplierId());

		Assert.assertEquals(itemId2.getProductId(), shop2.getDroppedItems().get(0).getItem().getItemId().getProductId());
		Assert.assertEquals(itemId2.getSupplierId(), shop2.getDroppedItems().get(0).getItem().getItemId().getSupplierId());
		
	}
	
	@Test(expected =  InvalidCartId_Exception.class)
	public void badCartID() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("Cart7", "4024007102923926");
	}	

	@Test(expected =  InvalidCreditCard_Exception.class)
	public void invalidCC() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("Cart1", itemId1, 1);
		mediatorClient.buyCart("Cart1", "1111");
	}
	@Test(expected =  InvalidCreditCard_Exception.class)
	public void spaceCCnum() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("Cart1", itemId1, 1);
		mediatorClient.buyCart("Cart1", " ");
	}
	@Test(expected =  InvalidCreditCard_Exception.class)
	public void nlCCnum() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("Cart1", itemId1, 1);
		mediatorClient.buyCart("Cart1", "\n");
	}
	@Test(expected =  InvalidCreditCard_Exception.class)
	public void tabCCnum() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("Cart1", itemId1, 1);
		mediatorClient.buyCart("Cart1", "\t");
	}
	@Test(expected =  InvalidCreditCard_Exception.class)
	public void nullCCnum() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("Cart1", itemId1, 1);
		mediatorClient.buyCart("Cart1", null);
	}
	@Test(expected =  InvalidCreditCard_Exception.class)
	public void emptyCCnum() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("Cart1", itemId1, 1);
		mediatorClient.buyCart("Cart1", "");
	}
	
	@Test(expected =  InvalidCartId_Exception.class)
	public void emptyCartId() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("", "4024007102923926");
	}
	@Test(expected =  InvalidCartId_Exception.class)
	public void spaceCartId() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart(" ", "4024007102923926");
	}
	@Test(expected =  InvalidCartId_Exception.class)
	public void nlCartId() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("\n","4024007102923926");
	}
	@Test(expected =  InvalidCartId_Exception.class)
	public void tabCartId() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart("\t","4024007102923926");
	}
	@Test(expected =  InvalidCartId_Exception.class)
	public void nullCartId() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart(null,"4024007102923926");
	}
	
	@After
	public void tearDown(){
		mediatorClient.clear();
	}
}



