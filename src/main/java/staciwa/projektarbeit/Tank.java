package staciwa.projektarbeit;

public class Tank {

	private double maxCapacity = 100.0;
	private String capacityUnit = "liter";
	private double currentLiquidLevel = 0.0;
	private double changeRate = 0.001;
	
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
		System.out.println("Tank	will be filled.");
		boolean tankNotFull = true;
		while(tankNotFull) {
			if (currentLiquidLevel <= (maxCapacity - changeRate)) {
				currentLiquidLevel += changeRate;
			} else {
				tankNotFull = false;
				System.out.println("Tank	is full. " + currentLiquidLevel);
			}
		}
	}
	
	
}