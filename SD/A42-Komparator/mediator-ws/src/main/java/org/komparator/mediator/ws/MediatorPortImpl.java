package org.komparator.mediator.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClient;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

@WebService(
		endpointInterface = "org.komparator.mediator.ws.MediatorPortType", 
		wsdlLocation = "mediator.1_0.wsdl", 
		name = "MediatorWebService", 
		portName = "MediatorPort", 
		targetNamespace = "http://ws.mediator.komparator.org/", 
		serviceName = "MediatorService"
)
@HandlerChain(file = "/mediator-ws_handler-chain.xml")

public class MediatorPortImpl implements MediatorPortType {

	// end point manager
	private MediatorEndpointManager endpointManager;
	
	//Global variables

	private List<CartView> _listcarts = new ArrayList<CartView>();
	public List<CartView> getListCarts() { return _listcarts; }
	
	private List<ShoppingResultView> _history = new ArrayList<ShoppingResultView>();
	public List<ShoppingResultView> getHistory() { return _history; }
	
	private Map _requests = new HashMap();
	
	private int _count = 1;

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}
	
	@Resource
	private WebServiceContext webServiceContext;

	// Main operations -------------------------------------------------------


	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception  {
		//Verifica o productId
		if(productId==null || productId.trim().length()==0){
			throwInvalidItemId("Invalid product ID\n");
		}
		//Cria lista de items
		List<ItemView> items = new ArrayList<ItemView>();
		//Procura todos os suppliers
		List<SupplierClient> suppliers = getAllSuppliers();

		//Procura um produto com este id em cada supplier
		for (SupplierClient supplier : suppliers){
			ProductView product = null;
			try{
				product = supplier.getProduct(productId);
			} catch (BadProductId_Exception e){
				throwInvalidItemId("Invalid product ID\n");
			}
			//Se existir, cria o item e adiciona-o a lista
			if (product != null){
				ItemView item = createItem(product, supplier);
				items.add(item);
			}
		}
		//Retorna lista ordenada pelo preco dos items
		return sortPrice(items);
	}

	@Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
		if (descText == null || descText.trim().length() == 0){
			throwInvalidText("Invalid product description\n"); 
		}
		List<ItemView> items = new ArrayList<ItemView>();
		//Procura todos os suppliers
		List<SupplierClient> suppliers = getAllSuppliers();
		//Para cada supplier, procura um produto cuja descricao contenha a pretendida
		for (SupplierClient supplier : suppliers){
			List<ProductView> products = new ArrayList<ProductView>();
			try {
				products = supplier.searchProducts(descText);
			} catch (BadText_Exception e){ 
				throwInvalidText("Invalid product description\n"); 
			}
			//Se houver produtos cria um item para cada e adiciona-os a lista
			if (products.size() > 0){
				for (ProductView product : products){
					ItemView item = createItem(product, supplier);
					items.add(item);
				}
			}
		}
		//Retorna lista ordenada por ordem alfabetica e, em caso de empate, preco
		return sortAlpha(items);
	}

	@Override
	public ShoppingResultView buyCart(String cartId, String creditCardNr) throws EmptyCart_Exception,
																	InvalidCartId_Exception, InvalidCreditCard_Exception {
		String requestId = getMessageId();
		if (_requests.get(requestId) == null){
			if (cartId == null || cartId.trim().length() == 0){
				throwInvalidCartId("Invalid cart ID\n"); 
			}
			//Confere se o carro existe
			CartView cartview = null;
			for (CartView _cartview : _listcarts){
				if (_cartview.getCartId().equals(cartId)) cartview = _cartview;
			}
			//Caso nao exista
			if (cartview == null) throwInvalidCartId("Invalid cart id.");
			//Caso esteja vazio
			if (cartview.getItems().size() == 0) throwEmptyCart("The cart is empty.");
	
			if (creditCardNr == null || creditCardNr.trim().length() == 0){
				throwInvalidCreditCard("Invalid credit card number\n"); 
			}
			//Verifica cartao de credito
			CreditCardClient cc = null;
			try {
				cc = new CreditCardClient( endpointManager.getUddiNaming().lookup("CreditCard"));
			} catch (Exception e1) { }
			
			if (!cc.validateNumber(creditCardNr)) 
				throwInvalidCreditCard("Invalid credit card number.");
			
			//Variaveis locais
			ShoppingResultView view = new ShoppingResultView();
			List<CartItemView> purchasedItems = new ArrayList<CartItemView>();
			List<CartItemView> droppedItems = new ArrayList<CartItemView>();
			Result result = null;
			int price = 0;
			
			synchronized(this){
				//Percorre os produtos do carro
				for (CartItemView _cartitemview : cartview.getItems()){
					int quantity = _cartitemview.getQuantity();
					String productid = _cartitemview.getItem().getItemId().getProductId();
					String supplierid = _cartitemview.getItem().getItemId().getSupplierId();
					
					//Efetua a compra junto do fornecedor
					for (SupplierClient supplier : getAllSuppliers()){
						if (supplier.getWsName().equals(supplierid)){
							try{
								supplier.buyProduct(productid, quantity);
								price += supplier.getProduct(productid).getPrice() * quantity;
								purchasedItems.add(_cartitemview);
							} catch (Exception e){
								droppedItems.add(_cartitemview);
							}
						}
					}
				}
				//Se houver produtos que nao foram comprados
				if (!droppedItems.isEmpty()){
					//mas se tambem houver alguns comprados -- compra parcial.
					if (!purchasedItems.isEmpty()) result = Result.PARTIAL;
					//se nenhum for comprado -- compra vazia
					else result = Result.EMPTY;
				}
				//Se todos os produtos foram comprados
				else result = Result.COMPLETE;
				
				
				//Setters do ShoppingResultView
				view.getPurchasedItems().addAll(purchasedItems);
				view.getDroppedItems().addAll(droppedItems);
				view.setId("SHOPPING"+ Integer.toString(_count));
				_count++;
				view.setResult(result);
				view.setTotalPrice(price);
				
				//Adiciona ao historico
				_history.add(view);
				LifeProof.updateHistory("add", view, requestId);
				System.out.println("Adding " + view.getId() + " @ secundary mediator!");
				
				//Guarda a resposta no mapa
				_requests.put(requestId, view);
				
				//System.exit(0);
				
				return view;
			}
		}
		else{ 
			System.out.println("Order already done.");
			return (ShoppingResultView) _requests.get(requestId);
		}
	}

	@Override
	public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		String requestId = getMessageId();
		if (_requests.get(requestId) == null){
			//Verifica itemId
			if (itemId==null || itemId.getProductId()==null || itemId.getProductId().trim().length() == 0 
					|| itemId.getSupplierId()==null || itemId.getSupplierId().trim().length() == 0 )
				throwInvalidItemId("Invalid item id.");
			//Verifica o cartId
			if (cartId == null || cartId.trim().length() == 0) throwInvalidCartId("Invalid cart id.");
			//Verifica se a quantidade e valida
			if (itemQty < 1) throwInvalidQuantity("Quantity must be bigger than 0.");
			//Procura se ja existe o carro onde se quer adicionar o produto
			//Se nao existir, cria-o e da-lhe o id.
			CartView cart = null;
			boolean alreadyExists = false;
			for (CartView _cart : listCarts()){
				if (_cart.getCartId().equals(cartId)){
					cart = _cart;
					alreadyExists = true;
				}
			}
			if (cart == null){
				cart = new CartView();
				cart.setCartId(cartId);
			}
			//Procura o supplier
			synchronized(this){
				for (SupplierClient supplier : getAllSuppliers()){
					//System.out.println(supplier.getWsName());
					if (supplier.getWsName().equals(itemId.getSupplierId())){
						ProductView product = null;
						try{
							//Obtem o produto atraves do seu id
							product = supplier.getProduct(itemId.getProductId());
						} catch (BadProductId_Exception e) {
							throwInvalidItemId("Invalid product Id");
						}
						if (product != null){
							//Verifica se o carrinho nao contem ja o produto pretendido, se tiver testa a quantidade, se passar, soma-a
							for (CartItemView view : cart.getItems()){
								if (view.getItem().getItemId().getProductId().equals(itemId.getProductId()) && 
										view.getItem().getItemId().getSupplierId().equals(itemId.getSupplierId())){
									if ((view.getQuantity() + itemQty) < product.getQuantity()){
										view.setQuantity(view.getQuantity() + itemQty);
										return;
									}
									else throwNotEnoughItems("The quantity needed is not available.");
								}
							}
							//Verifica se ha quantidade suficiente para o pretendido
							if (product.getQuantity() >= itemQty){
								//Cria o item a adicionar ao carro
								ItemView itemview = new ItemView();
								itemview.setDesc(product.getDesc());
								itemview.setItemId(itemId);
								itemview.setPrice(product.getPrice());
								
								CartItemView item = new CartItemView();
								item.setItem(itemview);
								item.setQuantity(itemQty);
								
								//Adiciona ao carro
								
								cart.getItems().add(item);
								LifeProof.updateCarts("update", cart, requestId);
								System.out.println("Updating " + cart.getCartId() + " @ secundary mediator!");
								_requests.put(requestId, cart);
							}
							else throwNotEnoughItems("The quantity needed is not available.");
						}
						else throwInvalidItemId("Invalid product Id");
					}
				}
			//Se o carro ainda nao estiver listado, junta-lo
			if (!alreadyExists){ 
				listCarts().add(cart);
				LifeProof.updateCarts("add", cart, requestId);
				System.out.println("Adding " + cart.getCartId() + " @ secundary mediator!");
				_requests.put(requestId, cart);
			}
			}
		}
		else System.out.println("Order already done.");
	}	

	// Auxiliary operations --------------------------------------------------	
	private String getMessageId() {
		MessageContext messageContext = webServiceContext.getMessageContext();
		String id = (String) messageContext.get("IDENTIFIER");
		System.out.println("Message " + id +  " approved.");
		return id;
	}
	
	@Override
	public String ping(String arg0) {
		if (arg0 == null || arg0.trim().length() == 0)
			arg0 = "friend";
	
		//Construi cabecalho com identificacao do mediador
		String wsName = "mediator";

		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(arg0);
		builder.append(" from ").append(wsName + "\n");
		
		//Procura todos os fornecedores
		List<SupplierClient> clients = getAllSuppliers();
		
		if (!clients.isEmpty()){
			for (SupplierClient client : clients)
				builder.append(client.ping(arg0) + "\n");
		}

		return builder.toString();		
	}
	
	@Override
	public void clear() {
		//Faz reset de variaveis globais
		synchronized(this){
			_listcarts = new ArrayList<CartView>();
			LifeProof.updateCarts("clean", null, null);
			_history = new ArrayList<ShoppingResultView>();
			LifeProof.updateHistory("clean", null, null);
		}
		System.out.println("Cleaning lists @ secundary mediator!");
		_count = 1;
		List<SupplierClient> suppliers = getAllSuppliers();
		for (SupplierClient supplier : suppliers){
			supplier.clear();
		}
		suppliers.clear();
	}
	
	@Override
	public List<CartView> listCarts() {
		return _listcarts;
	}
	
	@Override
	public List<ShoppingResultView> shopHistory() {
		Collections.reverse(_history);
		return _history;
	}
	
	// View helpers -----------------------------------------------------
	
	private List<SupplierClient> getAllSuppliers() {
		List<SupplierClient> clients = new ArrayList<SupplierClient>();
		Collection<UDDIRecord> records;
		try{
			records = endpointManager.getUddiNaming().listRecords("A42_Supplier%");
		} catch (UDDINamingException e){
			System.out.println("Error listing suppliers");
			return null;
		}
		for (UDDIRecord record : records){
			try{
				SupplierClient client = new SupplierClient(record.getUrl());
				client.setWsName(record.getOrgName());
				clients.add(client);
			}catch (SupplierClientException e){
				System.out.println("Error adding clients");
			}
		}
		return clients;			
	}

	public ItemView createItem(ProductView product, SupplierClient supplier){
		ItemView item = new ItemView();
		ItemIdView itemid = new ItemIdView();
		
		itemid.setProductId(product.getId());
		itemid.setSupplierId(supplier.getWsName());
		item.setItemId(itemid);
		item.setDesc(product.getDesc());
		item.setPrice(product.getPrice());
		
		return item;
	}
	
	public List<ItemView> sortPrice(List<ItemView> items){
		//Insertion Sort Algorithm
		for (int i = 1 ; i < items.size() ; i++){
		      ItemView index = items.get(i);
		      int price_index = index.getPrice();
		      int j = i;
		      while (j > 0 && items.get(j-1).getPrice() > price_index){
		    	  items.set(j, items.get(j-1));
		          j--;
		      }
		      items.set(j, index);
		}
		return items;
	}
	
	public List<ItemView> sortAlpha(List<ItemView> items){
		//Cria um sort para ordenar alfabeticamente os ItemView
		if (items.size() > 0) {
			  Collections.sort(items, new Comparator<ItemView>() {
			      @Override
			      public int compare(final ItemView item1, final ItemView item2) {
			          return item1.getItemId().getProductId().compareTo(item2.getItemId().getProductId());
			      }
			  });
		}
		//Insertion Sort
		for (int i = 1 ; i < items.size() ; i++){
		      ItemView index = items.get(i);
		      int price_index = index.getPrice();
		      String id_index = index.getItemId().getProductId();
		      
		      int j = i;
		      
		      //So ordena em caso de empate
		      while (j > 0 && items.get(j-1).getPrice() > price_index && 
		    		  items.get(j-1).getItemId().getProductId().equals(id_index)){
		    	  items.set(j, items.get(j-1));
		          j--;
		      } 
		      items.set(j, index);
		}
		return items;
	}
	


	@Override
	public void imAlive() {
		if (endpointManager.getStatus() == false){
			System.out.println("Hello from primary mediator!");
			LifeProof.setLastLife(new Date());
		}
	}
	
	@Override
	public void updateHistory(String command, ShoppingResultView shopResult, String requestId) {
		System.out.println("Updating History...");
		if (command.equals("add")){ 
			_history.add(shopResult);
			System.out.println(command + " " + shopResult.getId());
			_requests.put(requestId, shopResult);
		}
		if (command.equals("clean")){
			_history = new ArrayList<ShoppingResultView>();
			System.out.println(command);
		}
	}

	@Override
	public void updateCarts(String command, CartView cart, String requestId) {
		System.out.println("Updating ListCarts...");
		if (command.equals("add")){ 
			_listcarts.add(cart);
			_requests.put(requestId, cart);
		}
		if (command.equals("remove")){ 
			_listcarts.remove(cart);
			_requests.put(requestId, cart);
		}
		if (command.equals("update")){
			for (CartView _cart : _listcarts){
				if (_cart.getCartId().equals(cart.getCartId())){
					_listcarts.remove(_cart);
					_listcarts.add(cart);
					_requests.put(requestId, cart);
				}
			}
		}
		if (command.equals("clean")) _listcarts = new ArrayList<CartView>();
		if (cart != null) System.out.println(command + " " + cart.getCartId());
		else System.out.println(command);
	}

    
	// Exception helpers -----------------------------------------------------

	/** Helper method to throw new InvalidQuantity exception */
	private void throwInvalidQuantity(final String message) throws InvalidQuantity_Exception {
		InvalidQuantity faultInfo = new InvalidQuantity();
		faultInfo.message = message;
		throw new InvalidQuantity_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new EmptyCart exception */
	private void throwEmptyCart(final String message) throws EmptyCart_Exception {
		EmptyCart faultInfo = new EmptyCart();
		faultInfo.message = message;
		throw new EmptyCart_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new InvalidCreditCard exception */
	private void throwInvalidCreditCard(final String message) throws InvalidCreditCard_Exception {
		InvalidCreditCard faultInfo = new InvalidCreditCard();
		faultInfo.message = message;
		throw new InvalidCreditCard_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new InvalidCartId exception */
	private void throwInvalidCartId(final String message) throws InvalidCartId_Exception {
		InvalidCartId faultInfo = new InvalidCartId();
		faultInfo.message = message;
		throw new InvalidCartId_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new InvalidItemId exception */
	private void throwInvalidItemId(final String message) throws InvalidItemId_Exception {
		InvalidItemId faultInfo = new InvalidItemId();
		faultInfo.message = message;
		throw new InvalidItemId_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new InvalidText exception */
	private void throwInvalidText(final String message) throws InvalidText_Exception {
		InvalidText faultInfo = new InvalidText();
		faultInfo.message = message;
		throw new InvalidText_Exception(message, faultInfo);
	}
	
	/** Helper method to throw new NotEnoughItems exception */
	private void throwNotEnoughItems(final String message) throws NotEnoughItems_Exception {
		NotEnoughItems faultInfo = new NotEnoughItems();
		faultInfo.message = message;
		throw new NotEnoughItems_Exception(message, faultInfo);
	}

}
