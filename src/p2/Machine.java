package p2;

import java.util.ArrayList;
import java.util.List;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;
	public final int CookTimeMs;
    public int myCapacity;
    List<Food> listOfProducedFood;
    
	//YOUR CODE GOES HERE...


	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	/*
	 * I am adding PreparationTime to this Machine constructor so while creating 
	 * an Machine Instance I Can send the time taken by it so that CookAnItem 
	 * can sleep that many milliseconds to cookAnItem
	 * 
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn,int preparationTime) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		this.CookTimeMs = preparationTime;
		this.myCapacity = capacityIn;
		listOfProducedFood =new ArrayList<Food>();
	}
	

	

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	/*
	 * PreCondition : Capacity of this machine should be available when you cook Food
	 * 
	 * PostCondition : If the noOfItemsToBeCooked is equal to the available capacity
	 *                 it returns the number of items it finished cooking
	 *                 
	 * Approach : This method will take an number of items to be cooked from the 
	 *            the cook and check whether the machine has capacity. 
	 *            If the machine has capacity it will cook the noOfItemsToBeCooked
	 *            of items and return the actual no of noOfItemsActuallyCooked it actually
	 *            cooked.If it returns 0 then their was no capacity was available in 
	 *            this machine when cook calls this method.
	 *                          
	 */
	public void makeFood(Cook cookingForthisCook) throws InterruptedException {
		//YOUR CODE GOES HERE...
		listOfProducedFood.add(machineFoodType);
		Thread cook = new Thread(new CookAnItem(cookingForthisCook));
		cook.start();
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		private Cook c;
		public CookAnItem(Cook c)
		{
			this.c =c;
		}
		public void run() {
			try {
				/*
				 * This thread will sleep for an CookTimeMS(Preparation time for 
				 * an Machine to cook Each Item )
				 * In Order to cook the food Item 
				 */
				    Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this,machineFoodType));
					Thread.sleep(CookTimeMs);
					Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, machineFoodType));
					synchronized (listOfProducedFood) {
						listOfProducedFood.remove(0);
						listOfProducedFood.notifyAll();
					}
					synchronized (c.completeMyFoods) {
						c.completeMyFoods.add(machineFoodType);
						c.completeMyFoods.notifyAll();
					}
					
				
			} catch(InterruptedException e) { }
		}
	}
 

	public String toString() {
		return machineName;
	}
}