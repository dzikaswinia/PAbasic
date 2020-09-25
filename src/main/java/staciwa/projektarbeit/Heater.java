package staciwa.projektarbeit;

public class Heater {
	
	private double maxTemp = 90.0;
	private boolean isOn = false;
	private double currentTemp = 0.0;
	
	public double getCurrentTemp() {
		return currentTemp;
	}
	
	public void heat() {
		
		if (currentTemp <= (maxTemp - 10)) {
			System.out.println("Heater is On.");
			isOn = true;
			currentTemp += 10.0;
		} else {
			System.out.println("Maximal temperatur has been reached.");
			isOn = true;
		}
	}
}
