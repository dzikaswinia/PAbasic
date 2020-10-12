package staciwa.projektarbeit;

import java.time.Duration;
import java.time.LocalTime;
/**
 * Class Heater.
 * @author monika
 *
 */

public class Heater {
	
	private double maxTemp = 90.0;
	private double currentTemp = 20.0;
	private double environmentTemp = 20.0;
	private LocalTime lastTempAccess = LocalTime.now();
	/**
	 * This method gives current liquid temperature back.
	 * 
	 * It is simulating cooling of the liquid by calculating second since
	 * last time a value of a current temperature were changed {@link lastTempAccess}
	 * and multiplying it with factor 0.1.
	 * 
	 * The lowest value for {@link currentTemp} is the {@link environmentTemp}.
	 *  
	 * @return current temperature of the liquid in the tank.
	 */
	public double getCurrentTemp() {
		
		// TODO Miau
		
		
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
	/**
	 * This method set new value for the attribute {@link currentTemp}
	 * and reset the value of {@link lastTempAccess}.
	 * 
	 * @param temp - new current temperature of the liquid.
	 */
	public void setCurrentTemp(double temp) {
		currentTemp = temp;
		lastTempAccess = LocalTime.now();
	}
	/**
	 * Gives maximal temperature back.
	 * 
	 * @return maximal temperature of the liquid in tank.
	 */
	public double getMaxTemp() {
		return maxTemp;
	}
	/**
	 * Activates the method {@link heat}.
	 */
	public void tankIsFull() {
		heat();
	}
	/**
	 * This method rises the temperature of the liquid in tank {@link currentTemp} 
	 * by 10.0 till the {@link maxTemp} is reached. 
	 */
	public void heat() {
		System.out.println("Heater:	The method heat() has been activated.");
		while(currentTemp < maxTemp) {
			System.out.println("Heater:	current temperature - " + currentTemp);
			if (currentTemp <= (maxTemp - 10)) {
				double changedTemp = getCurrentTemp() + 10.0;
				setCurrentTemp(changedTemp);
			} else {
				System.out.println("Heater: Maximal temperatur has been reached.");
				setCurrentTemp(maxTemp);
			}
		}
	}
	
}
