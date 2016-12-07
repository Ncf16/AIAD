package broker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import company.Stock;
import jadex.bridge.IComponentIdentifier;
//import javafx.util.Pair;

/**
 * TODO WARNING CREATED OWN CLASS PAIR CHECK
 * 
 *
 *
 */
public class InformationBroker {

	private static final int MORE_THAN_X_ELEM = 1;

	private static InformationBroker instance = null;

	public HashMap<IComponentIdentifier, Double> companyRates = new HashMap<IComponentIdentifier, Double>();

	public List<IComponentIdentifier> agents = new ArrayList<IComponentIdentifier>();

	public List<Pair<String, Integer>> companies = new ArrayList<Pair<String, Integer>>();

	public List<Pair<Double, IComponentIdentifier>> stockPricesAbsDiff = new ArrayList<Pair<Double, IComponentIdentifier>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean add(Pair<Double, IComponentIdentifier> mt) {
			super.add(mt);
			Collections.sort(stockPricesAbsDiff, new Comparator<Pair<Double, IComponentIdentifier>>() {
				@Override
				public int compare(Pair<Double, IComponentIdentifier> o1, Pair<Double, IComponentIdentifier> o2) {
					return 0 - o1.getKey().compareTo(o2.getKey());
				}
			});
			return true;
		}
	};

	public List<Pair<Double, IComponentIdentifier>> stockPricesStandardDeviation = new ArrayList<Pair<Double, IComponentIdentifier>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean add(Pair<Double, IComponentIdentifier> mt) {
			super.add(mt);
			Collections.sort(stockPricesStandardDeviation, new Comparator<Pair<Double, IComponentIdentifier>>() {
				@Override
				public int compare(Pair<Double, IComponentIdentifier> o1, Pair<Double, IComponentIdentifier> o2) {
					return 0 - o1.getKey().compareTo(o2.getKey());
				}
			});
			return true;
		}
	};

	public List<Pair<Double, IComponentIdentifier>> stockPrices = new ArrayList<Pair<Double, IComponentIdentifier>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean add(Pair<Double, IComponentIdentifier> mt) {
			super.add(mt);
			Collections.sort(stockPrices, new Comparator<Pair<Double, IComponentIdentifier>>() {
				@Override
				public int compare(Pair<Double, IComponentIdentifier> o1, Pair<Double, IComponentIdentifier> o2) {
					return 0 - o1.getKey().compareTo(o2.getKey());
				}
			});
			return true;
		}
	};

	private InformationBroker() {

	}

	public synchronized void addCompanyInfo(Pair<IComponentIdentifier, ArrayList<Double>> companyStock) {
		fillStandardDeviation(companyStock.getKey(), companyStock.getValue());
		fillAbsDifference(companyStock.getKey(), companyStock.getValue());
		fillstockPrices(companyStock.getKey(), companyStock.getValue());

		System.out.print("Stock price: ");
		for (Pair<Double, IComponentIdentifier> p : stockPrices)
			System.out.println(p + "   " + p.getValue().getLocalName());
		System.out.println("");
		System.out.print("Abs Diff: ");
		for (Pair<Double, IComponentIdentifier> p : stockPricesAbsDiff)
			System.out.println(p + "   " + p.getValue().getLocalName());
		System.out.println("");
		System.out.print("Stadr Dev: ");
		for (Pair<Double, IComponentIdentifier> p : stockPricesStandardDeviation)
			System.out.println(p + "   " + p.getValue().getLocalName());

		System.out.println("End");
	}

	public synchronized void fillStandardDeviation(IComponentIdentifier company, ArrayList<Double> list) {
		removeStockListPair(company, stockPricesStandardDeviation);

		stockPricesStandardDeviation
				.add(new Pair<Double, IComponentIdentifier>(Statistics.instance().getStdDev(list), company));
	}

	public synchronized void fillAbsDifference(IComponentIdentifier company, ArrayList<Double> list) {
		double absDiff;
		if (list.size() > MORE_THAN_X_ELEM) {
			removeStockListPair(company, stockPricesAbsDiff);
			absDiff = Math.abs(list.get(list.size() - 1) - list.get(0));
		} else
			absDiff = 0.0;

		stockPricesAbsDiff.add(new Pair<Double, IComponentIdentifier>(absDiff, company));

	}

	public synchronized void fillstockPrices(IComponentIdentifier company, ArrayList<Double> list) {
		if (!list.isEmpty()) {
			removeStockListPair(company, stockPrices);
			stockPrices.add(new Pair<Double, IComponentIdentifier>(list.get(list.size() - 1), company));
		}
	}

	private synchronized boolean removeStockListPair(IComponentIdentifier company,
			List<Pair<Double, IComponentIdentifier>> list) {

		for (Iterator<Pair<Double, IComponentIdentifier>> iter = list.listIterator(); iter.hasNext();) {
			Pair<Double, IComponentIdentifier> a = iter.next();
			if (a.getValue().equals(company)) {
				iter.remove();
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
