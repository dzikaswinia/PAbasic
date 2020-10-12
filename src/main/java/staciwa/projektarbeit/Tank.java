package staciwa.projektarbeit;
/**
 * Class tank.
 * 
 * @author monika
 *
 */
public class Tank {

	private Pasteurizator pasti;
	private double maxCapacity = 100.0;
	private double currentLiquidLevel = 0.0;
	private double changeRate = 10;
	private boolean isFull = false;
	private boolean isEmpty = true;
	/**
	 * Class constructor.
	 * @param pasti - pasteurizator.
	 */
	public Tank(Pasteurizator pasti) {
		this.pasti = pasti;
	}
	/**
	 * Gives a maximal capacity of the tank in liter back.
	 * @return A maximal capacity of the tank.
	 */
	public double getMaxCapacity() {
		return maxCapacity;
	}
	/**
	 * Gives a current liquid level in the tank back.
	 * @return A current liquid level in the tank.
	 */
	public double getCurrentLiquidLevel() {
		return currentLiquidLevel;
	}
	/**
	 * Sets new value for the {@link currentLiquidLevel}.
	 * @param newLevel New liquid level.
	 */
	public void setCurrentLiquidLevel(double newLevel) {
		this.currentLiquidLevel = newLevel;
	}
	/**
	 * Gives value true back, when the tank is full.
	 * @return False - the tank is not full, True - the tank is full.
	 */
	public boolean getIsFull() {
		return isFull;
	}
	/**
	 * Sets a new value for {@link isFull}.
	 * @param state <code>true</code> means the tank is full.
	 */
	public void setIsFull(boolean state) {
		this.isFull = state;
	}
	/**
	 * Gives the value true, when the tank is empty.
	 * @return True - the tank is empty, False - the tank is not empty.
	 */
	public boolean getIsEmpty() {
		return this.isEmpty;
	}
	/**
	 * Sets a new value for {@link isEmpty}.
	 * @param state <code>true</code> means the tank is empty.
	 */
	public void setIsEmpty(boolean state) {
		this.isEmpty = state;
	}
	/**
	 * This method checks the liquid level and if it is lower than the difference
	 * between maximal capacity and the changeRate, it decreases the level of the liquid.
	 * 
	 * When the maximal liquid level is reached, it sets new value for the attribute {@link isFull}
	 * and  calls the method of {@link Heater} {@link tankIsFull}.  
	 */
	public void fill() {
		System.out.println("Tank: The fill() methode has been invoked.");
		isEmpty = false;
		while(!(isFull)) {
			System.out.println("Tank: Current liquid level - " + currentLiquidLevel);
			if (currentLiquidLevel <= (maxCapacity - changeRate)) {
				currentLiquidLevel += changeRate;
			} else {
				isFull = true;
				System.out.println("Tank: Tank is full.");
				pasti.getHeater().tankIsFull();
			}
		}
	}
	/**
	 * This method decrease the current liquid level in the tank till the tank is empty.
	 */
	public void empty() {
		System.out.println("Tank: The empty() methode has been invoked");
		isFull = false;
		while(!(isEmpty)) {
			System.out.println("Tank: Current liquid level - " + currentLiquidLevel);
			if (currentLiquidLevel > 0) {
				currentLiquidLevel -= changeRate;
			} else {
				isEmpty = true;
				System.out.println("Tank: Tank is empty.");
			}
		}
	}
	
	
}