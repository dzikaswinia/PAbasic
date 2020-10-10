package staciwa.projektarbeit;

public class Heater {
	
	private double maxTemp = 90.0;
	private boolean isOn = false;
	private double currentTemp = 0.0;
	
	public double getCurrentTemp() {
		return currentTemp;
	}
	
	public double getMaxTemp() {
		return maxTemp;
	}
	
	public void tankIsFull() {
		heat();
	}
	
	public void heat() {
		System.out.println("Heater:		The method heat() has been activated.");
		
		if (currentTemp <= (maxTemp - 10)) {
			
			isOn = true;
			currentTemp += 10.0;
		} else {
			System.out.println("Maximal temperatur has been reached.");
			isOn = true;
		}
	}
}
