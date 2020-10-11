package staciwa.projektarbeit;

public class Tank {

	private Pasteurizator pasti;
	private double maxCapacity = 100.0;
	private String capacityUnit = "liter";
	private double currentLiquidLevel = 0.0;
	private double changeRate = 10;
	private boolean isFull = false;
	private boolean isEmpty = true;
	
	public Tank(Pasteurizator pasti) {
		this.pasti = pasti;
	}
	
	public double getMaxCapacity() {
		return maxCapacity;
	}
	
	public String getCapacityUnit() {
		return capacityUnit;
	}
	
	public double getCurrentLiquidLevel() {
		return currentLiquidLevel;
	}
	
	public void setCurrentLiquidLevel(double newLevel) {
		this.currentLiquidLevel = newLevel;
	}
	/*
	public boolean getIsFull() {
		boolean result = false;
		if (currentLiquidLevel <= (maxCapacity - changeRate)) {
			result = true;
		}
		return result;
	}
	*/
	
	public boolean getIsFull() {
		return isFull;
	}
	
	public void setIsFull(boolean state) {
		this.isFull = state;
	}
	
	public boolean getIsEmpty() {
		boolean result = false;
		if (currentLiquidLevel == 0.0) {
			result = true;
		}
		return result;
	}
	
	public void fill() {
		System.out.println("Tank: The fill() methode has been invoked.");
		isEmpty = false;
		//boolean tankNotFull = true;
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
	
	public void empty() {
		System.out.println("Tank: methode empty()");
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