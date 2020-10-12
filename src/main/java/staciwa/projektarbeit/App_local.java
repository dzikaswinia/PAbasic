package staciwa.projektarbeit;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.servlet.http.HttpServlet;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistryService;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.aas.registration.restapi.DirectoryModelProvider;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.VABMultiSubmodelProvider;
import org.eclipse.basyx.models.controlcomponent.ControlComponent;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.SubModel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetypedef.PropertyValueTypeDef;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.SubModelProvider;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProviderHelper;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.basyx.vab.protocol.basyx.connector.BaSyxConnector;
import org.eclipse.basyx.vab.protocol.basyx.server.BaSyxTCPServer;
import org.eclipse.basyx.vab.protocol.http.server.AASHTTPServer;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

/**This class creates and runs an AAS for the Pasteurizator.
 * 
 * @author monika
 * **/
public class App_local 
{
	//Address and port of the device, on which the AAS is running.
	public static String AAS_IP = "192.168.2.3";
	public static int AAS_PORT = 4000;
	//Address and port of the device, on which the ControlComponent is running.
	public static String CC_IP = "192.168.2.3";
	public static int CC_PORT = 4001;
	
    public static void main( String[] args ) throws Exception
    {
        
    	System.out.println("\n\nWelcome to Pasteurizator Simulation - AAS\n\n");
        Pasteurizator pasti = new Pasteurizator();
        startMyControlComponent(pasti);
        startMyAAS(pasti);
        
    }
    
