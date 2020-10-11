package staciwa.projektarbeit;

import java.time.Duration;
import java.time.LocalTime;



public class Heater {
	
	private double maxTemp = 90.0;
	private boolean isOn = false;
	private double currentTemp = 20.0;
	private double environmentTemp = 20.0;
	private LocalTime lastTempAccess = LocalTime.now();
	
	public double getCurrentTemp() {
		
		// Miau
		
		
		LocalTime now = LocalTime.now();
		Long timeDiff = Duration.between(lastTempAccess,now).getSeconds();
		
		// simulate cooling of milk with a linear function (simple but not realistic)
	 	double tempDiff = timeDiff.doubleValue() * 0.1;
	 	double newTemp = currentTemp - tempDiff;
	 	if (newTemp < environmentTemp) {
	 		currentTemp = environmentTemp;
	 	} else {
	 		currentTemp = newTemp;
	 	}
	 	this.lastTempAccess = now;
		
		return currentTemp;
	}
	
	public void setCurrentTemp(double temp) {
		currentTemp = temp;
		lastTempAccess = LocalTime.now();
	}
	
	public double getMaxTemp() {
		return maxTemp;
	}
	//TODO
	public void tankIsFull() {
		heat();
	}
	
	public void heat() {
		System.out.println("Heater:	The method heat() has been activated.");
		while(currentTemp < maxTemp) {
			System.out.println("Heater:	current temperature - " + currentTemp);
			if (currentTemp <= (maxTemp - 10)) {
				isOn = true;	//TODO brauche ich das?
				double changedTemp = getCurrentTemp() + 10.0;
				setCurrentTemp(changedTemp);
			} else {
				//TODO vielleicht brauche ich isOn gar nicht
				System.out.println("Heater: Maximal temperatur has been reached.");
				isOn = false;	//TODO brauche ich das?
				setCurrentTemp(maxTemp);
			}
		}
	}
	
}
