package staciwa.projektarbeit;

import org.eclipse.basyx.models.controlcomponent.ControlComponentChangeListener;
import org.eclipse.basyx.models.controlcomponent.ExecutionMode;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.eclipse.basyx.models.controlcomponent.OccupationState;
import org.eclipse.basyx.models.controlcomponent.SimpleControlComponent;

public class PasteurizatorControlComponent extends SimpleControlComponent implements ControlComponentChangeListener {

	public static final String OPMODE_BASIC = "BSTATE";
	public static final String OPMODE_FILL = "FILL";
	
	private Pasteurizator pasti;
	
	public PasteurizatorControlComponent(Pasteurizator pasti) {
		this.pasti = pasti;
		addControlComponentChangeListener(this);
	}
	
	// changing execution state
	@Override
	public void onChangedExecutionState(ExecutionState newExecutionState) {
		System.out.println("CC:	new execution state: " + newExecutionState ); //TODO
		// TODO das verstehe ich nicht
		if (newExecutionState == ExecutionState.EXECUTE) {
			if (this.getOperationMode().equals(OPMODE_FILL)) {
				controlTank();
			} else {
				setExecutionState(ExecutionState.COMPLETE.getValue());
			}
		}
		
	}
	
	/* Es füllt den Tank 
	 * */
	protected void controlTank() {
		
		
		
		new Thread(() -> {
			for (int i = 0; i < 20; i++) {
				//System.out.println("CC:	current liquid level - " + pasti.getTank().getCurrentLiquidLevel());
				if (pasti.getTank().getIsFull()) {
					pasti.getTank().fill();
				} 
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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