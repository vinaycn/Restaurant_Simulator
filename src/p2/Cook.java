package p2;

import java.util.ArrayList;
import java.util.List;

/**
 * Cooks are simulation actors that have at least one field, a name. When
 * running, a cook attempts to retrieve outstanding orders placed by Eaters and
 * process them.
 */
/*
 * Invariant : Cook should be free to take the order from the customer and call
 * the appropiate machine get the food ready and notify the customer once the
 * order is ready
 */
public class Cook implements Runnable {
	private final String name;
	private Customer customerWhoGivesOrderToThisCook;
	public List<Food> completeMyFoods = new ArrayList<>();
	

	/**
	 * You can feel free modify this constructor. It must take at least the
	 * name, but may take other parameters if you would find adding them useful.
	 *
	 * @param: the
	 *             name of the cook
	 */
	public Cook(String name) {
		this.name = name;

	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows. The cook tries to retrieve orders placed
	 * by Customers. For each order, a List<Food>, the cook submits each Food
	 * item in the List to an appropriate Machine, by calling makeFood(). Once
	 * all machines have produced the desired Food, the order is complete, and
	 * the Customer is notified. The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some other thread
	 * calls the interrupt() method on it, which could raise
	 * InterruptedException if the cook is blocking), then it terminates.
	 */
	public void run() {
		// Log the CookStartingEvent once the this thread start to execute
		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while (true) {
				synchronized (Simulation.customerEntry) {
					while (Simulation.customerEntry.size() == 0) {
						Simulation.customerEntry.wait();
					}
					customerWhoGivesOrderToThisCook = Simulation.customerEntry.get(0);
					Simulation.customerEntry.remove(0);
					Simulation.logEvent(
							SimulationEvent.cookReceivedOrder(this, customerWhoGivesOrderToThisCook.getMyOrder(),
									customerWhoGivesOrderToThisCook.getOrderNumber()));
					System.out.println(this.name + " recived order from " + customerWhoGivesOrderToThisCook.toString()
							+ " customer ");
				}

				// Get each food from the list of order and place it
				for (int i = 0; i < customerWhoGivesOrderToThisCook.getMyOrder().size(); i++) {
					if (customerWhoGivesOrderToThisCook.getMyOrder().get(i) == FoodType.burger) {
						Simulation.logEvent(SimulationEvent.cookStartedFood(this,
								customerWhoGivesOrderToThisCook.getMyOrder().get(i),
								customerWhoGivesOrderToThisCook.getOrderNumber()));
						synchronized (Simulation.BurgerMachine.listOfProducedFood) {
							Simulation.BurgerMachine.makeFood(this);
						}
					} else if (customerWhoGivesOrderToThisCook.getMyOrder().get(i) == FoodType.coffee) {
						Simulation.logEvent(SimulationEvent.cookStartedFood(this,
								customerWhoGivesOrderToThisCook.getMyOrder().get(i),
								customerWhoGivesOrderToThisCook.getOrderNumber()));
						synchronized (Simulation.CoffeeMachine.listOfProducedFood) {
							Simulation.CoffeeMachine.makeFood(this);
						}
					} else {
						Simulation.logEvent(SimulationEvent.cookStartedFood(this,
								customerWhoGivesOrderToThisCook.getMyOrder().get(i),
								customerWhoGivesOrderToThisCook.getOrderNumber()));
						synchronized (Simulation.FriesMacine.listOfProducedFood) {
							Simulation.FriesMacine.makeFood(this);
						}
					}
				}

				// Cook Should wait till all the foods are completed
				synchronized (completeMyFoods) {
					
					while (completeMyFoods.size() != customerWhoGivesOrderToThisCook.getMyOrder().size()) {
						completeMyFoods.wait();
					}
                    completeMyFoods.clear();
				}
                synchronized (customerWhoGivesOrderToThisCook) {
                	customerWhoGivesOrderToThisCook.myOrderStatus = true;
                	customerWhoGivesOrderToThisCook.notifyAll();
				}
				Simulation.logEvent(SimulationEvent.cookCompletedOrder(this,customerWhoGivesOrderToThisCook.getOrderNumber()));
				System.out.println(
						this.name + " Completed " + customerWhoGivesOrderToThisCook.toString() + " order ");
			}

		} catch (InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}

	

}