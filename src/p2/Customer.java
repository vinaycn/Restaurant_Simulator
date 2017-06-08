
package p2;

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */

/*
 * InVariant : Customer should have space in the shop to enter and should be able
 *             place the order by generating all the necessary events. 
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum;    
	public boolean myOrderStatus = false;
	private static int runningCounter = 0;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
	}

	public List<Food> getMyOrder()
	{
		return order;
	}
	
	public int getOrderNumber()
	{
		return orderNum;
	}
	public String toString() {
		return name;
	}

	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	public void run() {
		
		//YOUR CODE GOES HERE...
		//////My Approach///////
		 //First log  Customerstarting event
		  Simulation.logEvent(SimulationEvent.customerStarting(this));
		  System.out.println(this.name + " is Starting  ");
		 // I will acquire this lock to check whether there is a table available in the shop
		 //so the customer can enter
		 synchronized(Simulation.LockForControllingCustomerEntry)
		  {
//		    check the is Table available for this customer
		    while(Simulation.keepTrackNumberOfTablesAcquired >= Simulation.numTable)
		    {
		    	try {
					Simulation.LockForControllingCustomerEntry.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		    }
		    //Increment the keepTrackNumberOfTablesAcquired indicating customer has entered
		    Simulation.keepTrackNumberOfTablesAcquired++;
		  }
           //Log the event 
		 System.out.println(this.name + " Entered Shop  ");
           Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
           
           synchronized (Simulation.customerEntry) {
			Simulation.customerEntry.add(this);
			Simulation.customerEntry.notifyAll();
			Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, order, orderNum));      	   
		}
          
           System.out.println(this.name + " Placed Order  ");
		     
		  synchronized (this) {
			  while(!myOrderStatus)
				 {
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
		}   
           
		
		 Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, order, orderNum));
		 System.out.println(this.name + " Received Order  ");
		  
		     synchronized(Simulation.LockForControllingCustomerEntry)
		     {
             //Decrement Simulation.keepTrackNumberOfTablesAcquired-- by 1 indicating one table 
             //is free so the customer who is waiting can enter the shop
		       Simulation.keepTrackNumberOfTablesAcquired--;
		       Simulation.LockForControllingCustomerEntry.notifyAll();
		     Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
		  }
		     System.out.println(this.name + " Leaving shop  ");
			
		
	}
	

	
}