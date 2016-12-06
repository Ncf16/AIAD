package broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jadex.bridge.IComponentIdentifier;
import javafx.util.Pair;

public class InformationBroker {

	private static InformationBroker instance = null;
	
	public HashMap<IComponentIdentifier, Double> companyRates = new HashMap<IComponentIdentifier, Double>();
	public List<IComponentIdentifier> agents = new ArrayList<IComponentIdentifier>();
	
	public List<Pair<String, Integer>> companies = new ArrayList<Pair<String,Integer>>();
	
	
	private InformationBroker(){
		
	}
	
	public static InformationBroker getInstance(){
		if(instance == null)
			instance = new InformationBroker();
		
		return instance;
	}
	
	
}
