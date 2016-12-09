package broker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	
	protected IExternalAccess platform;
	
	protected IComponentManagementService cms;

	// Deprecated
	// public HashMap<IComponentIdentifier, Double> companyRates = new HashMap<IComponentIdentifier,
	// Double>();

	private InformationBroker() {
		
	}
	
	public void initBrokerServiceInfo(IExternalAccess platform){
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
			Collections.sort(agentsRegistered, new Comparator<Pair<IComponentIdentifier, Double>>() {
				@Override
				public int compare(Pair<IComponentIdentifier, Double> o1, Pair<IComponentIdentifier, Double> o2) {
					return 0 - o1.getValue().compareTo(o2.getValue());
				}
			});
			System.out.println(agentsRegistered);
			return true;
		}

	};;

	public List<Pair<IComponentIdentifier, Double>> stockPricesAbsDiff = new ArrayList<Pair<IComponentIdentifier, Double>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean add(Pair<IComponentIdentifier, Double> mt) {
			super.add(mt);
			Collections.sort(stockPricesAbsDiff, new Comparator<Pair<IComponentIdentifier, Double>>() {
				@Override
				public int compare(Pair<IComponentIdentifier, Double> o1, Pair<IComponentIdentifier, Double> o2) {
					return 0 - o1.getValue().compareTo(o2.getValue());
				}
			});
			return true;
		}
	};

	public List<Pair<IComponentIdentifier, Double>> stockPricesStandardDeviation = new ArrayList<Pair<IComponentIdentifier, Double>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean add(Pair<IComponentIdentifier, Double> mt) {
			super.add(mt);
			Collections.sort(stockPricesAbsDiff, new Comparator<Pair<IComponentIdentifier, Double>>() {
				@Override
				public int compare(Pair<IComponentIdentifier, Double> o1, Pair<IComponentIdentifier, Double> o2) {
					return 0 - o1.getValue().compareTo(o2.getValue());
				}
			});
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
			Collections.sort(stockPricesAbsDiff, new Comparator<Pair<IComponentIdentifier, Double>>() {
				@Override
				public int compare(Pair<IComponentIdentifier, Double> o1, Pair<IComponentIdentifier, Double> o2) {
					return 0 - o1.getValue().compareTo(o2.getValue());
				}
			});
			return true;
		}
	};

	

	public Pair<IComponentIdentifier, Double> getPair(IComponentIdentifier identifier, List<Pair<IComponentIdentifier, Double>> list) {
		for (Iterator<Pair<IComponentIdentifier, Double>> iter = list.listIterator(); iter.hasNext();) {
			Pair<IComponentIdentifier, Double> pair = iter.next();
			if (pair.getKey().equals(identifier)) {
				return pair;
			}
		}
		return null;
	}

	public Boolean registerAgent(IComponentIdentifier agent) {
		if (agentsRegistered.contains(agent) || agent == null) {
			return false;
		} else {
			Random random = new Random();
			Double testValue = (random.nextDouble() + 1) * 10; // Test value, randomly between 10
																// and 20
			Pair<IComponentIdentifier, Double> pair = new Pair(agent, testValue);
			agentsRegistered.add(pair);
			System.out.println("Added agent: " + agent + " with value: " + testValue + " to the Information Broker ");
			return true;
		}
	}

	public synchronized void addCompanyInfo(Pair<IComponentIdentifier, ArrayList<Double>> companyStock) {
		fillStandardDeviation(companyStock.getKey(), companyStock.getValue());
		fillAbsDifference(companyStock.getKey(), companyStock.getValue());
		fillstockPrices(companyStock.getKey(), companyStock.getValue());

		/*
		 * System.out.print("Stock price: "); for (Pair<IComponentIdentifier, Double> p :
		 * stockPrices) System.out.println(p + " " + p.getKey().getLocalName());
		 * System.out.println(""); System.out.print("Abs Diff: "); for (Pair<IComponentIdentifier,
		 * Double> p : stockPricesAbsDiff) System.out.println(p + " " + p.getKey().getLocalName());
		 * System.out.println(""); System.out.print("Stadr Dev: "); for (Pair<IComponentIdentifier,
		 * Double> p : stockPricesStandardDeviation) System.out.println(p + " " +
		 * p.getKey().getLocalName());
		 * 
		 * System.out.println("End");
		 */
	}

	public synchronized void fillStandardDeviation(IComponentIdentifier company, ArrayList<Double> list) {
		double stdrDev = Statistics.instance().getStdDev(list);

		if (!replaceStockListPair(company, stockPricesStandardDeviation, stdrDev))
			stockPricesStandardDeviation.add(new Pair<IComponentIdentifier, Double>(company, stdrDev));
	}

	public synchronized void fillAbsDifference(IComponentIdentifier company, ArrayList<Double> list) {
		double absDiff;
		if (list.size() > MORE_THAN_X_ELEM) {
			absDiff = Math.abs(list.get(list.size() - 1) - list.get(0));
		} else
			absDiff = 0.0;

		if (!replaceStockListPair(company, stockPricesAbsDiff, absDiff))
			stockPricesAbsDiff.add(new Pair<IComponentIdentifier, Double>(company, absDiff));

	}

	public synchronized void fillstockPrices(IComponentIdentifier company, ArrayList<Double> list) {
		if (!list.isEmpty()) {
			if (!replaceStockListPair(company, stockPrices, list.get(list.size() - 1)))
				stockPrices.add(new Pair<IComponentIdentifier, Double>(company, list.get(list.size() - 1)));
		}
	}

	private synchronized boolean replaceStockListPair(IComponentIdentifier company, List<Pair<IComponentIdentifier, Double>> list, Double newValue) {

		for (Iterator<Pair<IComponentIdentifier, Double>> iter = list.listIterator(); iter.hasNext();) {
			Pair<IComponentIdentifier, Double> a = iter.next();
			if (a.getKey().equals(company)) {
				a.setValue(newValue);
				return true;
			}
		}
		return false;

	}

	public static InformationBroker getInstance() {
		if (instance == null)
			instance = new InformationBroker();

		return instance;
	}

}
