package staciwa.projektarbeit;

import java.util.Map;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.registration.api.IAASRegistryService;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.submodel.metamodel.api.ISubModel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.vab.protocol.api.IConnectorProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorProvider;

/**This class connects to the AAS.
 * 
 * @author monika
 * **/
public class App_remote 
{
	
	//GLOBALS: a network address and port of the device on which the AAS is running.
	public static String AAS_IP = "192.168.2.107";
	public static int CC_PORT = 4001;
	public static int AAS_PORT = 4000;
	
	
    public static void main( String[] args ) throws Exception
    {
     
        //Connecting to AAS
    	IAASRegistryService registry = new AASRegistryProxy("http://" + App_remote.AAS_IP + ":" 
    			+ App_local.AAS_PORT + "/registry");
        IConnectorProvider connectorProvider = new HTTPConnectorProvider();
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry,
				connectorProvider);
					
		// The ID of the Pasteurizator AAS
		ModelUrn aasURN = new ModelUrn("de.FHG", "devices.es.iese", "AAS", "1.0", "1", "oven01", "001");	//TODO change name
		ConnectedAssetAdministrationShell connectedAAS = manager.retrieveAAS(aasURN);
		// Retrieving the tank's submodel.
		Map<String, ISubModel> submodels = connectedAAS.getSubModels();
		ISubModel connectedTankSM = submodels.get("tank");
		System.out.print("Short ID: " + connectedTankSM.getIdShort());
		// Retrieving the properties of the submodel.
		Map<String, ISubmodelElement> properties = connectedTankSM.getSubmodelElements(); 
		IProperty maxCapPro = (IProperty) properties.get("maxCapacity");
		Double maxCap = (Double) maxCapPro.get();
		System.out.print("\nCapacity: " + maxCap);
		IProperty currentLiquidLevel = (IProperty) properties.get("currentLiquidLevel");
		Double curLiqLev = (Double) currentLiquidLevel.get();
		System.out.println("\nCurrent liquid level: " + curLiqLev);
		// Filling the tank.
		Map<String, IOperation> operations = connectedTankSM.getOperations();
		IOperation tankOperation = operations.get("fillTank");
		tankOperation.invoke();
		
		System.out.println("\nCurrent liquid level: " + curLiqLev);
		
    }
    
}