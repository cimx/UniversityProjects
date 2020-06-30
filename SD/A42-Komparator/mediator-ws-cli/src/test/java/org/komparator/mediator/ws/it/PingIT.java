package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


/**
 * Test suite
 */
public class PingIT extends BaseIT {

	//Testes a correr com 2 suppliers associados
    @Test
    public void pingEmptyTest() {
        assertNotNull(mediatorClient.ping("test"));
    }
    
    @Test
    public void pingTest() {
    	assertEquals("Hello test from mediator\nHello test from supplier\nHello test from supplier\nHello test from supplier\n", mediatorClient.ping("test"));
    }

}

