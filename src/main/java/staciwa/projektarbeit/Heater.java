package staciwa.projektarbeit;

import java.time.Duration;
import java.time.LocalTime;



public class Heater {
	
	private double maxTemp = 90.0;
	private boolean isOn = false;
	private double currentTemp = 20.0;
	private double enviromentTemp = 20.0;
	private LocalTime lastTempCall = LocalTime.now();
	
	public double getCurrentTemp() {
		LocalTime now = LocalTime.now();
		Long timeDiff = Duration.between(lastTempCall,now).getSeconds();
		// TODO
	 	double tempDiff = timeDiff.doubleValue() * 0.1;
	 	double newTemp = currentTemp - tempDiff;
	 	if (newTemp < currentTemp) {
	 		currentTemp = enviromentTemp;
	 	} else {
	 		currentTemp = newTemp;
	 	}
	 	this.lastTempCall = now;
		
		return currentTemp;
	}
	
	public double getMaxTemp() {
		return maxTemp;
	}
	
	public void tankIsFull() {
		heat();
	}
	
	public void heat() {
		System.out.println("Heater:	The method heat() has been activated.");
		while(currentTemp != maxTemp) {
			System.out.println("Heater:	current temperature - " + currentTemp);
			if (currentTemp <= (maxTemp - 10)) {
				isOn = true;
				currentTemp += 10.0;
			} else {
				//TODO vielleicht brauche ich isOn gar nicht
				System.out.println("Maximal temperatur has been reached.");
				isOn = false;
			}
		}
	}
	
}
