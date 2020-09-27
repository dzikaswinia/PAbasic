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
	//GLOBALS: a network address and port of the device on which the AAS is running.
	public static String AAS_IP = "192.168.2.107";		//TODO von User abfragen?
	public static int CC_PORT = 4001;
	public static int AAS_PORT = 4000;
	public static String CC_IP = "";
	
    public static void main( String[] args ) throws Exception
    {
        
    	System.out.println("Welcome to Pasteurizator's AAS");
    	/*
    	//AAS IP - user input.
    	Scanner scanner = new Scanner(System.in);
    	System.out.println("AAS IP: 192.168.2.");
    	String aas_ip = "192.168.2." + scanner.nextLine();	//TODO falsche input abfangen
    	//System.out.println("User Input: " + aas_ip);
    	 * 
    	 */
        Pasteurizator pasti = new Pasteurizator("Milcherhitzter");
        startMyControlComponent(pasti);
        startMyAAS(pasti);
        
    }
    
    /**This method starts the Asset Administration Shell.
     * 
     * @param pasti: the Pasteurizator
     */
    public static void startMyAAS(Pasteurizator pasti) {
    	
    	/**Submodel: Tank
    	 * 
    	 */
    	SubModel tankSubModel = new SubModel();
    	
    	// static property: maximal capacity of the tank.
    	Property maxCapacity = new Property();
    	maxCapacity.setIdShort("maxCapacity");
    	maxCapacity.set(VABLambdaProviderHelper.createSimple(() -> {
    		return pasti.getTank().getMaxCapacity(); 
    	}, null), PropertyValueTypeDef.Double);
    	
    	//dynamic property: current liquid level in the tank. 
    	Property currentLiquidLevel = new Property();
    	currentLiquidLevel.setIdShort("currentLiquidLevel");
    	    	
    	currentLiquidLevel.set(VABLambdaProviderHelper.createSimple(() -> {
    		return pasti.getTank().getCurrentLiquidLevel(); 
    	}, null), PropertyValueTypeDef.Double);
    	   	
    	
    	tankSubModel.addSubModelElement(maxCapacity);
    	tankSubModel.addSubModelElement(currentLiquidLevel);
    	
    	// Function 
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
		
		// Creating the Operation
		Operation operation = new Operation();
		operation.setIdShort("fillTank");
		operation.setInvocable(tankFillInvokable);
		tankSubModel.addSubModelElement(operation);
    	
		// Setting identifiers. 
    	tankSubModel.setIdShort("tank");
    	tankSubModel.setIdentification(IdentifierType.CUSTOM, "tank");
    	
    	/**
    	 * AAS
    	 */
    	AssetAdministrationShell aas = new AssetAdministrationShell();
    	ModelUrn aasURN = new ModelUrn("de.FHG", "devices.es.iese", "AAS", "1.0", "1", "oven01", "001"); //TODO new name for this device
    	aas.setIdentification(aasURN);
    	
    	// TODO delete?
    	//IIdentifier aasID = new Identifier(IdentifierType.CUSTOM, "pasti");
    	//aas.setIdentification(aasID);
    	aas.setIdShort("pasti");
    	
    	/**
    	 * Deployment
    	 */
    	
    	//Wrapping submodel in IModelProvider
    	AASModelProvider aasProvider = new AASModelProvider(aas);
    	SubModelProvider tankSMProvider = new SubModelProvider(tankSubModel);
    	VABMultiSubmodelProvider fullProvider = new VABMultiSubmodelProvider ();
    	fullProvider.setAssetAdministrationShell(aasProvider);
    	fullProvider.addSubmodel("tank",tankSMProvider);
    	
    	HttpServlet aasServlet = new VABHTTPInterface<IModelProvider>(fullProvider);
    	
    	// TODO InMemoryRegistry local
    	IAASRegistryService registry = new InMemoryRegistry();
    	// TODO von mir
    	//AASRegistryProxy registry = new AASRegistryProxy("http://192.168.2.3:4001");		//es started nicht 
		IModelProvider registryProvider = new DirectoryModelProvider(registry);
		HttpServlet registryServlet = new VABHTTPInterface<IModelProvider>(registryProvider);
		
		// Register the VAB model at the directory (locally in this case)
		AASDescriptor aasDescriptor = new AASDescriptor(aas, "http://" + App_local.AAS_IP + ":" 
				+ App_local.AAS_PORT + "/pasti/aas");
		aasDescriptor.addSubmodelDescriptor(new SubmodelDescriptor(tankSubModel, 
				"http://" + App_local.AAS_IP + ":" + App_local.AAS_PORT + "/pasti/aas/submodels/tank/submodel"));
		registry.register(aasDescriptor);
		
		// Deploy the AAS on a HTTP server
		BaSyxContext context = new BaSyxContext("", "", App_local.AAS_IP, App_local.AAS_PORT);
		context.addServletMapping("/pasti/*", aasServlet);
		context.addServletMapping("/registry/*", registryServlet);
		AASHTTPServer httpServer = new AASHTTPServer(context);
		
		httpServer.start();
		
		/*
		//closing the server
		try {
			// Wait for 5s and then shutdown the server
			Thread.sleep(10000);
			httpServer.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
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