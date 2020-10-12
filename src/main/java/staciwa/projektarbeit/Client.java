package staciwa.projektarbeit;

import java.util.Map;
import java.util.Scanner;

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
public class Client {
	
	//GLOBALS: a network address and port of the device on which the AAS is running.
	//public static String AAS_IP = "147.172.178.150";
	public static String AAS_IP = "192.168.2.3";
	public static int CC_PORT = 4001;
	public static int AAS_PORT = 4000;
	
	
    public static void main( String[] args ) throws Exception {
    	
    	System.out.println("\n\nWelcome to the Pasteurizator Simulation CLIENT\n\n");
    	
        //Connecting to AAS
    	IAASRegistryService registry = new AASRegistryProxy("http://" + Client.AAS_IP + ":" 
    			+ App_local.AAS_PORT + "/registry");
        IConnectorProvider connectorProvider = new HTTPConnectorProvider();
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry,
				connectorProvider);
					
		// The ID of the Pasteurizator AAS
		ModelUrn aasURN = new ModelUrn("", "", "AAS", "", "", "pasteurizator", "");
		ConnectedAssetAdministrationShell connectedAAS = manager.retrieveAAS(aasURN);
		// Retrieving the tank's submodel.
		Map<String, ISubModel> submodels = connectedAAS.getSubModels();
		ISubModel connectedTankSM = submodels.get("submodel_tank");
		System.out.print("\nSubmodel \"Tank\" with short ID \"" + connectedTankSM.getIdShort() + "\" has been retrieved.\n");
		
		//Heating the liquid
		ISubModel connectedHeaterSM = submodels.get("submodel_heater");
		String shortid = connectedHeaterSM.getIdShort();
		System.out.print("\nSubmodel \"Heater\" with short ID \"" + connectedHeaterSM.getIdShort() + "\" has been retrieved.\n");
		
		// Retrieving the properties of the submodel tank.
		Map<String, ISubmodelElement> propertiesTank = connectedTankSM.getSubmodelElements(); 
		IProperty maxCapPro = (IProperty) propertiesTank.get("maxCapacity");
		Double maxCap = (Double) maxCapPro.get();
		System.out.println("\n\tThe maximal capacity of the tank: " + maxCap);
		IProperty currentLiquidLevel = (IProperty) propertiesTank.get("currentLiquidLevel");
		Double curLiqLev = (Double) currentLiquidLevel.get();
		
		// Retrieving the properties of the Submodel heater.
		Map<String, ISubmodelElement> propertiesHeater = connectedHeaterSM.getSubmodelElements(); 
		IProperty currentTemp = (IProperty) propertiesHeater.get("currentTemp");
		Double currentLiquidTemp = (Double) currentTemp.get();
				
		System.out.println("\tCurrent liquid level: " + curLiqLev);
		System.out.println("\tCurrent temperatur of the liquid: " + currentLiquidTemp + "°C");
		
		System.out.println("\nFilling the tank with liquid and heating the liquid.");
		Map<String, IOperation> operationsTank = connectedTankSM.getOperations();
		IOperation tankOperation = operationsTank.get("fillTank");
		tankOperation.invoke();
			
		Thread.sleep(3000);
		
		curLiqLev = (Double) currentLiquidLevel.get();
		currentLiquidTemp = (Double) currentTemp.get();
		System.out.println("\tCurrent liquid level: " + curLiqLev);
		System.out.println("\tCurrent temperatur of the liquid: " + currentLiquidTemp + "°C");
						
		//cooling down for 5s
		Thread.sleep(5000);
		
		currentLiquidTemp = (Double) currentTemp.get();
		System.out.println("\tTemperatur of the liquid after 5s: " + currentLiquidTemp + "°C");
		
		//Empty tank
		System.out.println("\nEmptying the tank.");
		IOperation emptyTank = operationsTank.get("emptyTank");
		emptyTank.invoke();
		
		curLiqLev = (Double) currentLiquidLevel.get();
		System.out.println("\tCurrent liquid level: " + curLiqLev);
		
    } 
    
}