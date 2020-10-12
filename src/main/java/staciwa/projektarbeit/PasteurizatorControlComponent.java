package staciwa.projektarbeit;
/**
 * Class PasteurizatorControlComponent implements Control Component for the device Pasteurizator.
 */
import org.eclipse.basyx.models.controlcomponent.ControlComponentChangeListener;
import org.eclipse.basyx.models.controlcomponent.ExecutionMode;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.eclipse.basyx.models.controlcomponent.OccupationState;
import org.eclipse.basyx.models.controlcomponent.SimpleControlComponent;

public class PasteurizatorControlComponent extends SimpleControlComponent implements ControlComponentChangeListener {

	private static final long serialVersionUID = 1L;

	// Operation modes
	public static final String OPMODE_BASIC = "BSTATE";
	public static final String OPMODE_FILL = "FILL";
	public static final String OPMODE_EMPTY = "EMPTY";
	
	private Pasteurizator pasti;
	
	public PasteurizatorControlComponent(Pasteurizator pasti) {
		this.pasti = pasti;
		addControlComponentChangeListener(this);
	}
	
	@Override
	public void onChangedExecutionState(ExecutionState newExecutionState) {
		
		System.out.println("\nCC:	new execution state: " + newExecutionState ); 
		
		if (newExecutionState == ExecutionState.EXECUTE) {
			String opMode = this.getOperationMode();
			switch(opMode) {
				case "FILL":
					fillTank();
					break;
				case "EMPTY":
					emptyTank();
					break;
				default:
					setExecutionState(ExecutionState.COMPLETE.getValue());
			}		
		}
	}
	/**
	 * This method fills the tank and when the maximal capacity is reached, it activates the heater.
	 */
	protected void fillTank() {
		
		new Thread(() -> {
			
			System.out.println("\nCC: the method fillTank has been invoked");
			
			if (!(pasti.getTank().getIsFull())) {
				pasti.getTank().fill();
			} else {
				pasti.getTank().setIsFull(true);
				//System.out.println("\nCC: the tank is full - " + pasti.getTank().getIsFull());
				System.out.println("CC: the current temperatur of the liquid is " + pasti.getHeater().getCurrentTemp());
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setExecutionState(ExecutionState.COMPLETE.getValue());
			
		}).start();
		

	}
	/**
	 * This method empties the tank.
	 */
	protected void emptyTank() {
		
		new Thread(() -> {
			
			System.out.println("\nCC: method emptyTank() has been invoked.");
			
			if (!(pasti.getTank().getIsEmpty())) {
				pasti.getTank().empty();
			} 
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setExecutionState(ExecutionState.COMPLETE.getValue());
		}).start();
		
	}
	
	@Override
	public void onVariableChange(String varName, Object newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewOccupier(String occupierId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewOccupationState(OccupationState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangedExecutionMode(ExecutionMode newExecutionMode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangedOperationMode(String newOperationMode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangedWorkState(String newWorkState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangedErrorState(String newWorkState) {
		// TODO Auto-generated method stub
		
	}
	
}