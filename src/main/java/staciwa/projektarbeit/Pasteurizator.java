package staciwa.projektarbeit;

public class Pasteurizator {
	
	private String name;
	private Tank tank;
	private Heater heater;
	
	public Pasteurizator(String name) {
		this.name = name;
		this.tank = new Tank();
		this.heater = new Heater();
	}
	
	public String getName() {
		return name;
	}
	
	public Tank getTank() {
		return tank;
	}
	
	public Heater getHeater() {
		return heater;
	}
	
	public void fillTank() {
		tank.fill();
	}

}