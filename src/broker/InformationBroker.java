package broker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import company.Stock;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
//import javafx.util.Pair;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;

public class InformationBroker {

	private static final int MORE_THAN_X_ELEM = 1;

	private static InformationBroker instance = null;

	private IExternalAccess platform;

	private HashMap<IComponentIdentifier, String> companyNames = new HashMap<IComponentIdentifier, String>();
	public HashMap<IComponentIdentifier, String> agentNames = new HashMap<IComponentIdentifier, String>();

	private IComponentManagementService cms;

	// Deprecated
	// public HashMap<IComponentIdentifier, Double> companyRates = new
	// HashMap<IComponentIdentifier, Double>();
	// public HashMap<IComponentIdentifier, Double> companyRates = new
	// HashMap<IComponentIdentifier,
	// Double>();

	private InformationBroker() {

	}

	public void initBrokerServiceInfo(IExternalAccess platform) {
		this.platform = platform;
		IFuture<IComponentManagementService> fut = SServiceProvider.getService(platform, IComponentManagementService.class);
		cms = fut.get();
	}

	public List<Pair<IComponentIdentifier, Double>> agentsRegistered = new ArrayList<Pair<IComponentIdentifier, Double>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean add(Pair<IComponentIdentifier, Double> mt) {
			super.add(mt);
			sortListDecreasing(agentsRegistered);
			System.out.println(agentsRegistered);
			return true;
		}

	};;

	public List<Pair<IComponentIdentifier, Double>> stockPricesGrowth = new ArrayList<Pair<IComponentIdentifier, Double>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean add(Pair<IComponentIdentifier, Double> mt) {
			super.add(mt);
			sortListDecreasing(stockPricesGrowth);
			return true;
		}
	};

	public List<Pair<IComponentIdentifier, Double>> stockPricesCoefVar = new ArrayList<Pair<IComponentIdentifier, Double>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean add(Pair<IComponentIdentifier, Double> mt) {
			super.add(mt);
			sortListDecreasing(stockPricesCoefVar);
			return true;
		}

	};

	public List<Pair<IComponentIdentifier, Double>> stockPrices = new ArrayList<Pair<IComponentIdentifier, Double>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean add(Pair<IComponentIdentifier, Double> mt) {
			super.add(mt);
			sortListDecreasing(stockPrices);
			return true;
		}
	};

	public void sortListDecreasing(List<Pair<IComponentIdentifier, Double>> list) {
		Collections.sort(list, new Comparator<Pair<IComponentIdentifier, Double>>() {
			@Override
			public int compare(Pair<IComponentIdentifier, Double> o1, Pair<IComponentIdentifier, Double> o2) {
				return 0 - o1.getValue().compareTo(o2.getValue());
			}
		});
	}

	public Boolean registerAgent(IComponentIdentifier agent, String name) {
		if (agentsRegistered.contains(agent) || agent == null) {
			return false;
		} else {
			Random random = new Random();
			Double testValue = (random.nextDouble() + 1) * 10; // Test value,
																// randomly
																// between 10
																// and 20
			Pair<IComponentIdentifier, Double> pair = new Pair<IComponentIdentifier, Double>(agent, testValue);
			agentsRegistered.add(pair);
			System.out.println("Added agent: " + agent + " with value: " + testValue + " to the Information Broker ");
			agentNames.put(pair.getKey(), name);
			return true;
		}
	}

	public synchronized Boolean updateAgentRatio(IComponentIdentifier agent, Double ratio) {

		if (!agentsRegistered.isEmpty()) {
			if (!replaceListPair(agent, agentsRegistered, ratio)) {
				agentsRegistered.add(new Pair<IComponentIdentifier, Double>(agent, ratio));
			}
		}
		sortListDecreasing(agentsRegistered);

		// System.out.println("Agent list ordered: " + agentsRegistered);

		return true;
	}

	public synchronized void addCompanyInfo(Pair<IComponentIdentifier, ArrayList<Double>> companyStock, String name) {
		fillStockPrices(companyStock.getKey(), companyStock.getValue());
		fillCoefVar(companyStock.getKey(), companyStock.getValue());
		fillGrowth(companyStock.getKey(), companyStock.getValue());

		if (!companyNames.containsKey(companyStock.getKey()))
			companyNames.put(companyStock.getKey(), name);

		// System.out.println("After Stock price: ");
		// for (Pair<IComponentIdentifier, Double> p : stockPrices)
		// System.out.println(p + " " + p.getKey().getLocalName());
		// System.out.println("");
		// System.out.println("Growth");
		// for (Pair<IComponentIdentifier, Double> p : stockPricesGrowth)
		// System.out.println(p + " " + p.getKey().getLocalName());
		// System.out.println("");
		// System.out.println("Stadr Dev: ");
		// for (Pair<IComponentIdentifier, Double> p :
		// stockPricesStandardDeviation)
		// System.out.println(p + " " + p.getKey().getLocalName());
		//
		// System.out.println("End\n");

	}

	public synchronized void fillCoefVar(IComponentIdentifier company, ArrayList<Double> list) {
		double coefVar = Statistics.instance().getVarCoef(list);

		if (list != null && !list.isEmpty()) {
			if (!replaceListPair(company, stockPricesCoefVar, coefVar))
				stockPricesCoefVar.add(new Pair<IComponentIdentifier, Double>(company, coefVar));

			sortListDecreasing(stockPricesCoefVar);
		}
	}

	public synchronized void fillGrowth(IComponentIdentifier company, ArrayList<Double> list) {

		if (list != null && !list.isEmpty()) {
			double growthRate;

			if (list.size() > MORE_THAN_X_ELEM) {
				growthRate = Math.abs(list.get(list.size() - 1) / list.get(0));
			} else
				growthRate = list.get(0);

			if (!replaceListPair(company, stockPricesGrowth, growthRate))
				stockPricesGrowth.add(new Pair<IComponentIdentifier, Double>(company, growthRate));
		}
		sortListDecreasing(stockPricesGrowth);
	}

	public synchronized void fillStockPrices(IComponentIdentifier company, ArrayList<Double> list) {
		if (list != null && !list.isEmpty()) {
			if (!replaceListPair(company, stockPrices, list.get(list.size() - 1)))
				stockPrices.add(new Pair<IComponentIdentifier, Double>(company, list.get(list.size() - 1)));
		}
		sortListDecreasing(stockPrices);
	}

	private synchronized boolean replaceListPair(IComponentIdentifier pairKey, List<Pair<IComponentIdentifier, Double>> list, Double newValue) {
		Pair<IComponentIdentifier, Double> pair;
		if ((pair = getPairLinear(pairKey, list)) != null) {
			pair.setValue(newValue);
			return true;
		}

		return false;

	}

	public synchronized Pair<IComponentIdentifier, Double> getPairLinear(IComponentIdentifier company, List<Pair<IComponentIdentifier, Double>> list) {
		for (Iterator<Pair<IComponentIdentifier, Double>> iter = list.listIterator(); iter.hasNext();) {
			Pair<IComponentIdentifier, Double> pair = iter.next();
			if (pair.getKey().equals(company)) {
				return pair;
			}
		}
		return null;
	}

	public synchronized Pair<IComponentIdentifier, Double> getPairBinary(IComponentIdentifier company, Double value, List<Pair<IComponentIdentifier, Double>> list) {

		int low = 0;
		int high = list.size() - 1;

		while (high >= low) {
			int middle = (low + high) / 2;
			Pair<IComponentIdentifier, Double> pair = list.get(middle);
			System.out.println("Middle: " + middle);
			double keyValue = pair.getValue();
			System.out.println("keyValue: " + keyValue + "  value: " + value + "  " + pair.getKey().equals(company));
			if (keyValue == value) {
				if (pair.getKey().equals(company))
					return pair;
				else
					return linearSearch(list, value, company, middle);
			}
			if (keyValue < value) {
				low = middle + 1;
			}
			if (keyValue > value) {
				high = middle - 1;
			}
		}
		return null;

	}

	public synchronized Pair<IComponentIdentifier, Double> linearSearch(List<Pair<IComponentIdentifier, Double>> list, Double value, IComponentIdentifier company, int middle) {

		for (Iterator<Pair<IComponentIdentifier, Double>> iter = list.listIterator(); iter.hasNext();) {
			Pair<IComponentIdentifier, Double> pair = iter.next();
			if (pair.getValue() > value)
				break;
			if (pair.getKey().equals(company)) {
				return pair;
			}
		}

		for (ListIterator<Pair<IComponentIdentifier, Double>> iter = list.listIterator(); iter.hasPrevious();) {
			Pair<IComponentIdentifier, Double> pair = iter.previous();
			if (pair.getValue() < value)
				return null;
			if (pair.getKey().equals(company)) {
				return pair;
			}
		}
		return null;
	}

	public static InformationBroker getInstance() {
		if (instance == null)
			instance = new InformationBroker();

		return instance;
	}

	public HashMap<IComponentIdentifier, String> getCompanyNames() {
		return companyNames;
	}

	public HashMap<IComponentIdentifier, String> getAgentNames() {
		return agentNames;
	}

}
