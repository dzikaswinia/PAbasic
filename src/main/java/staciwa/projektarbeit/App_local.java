package staciwa.projektarbeit;

import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServlet;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistryService;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.aas.registration.restapi.DirectoryModelProvider;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.VABMultiSubmodelProvider;
import org.eclipse.basyx.models.controlcomponent.ControlComponent;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.eclipse.basyx.submodel.metamodel.api.ISubModel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IDataElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
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
import org.eclipse.basyx.vab.protocol.api.IConnectorProvider;
import org.eclipse.basyx.vab.protocol.basyx.connector.BaSyxConnector;
import org.eclipse.basyx.vab.protocol.basyx.server.BaSyxTCPServer;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorProvider;
import org.eclipse.basyx.vab.protocol.http.server.AASHTTPServer;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;


public class App_local 
{
	public static int CC_PORT = 4001;
	public static int AAS_PORT = 4000;
	//public static String AAS_IP = "192.168.2.3";
	public static String AAS_IP = "192.168.2.3";
	public static String CC_IP = "";
	
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Hello World!" );
        Pasteurizator pasti = new Pasteurizator("podgrzewacz mleka");
        startMyControlComponent(pasti);
        startMyAAS(pasti);
        
        //Connecting to AAS
        IAASRegistryService registry = new AASRegistryProxy("http://" + App_local.AAS_IP + ":" + App_local.AAS_PORT + "/registry");
        //IAASRegistryService registry = new AASRegistryProxy("http://192.168.2.3:4000/registry");
        IConnectorProvider connectorProvider = new HTTPConnectorProvider();
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry,
				connectorProvider);
 
		// The ID of the oven AAS
		ModelUrn aasURN = new ModelUrn("de.FHG", "devices.es.iese", "AAS", "1.0", "1", "oven01", "001");
		ConnectedAssetAdministrationShell connectedAAS = manager.retrieveAAS(aasURN);
		Map<String, ISubModel> submodels = connectedAAS.getSubModels();
		ISubModel connectedTankSM = submodels.get("tank");
		
		System.out.print("Short ID: " + connectedTankSM.getIdShort());
				
		Map<String, IDataElement> properties = connectedTankSM.getDataElements();
		IProperty tankProperty = (IProperty) properties.get("maxCapacity");
		double maxCap = (double) tankProperty.get();
		System.out.println("\nMax Capacity: " + maxCap);
		
		// TODO Funktionsaufruf ohne AAS, durch ControlComponent
		/*
		VABElementProxy proxy = new VABElementProxy("", new JSONConnector(new BaSyxConnector(App_local.CC_IP, App_local.CC_PORT)));
		proxy.setModelPropertyValue("status/opMode", PasteurizatorControlComponent.OPMODE_FILL);
		proxy.invokeOperation("operations/service/start");	
		*/
		
		/*
		Map<String, IOperation> operations = connectedTankSM.getOperations();
		IOperation tankOperation = operations.get("fillTank");
		tankOperation.invoke();
		
		
		
		System.out.println("Current liguid level in tank: " + pasti.getTank().getCurrentLiquidLevel());
		System.out.println("Current liguid level in tank: " + pasti.getTank().getCurrentLiquidLevel());
		System.out.println("Current liguid level in tank: " + pasti.getTank().getCurrentLiquidLevel());
		//TODO kann nicht die Funktion mehrmal aufrufen
		tankOperation.invoke();
		*/
    }
    
    public static void startMyAAS(Pasteurizator pasti) {
    	
    	/**Submodel
    	 * 
    	 */
    	SubModel tankSubModel = new SubModel();
    	// static property
    	Property maxCapacity = new Property();
    	maxCapacity.setIdShort("maxCapacity");
    	maxCapacity.set(VABLambdaProviderHelper.createSimple(() -> {
    		return pasti.getTank().getMaxCapacity(); 
    	}, null), PropertyValueTypeDef.Double);
    	
    	// Function 
    	Function<Object[], Object> tankFillInvokable = (params) -> {
			// From: HandsOn 04
			// Connect to the control component
			VABElementProxy proxy = new VABElementProxy("", new JSONConnector(new BaSyxConnector(App_local.CC_IP, App_local.CC_PORT)));
 
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
			// Then return -> synchronous
			return null;
		};
		
		// Create the Operation
		Operation operation = new Operation();
		operation.setIdShort("fillTank");
		operation.setInvocable(tankFillInvokable);
		tankSubModel.addSubModelElement(operation);
    	
    	tankSubModel.setIdShort("tank");
    	tankSubModel.setIdentification(IdentifierType.CUSTOM, "tank");
    	tankSubModel.addSubModelElement(maxCapacity);
    	
    	
    	  	
    	
    	/**
    	 * AAS
    	 */
    	AssetAdministrationShell aas = new AssetAdministrationShell();
    	ModelUrn aasURN = new ModelUrn("de.FHG", "devices.es.iese", "AAS", "1.0", "1", "oven01", "001");
    	aas.setIdentification(aasURN);
    	
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
    
    /*
    public static Map<String, Object> createMyPasteurizatorModel(Pasteurizator pasti) {
    	
    	Map<String, Object> properties = new HashMap<>();
    	
    }
    */
    
    public static void startMyControlComponent(Pasteurizator pasti) {
    	
    	ControlComponent cc = new PasteurizatorControlComponent(pasti);
    	// Server where the control component is reachable.
    	VABMapProvider ccProvider = new VABMapProvider(cc);
    	BaSyxTCPServer<VABMapProvider> server = new BaSyxTCPServer<>(ccProvider, App_local.CC_PORT);
    	server.start();
    	
    }
    
    
}