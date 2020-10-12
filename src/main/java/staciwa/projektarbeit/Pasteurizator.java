package staciwa.projektarbeit;
/**
 * Class Pasteurizator. 
 * @author monika
 *
 */
public class Pasteurizator {
	
	private Tank tank;
	private Heater heater;
	
	public Pasteurizator() {
		this.tank = new Tank(this);
		this.heater = new Heater();
	}
	
	public Tank getTank() {
		return tank;
	}
	
	public Heater getHeater() {
		return heater;
	}
	
}