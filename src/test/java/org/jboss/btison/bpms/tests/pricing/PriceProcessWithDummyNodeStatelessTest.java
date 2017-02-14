package org.jboss.btison.bpms.tests.pricing;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

public class PriceProcessWithDummyNodeStatelessTest {
	
	private static KieContainer kieContainer;
	
	private static KieCommands commandsFactory;
	
    @BeforeClass
    public static void setupKieBase() {
        KieServices ks = KieServices.Factory.get();
        kieContainer = ks.newKieClasspathContainer(); 
        kieContainer.getKieBase();
        commandsFactory = KieServices.Factory.get().getCommands();
    }
    
    @Test
    public void testEmptyShoppingCard() throws Exception {
    	StatelessKieSession kSession = kieContainer.newStatelessKieSession("ksession.stateless");
    	
    	ShoppingCart sc = new ShoppingCart();
		sc.setCartItemTotal(0);
		sc.setCartItemPromoSavings(0);
		sc.setShippingTotal(0);
		sc.setShippingPromoSavings(0);
		sc.setCartTotal(0);
		
		List<Command<?>> commands = new ArrayList<Command<?>>();
        BatchExecutionCommand executionCommand = commandsFactory.newBatchExecution(commands);
        commands.add(commandsFactory.newInsert(sc, "shoppingCart"));
        commands.add(commandsFactory.newStartProcess("org.jboss.btison.bpms.tests.pricing.PriceProcessWithDummyNode"));
        
        ExecutionResults results = kSession.execute(executionCommand);
        ShoppingCart result = (ShoppingCart) results.getValue("shoppingCart");
    	Assert.assertNotNull(result);
    	Assert.assertEquals(0, new Double(sc.getCartItemTotal()).intValue());
    }
    
    @Test
    public void testShoppingCardWithProduct() throws Exception {
    	StatelessKieSession kSession = kieContainer.newStatelessKieSession("ksession.stateless");
    	
    	ShoppingCart sc = new ShoppingCart();
		sc.setCartItemTotal(0);
		sc.setCartItemPromoSavings(0);
		sc.setShippingTotal(0);
		sc.setShippingPromoSavings(0);
		sc.setCartTotal(0);
		
		Product product = new Product("100000", "product", "product", 25.0);
		
		ShoppingCartItem sci = new ShoppingCartItem();
		sci.setShoppingCart(sc);
		sci.setItemId(product.getItemId());
		sci.setName(product.getName());
		sci.setPrice(product.getPrice());
		sci.setQuantity(1);
		sci.setPromoSavings(0);		
		
		List<Command<?>> commands = new ArrayList<Command<?>>();
        BatchExecutionCommand executionCommand = commandsFactory.newBatchExecution(commands);
        commands.add(commandsFactory.newInsert(sc, "shoppingCart"));
        commands.add(commandsFactory.newInsert(sci));
        commands.add(commandsFactory.newStartProcess("org.jboss.btison.bpms.tests.pricing.PriceProcessWithDummyNode"));
        
        ExecutionResults results = kSession.execute(executionCommand);
        ShoppingCart result = (ShoppingCart) results.getValue("shoppingCart");
    	Assert.assertNotNull(result);
    	Assert.assertEquals(25, new Double(sc.getCartItemTotal()).intValue());
    }

}
