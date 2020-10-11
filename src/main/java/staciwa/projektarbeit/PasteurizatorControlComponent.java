package staciwa.projektarbeit;

import org.eclipse.basyx.models.controlcomponent.ControlComponentChangeListener;
import org.eclipse.basyx.models.controlcomponent.ExecutionMode;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.eclipse.basyx.models.controlcomponent.OccupationState;
import org.eclipse.basyx.models.controlcomponent.SimpleControlComponent;

public class PasteurizatorControlComponent extends SimpleControlComponent implements ControlComponentChangeListener {

	public static final String OPMODE_BASIC = "BSTATE";
	public static final String OPMODE_FILL = "FILL";
	public static final String OPMODE_EMPTY = "EMPTY";
	
	private Pasteurizator pasti;
	
	public PasteurizatorControlComponent(Pasteurizator pasti) {
		this.pasti = pasti;
		addControlComponentChangeListener(this);
	}
	
	// changing execution state
	@Override
	public void onChangedExecutionState(ExecutionState newExecutionState) {
		System.out.println("CC:	new execution state: " + newExecutionState ); //TODO
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
	
	/* Es füllt den Tank 
	 * */
	protected void fillTank() {
		
		new Thread(() -> {
			System.out.println("\nCC: the tank is full - " + pasti.getTank().getIsFull());
			if (!(pasti.getTank().getIsFull())) {
				pasti.getTank().fill();
			} else {
				pasti.getTank().setIsFull(true);
				System.out.println("\nCC: the tank is full - " + pasti.getTank().getIsFull());
				System.out.println("\nCC: the current temperatur of the liquid is " + pasti.getHeater().getCurrentTemp());
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setExecutionState(ExecutionState.COMPLETE.getValue());
		}).start();
		

	}
	
	protected void emptyTank() {
		
		new Thread(() -> {
			System.out.println("CC: method emptyTank() has been invoked.");
			if (pasti.getTank().getIsEmpty()) {
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