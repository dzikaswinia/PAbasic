package staciwa.projektarbeit;

public class Tank {

	private double maxCapacity = 100.0;
	private String capacityUnit = "liter";
	private double currentLiquidLevel = 0.0;
	private double changeRate = 10;
	
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
	
	public boolean getIsFull() {
		boolean result = false;
		if (currentLiquidLevel <= (maxCapacity - changeRate)) {
			result = true;
		}
		return result;
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
		boolean tankNotFull = true;
		while(tankNotFull) {
			System.out.println("Tank: Current liquid level - " + currentLiquidLevel);
			if (currentLiquidLevel <= (maxCapacity - changeRate)) {
				currentLiquidLevel += changeRate;
			} else {
				tankNotFull = false;
				System.out.println("Tank: Tank is full.");
			}
		}
	}
	
	public void empty() {
		boolean tankNotEmpty = true;
		while(tankNotEmpty) {
			System.out.println("Tank: Current liquid level - " + currentLiquidLevel);
			if (currentLiquidLevel > 0) {
				currentLiquidLevel -= changeRate;
			} else {
				tankNotEmpty = false;
				System.out.println("Tank: Tank is empty.");
			}
		}
	}
	
	
}