package org.komparator.mediator.ws.it;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.ItemView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;

public class getItemsTest extends BaseIT {
	
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception{
		//Cria no primeiro fornecedor um produto com as seguintes caracteristicas:
		ProductView p1 = new ProductView();
		p1.setId("PC");
		p1.setDesc("Computador1");
		p1.setQuantity(1);
		p1.setPrice(1500);
		_suppliers.get(0).createProduct(p1);
		
		//Cria no primeiro fornecedor um produto com as seguintes caracteristicas:
		ProductView p2 = new ProductView();
		p2.setId("PC1");
		p2.setDesc("Computador2");
		p2.setQuantity(2);
		p2.setPrice(500);
		_suppliers.get(0).createProduct(p2);
		
		//Cria no segundo fornecedor um produto com as seguintes caracteristicas:
		ProductView p3 = new ProductView();
		p3.setId("PC");
		p3.setDesc("Computador3");
		p3.setQuantity(3);
		p3.setPrice(750);
		_suppliers.get(1).createProduct(p3);
	}
	
	@Test
	public void sucess() throws InvalidItemId_Exception {
		//Procura items com id == "PC"
		List<ItemView> items = mediatorClient.getItems("PC");
		//Procura items com id == "PC1"
		List<ItemView> items2 = mediatorClient.getItems("PC1");
		
		//Verifica numero de items em cada lista
		Assert.assertEquals(2, items.size());
		Assert.assertEquals(1, items2.size());
		
		//Verifica todas as propriedades do primeiro produto encontrado
		Assert.assertEquals("PC", items.get(0).getItemId().getProductId());
		Assert.assertEquals("Computador3", items.get(0).getDesc());
		Assert.assertEquals(750, items.get(0).getPrice());
		//Verifica todas as propriedades do segundo produto encontrado
		Assert.assertEquals("PC", items.get(1).getItemId().getProductId());
		Assert.assertEquals("Computador1", items.get(1).getDesc());
		Assert.assertEquals(1500, items.get(1).getPrice());
		//Verifica todas as propriedades do terceiro produto encontrado
		Assert.assertEquals("PC1", items2.get(0).getItemId().getProductId());
		Assert.assertEquals("Computador2", items2.get(0).getDesc());
		Assert.assertEquals(500, items2.get(0).getPrice());
		//Conclui-se que esta por ordem de preco, do menor para o mais.
	}
	
	//fet non existent product ID
	@Test
	public void badIdItemId() throws InvalidItemId_Exception{
		Assert.assertEquals(0,mediatorClient.getItems("PCX").size());
	}

	@Test (expected = InvalidItemId_Exception.class)
	public void nullItemId() throws InvalidItemId_Exception{
		mediatorClient.getItems(null);
	}
	
	@Test (expected = InvalidItemId_Exception.class)
	public void emptyStringItemId() throws InvalidItemId_Exception{
		mediatorClient.getItems("");
	}
	
	@Test (expected = InvalidItemId_Exception.class)
	public void spaceItemId() throws InvalidItemId_Exception{
		mediatorClient.getItems(" ");
	}
	
	@Test (expected = InvalidItemId_Exception.class)
	public void tabItemId() throws InvalidItemId_Exception{
		mediatorClient.getItems("\t");
	}
	
	@Test (expected = InvalidItemId_Exception.class)
	public void nlItemId() throws InvalidItemId_Exception{
		mediatorClient.getItems("\n"); 
	}
	
	@After
	public void tearDown(){
		mediatorClient.clear();
	}
}