    /**This method starts the Asset Administration Shell.
     * 
     * @param pasti: the Pasteurizator
     */
    public static void startMyAAS(Pasteurizator pasti) {
    	
    	// SUBMODEL TANK
    	SubModel tankSubModel = new SubModel();
    	
    	// Properties - maximal capacity and current liquid level.
    	Property maxCapacity = new Property();
    	maxCapacity.setIdShort("maxCapacity");
    	maxCapacity.set(VABLambdaProviderHelper.createSimple(() -> {
    		return pasti.getTank().getMaxCapacity(); 
    	}, null), PropertyValueTypeDef.Double);
    	
    	
    	Property currentLiquidLevel = new Property();
    	currentLiquidLevel.setIdShort("currentLiquidLevel");
    	currentLiquidLevel.set(VABLambdaProviderHelper.createSimple(() -> {
    		double returnValue = pasti.getTank().getCurrentLiquidLevel();  
    		//System.out.println("\nApp_local: currentLiquidLevel angefragt: " + returnValue);	TODO
    		return returnValue;
    	}, null), PropertyValueTypeDef.Double);
    	   	
    	tankSubModel.addSubModelElement(maxCapacity);
    	tankSubModel.addSubModelElement(currentLiquidLevel);
    	
    	// Function - fill the tank.
    	Function<Object[], Object> tankFillInvokable = (params) -> {
		
			// Connect to the control component
			VABElementProxy proxy = new VABElementProxy("", new JSONConnector(new BaSyxConnector(App_local.AAS_IP, App_local.CC_PORT)));
 
			// Select the operation from the control component
			proxy.setModelPropertyValue("status/opMode", PasteurizatorControlComponent.OPMODE_FILL);
 
			// Start the control component operation asynchronous
			proxy.invokeOperation("/operations/service/start");
 
			// Wait until the operation is completed
			while (!proxy.getModelPropertyValue("status/exState").equals(ExecutionState.COMPLETE.getValue())) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
 
			proxy.invokeOperation("operations/service/reset");
			return null;
		};
		
		// Function - empty the tank.
    	Function<Object[], Object> tankEmptyInvokable = (params) -> {
		
			// Connect to the control component
			VABElementProxy proxy = new VABElementProxy("", new JSONConnector(new BaSyxConnector(App_local.AAS_IP, App_local.CC_PORT)));
 
			// Select the operation from the control component
			proxy.setModelPropertyValue("status/opMode", PasteurizatorControlComponent.OPMODE_EMPTY);
 
			// Start the control component operation asynchronous
			proxy.invokeOperation("/operations/service/start");
 
			// Wait until the operation is completed
			while (!proxy.getModelPropertyValue("status/exState").equals(ExecutionState.COMPLETE.getValue())) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
 
			proxy.invokeOperation("operations/service/reset");
			return null;
		};
		
		// Creating the Operations
		Operation operationFillTank = new Operation();
		Operation operationEmptyTank = new Operation();
		operationFillTank.setIdShort("fillTank");
		operationEmptyTank.setIdShort("emptyTank");
		operationFillTank.setInvocable(tankFillInvokable);
		operationEmptyTank.setInvocable(tankEmptyInvokable);
		tankSubModel.addSubModelElement(operationFillTank);
		tankSubModel.addSubModelElement(operationEmptyTank);
    	
		// Setting identifiers. 
    	tankSubModel.setIdShort("submodel_tank");
    	tankSubModel.setIdentification(IdentifierType.CUSTOM, "tank");
    	
    	
    	// SUBMODEL HEATER
    	SubModel heaterSubModel = new SubModel();
    	
    	// Properties
    	Property maxTemp = new Property();
    	maxTemp.setIdShort("maxTemp");
    	maxTemp.set(VABLambdaProviderHelper.createSimple(() -> {
    		return pasti.getHeater().getMaxTemp(); 
    	}, null), PropertyValueTypeDef.Double);
    	
    	Property currentTemp = new Property();
    	currentTemp.setIdShort("currentTemp");
    	currentTemp.set(VABLambdaProviderHelper.createSimple(() -> {
    		double returnValue = pasti.getHeater().getCurrentTemp(); 
    		//System.out.println("App_local: currentTemp angefragt " + returnValue);	TODO
    		return returnValue;
    	}, null), PropertyValueTypeDef.Double);
    	
    	heaterSubModel.addSubModelElement(maxTemp);
    	heaterSubModel.addSubModelElement(currentTemp);
    	heaterSubModel.setIdShort("submodel_heater");
    	heaterSubModel.setIdentification(IdentifierType.CUSTOM, "heater");
    	
    	/**
    	 * AAS
    	 */
    	AssetAdministrationShell aas = new AssetAdministrationShell();
    	ModelUrn aasURN = new ModelUrn("", "", "AAS", "", "", "pasteurizator", ""); 
    	aas.setIdentification(aasURN);
    	
    	// Optional in this local setting.
    	//IIdentifier aasID = new Identifier(IdentifierType.CUSTOM, "pasti");
    	aas.setIdShort("pasti");
    	
    	/**
    	 * Deployment
    	 */
    	
    	//Wrapping Submodels in IModelProvider
    	AASModelProvider aasProvider = new AASModelProvider(aas);
    	SubModelProvider tankSMProvider = new SubModelProvider(tankSubModel);
    	SubModelProvider heaterSMProvider = new SubModelProvider(heaterSubModel);
    	VABMultiSubmodelProvider fullProvider = new VABMultiSubmodelProvider ();
    	fullProvider.setAssetAdministrationShell(aasProvider);
    	fullProvider.addSubmodel("heater",heaterSMProvider);
    	fullProvider.addSubmodel("tank",tankSMProvider);
    	
    	HttpServlet aasServlet = new VABHTTPInterface<IModelProvider>(fullProvider);
    	IAASRegistryService registry = new InMemoryRegistry();
		IModelProvider registryProvider = new DirectoryModelProvider(registry);
		HttpServlet registryServlet = new VABHTTPInterface<IModelProvider>(registryProvider);
		
		// Register the VAB model at the directory (locally in this case)
		AASDescriptor aasDescriptor = new AASDescriptor(aas, "http://" + App_local.AAS_IP + ":" 
				+ App_local.AAS_PORT + "/pasti/aas");
		// Register Submodels at the AAS Descriptor.
		aasDescriptor.addSubmodelDescriptor(new SubmodelDescriptor(tankSubModel, 
				"http://" + App_local.AAS_IP + ":" + App_local.AAS_PORT + "/pasti/aas/submodels/tank/submodel"));		
		aasDescriptor.addSubmodelDescriptor(new SubmodelDescriptor(heaterSubModel, 
				"http://" + App_local.AAS_IP + ":" + App_local.AAS_PORT + "/pasti/aas/submodels/heater/submodel"));
		registry.register(aasDescriptor);
		
		// Deploy the AAS on a HTTP server
		BaSyxContext context = new BaSyxContext("", "", App_local.AAS_IP, App_local.AAS_PORT);
		context.addServletMapping("/pasti/*", aasServlet);
		context.addServletMapping("/registry/*", registryServlet);
		AASHTTPServer httpServer = new AASHTTPServer(context);
		
		httpServer.start();
		
    }
    
    /** This method creates a control component for the Pasteurizator.
     * 
     * @param pasti: Pasteurizator
     */
    public static void startMyControlComponent(Pasteurizator pasti) {
    	
    	ControlComponent cc = new PasteurizatorControlComponent(pasti);
    	// Server where the control component is reachable.
    	VABMapProvider ccProvider = new VABMapProvider(cc);
    	BaSyxTCPServer<VABMapProvider> server = new BaSyxTCPServer<>(ccProvider, App_local.CC_PORT);
    	server.start();
    	
    }
    
}