package staciwa.projektarbeit;

public class Pasteurizator {
	
	private Tank tank;
	private String name;
	
	public Pasteurizator(String name) {
		this.name = name;
		this.tank = new Tank();
	}
	
	public String getName() {
		return name;
	}
	
	public Tank getTank() {
		return tank;
	}
	
	public void fillTank() {
		tank.fill();
	}

}