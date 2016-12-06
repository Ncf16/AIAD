package broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jadex.bridge.IComponentIdentifier;

public class InformationBroker {

	private static InformationBroker instance = null;
	
	public HashMap<String, Double> companyRates = new HashMap<String, Double>();
	public List<IComponentIdentifier> agents = new ArrayList<IComponentIdentifier>();
	
	private InformationBroker(){
		
	}
	
	public static InformationBroker getInstance(){
		if(instance == null)
			instance = new InformationBroker();
		
		return instance;
	}
	
	
}
