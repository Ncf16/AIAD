package agents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import broker.InformationBroker;
import broker.Pair;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import services.IFollowService;

// Multi archetypal agent: can follow different plans along it's life: greedy, cautious, etc.

@Agent
@Arguments({ @Argument(name = "platform", clazz = IExternalAccess.class), @Argument(name = "name", clazz = String.class, defaultvalue = "A"),
		@Argument(name = "startingMoney", clazz = Double.class, defaultvalue = "300.0"), @Argument(name = "goalMoney", clazz = Double.class, defaultvalue = "2000.0"),
		@Argument(name = "maxRisk", clazz = Double.class, defaultvalue = "2.0"), @Argument(name = "lowerBoundOfSalesInterval", clazz = Double.class, defaultvalue = "0.75"),
		@Argument(name = "upperBoundOfSalesInterval", clazz = Double.class, defaultvalue = "1.25"), @Argument(name = "maxMoneySpentOnPurchase", clazz = Double.class, defaultvalue = "0.25") })
@RequiredServices(@RequiredService(name = "followservices", type = IFollowService.class, multiple = true, binding = @Binding(scope = Binding.SCOPE_GLOBAL)))
@ProvidedServices(@ProvidedService(type = IFollowService.class))
public class StandardBDI implements IFollowService {

	private static long TIME_BETWEEN_PLANS = 1000;

	@AgentArgument
	IExternalAccess platform;

	@Belief
	@AgentArgument
	private String name;

	@Belief
	@AgentArgument
	private Double startingMoney;

	@Belief
	@AgentArgument
	private Double maxRisk;

	@Belief
	@AgentArgument
	private Double maxMoneySpentOnPurchase;

	@Belief
	@AgentArgument
	protected Double upperBoundOfSalesInterval;

	@Belief
	@AgentArgument
	protected Double lowerBoundOfSalesInterval;

	@Belief
	protected Double currentMoney;

	@Belief(updaterate = 1000)
	protected long time = System.currentTimeMillis();

	@Belief
	@AgentArgument
	private Double goalMoney; // Is this supposed to be a belief?

	@Belief
	int maxFollowed;

	@Belief
	boolean goalAchieved = false;

	@Belief
	private IComponentIdentifier tipBuyCompany;

	@Belief
	private IComponentIdentifier tipSellCompany;

	@Belief
	private IComponentIdentifier identifier;

	@Belief
	private List<IComponentIdentifier> followed = new ArrayList<IComponentIdentifier>();

	@Belief
	private List<IComponentIdentifier> followers = new ArrayList<IComponentIdentifier>();

	// TODO change to Hashmap
	private ArrayList<Pair<IComponentIdentifier, Purchase>> purchases = new ArrayList<Pair<IComponentIdentifier, Purchase>>();

	private InformationBroker broker;

	@Belief
	private GetRich achieveGoal;

	@Belief
	private int counter = 0;

	@AgentFeature
	protected IBDIAgentFeature bdiFeature;

	@AgentFeature
	IExecutionFeature execFeature;

	@Agent
	protected IInternalAccess internalAccess;

	@AgentCreated
	public void init() {

		broker = InformationBroker.getInstance();
		Map<String, Object> arguments = internalAccess.getComponentFeature(IArgumentsResultsFeature.class).getArguments();

		platform = (IExternalAccess) arguments.get("platform");
		name = (String) arguments.get("name");
		maxMoneySpentOnPurchase = (Double) arguments.get("maxMoneySpentOnPurchase");
		maxRisk = (Double) arguments.get("maxRisk");
		upperBoundOfSalesInterval = (Double) arguments.get("upperBoundOfSalesInterval");
		lowerBoundOfSalesInterval = (Double) arguments.get("lowerBoundOfSalesInterval");
		identifier = internalAccess.getComponentIdentifier();
		currentMoney = startingMoney;
		achieveGoal = new GetRich(goalMoney);

	}

	@AgentBody
	public void executeBody() {
		IFuture<IComponentManagementService> fut = SServiceProvider.getService(platform, IComponentManagementService.class);
		IComponentManagementService cms = fut.get();

		// System.out.println("I am: " + identifier + " and I have " +
		// currentMoney + "$");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Service call
		IFuture<IExternalAccess> futExt = cms.getExternalAccess(identifier);
		IExternalAccess extAcc = futExt.get();

		bdiFeature.dispatchTopLevelGoal(new GetRich(goalMoney));

		/*
		 * IFuture<IFollowService> fut1 = SServiceProvider.getService(extAcc,
		 * IFollowService.class); fut1.addResultListener(new
		 * IResultListener<IFollowService>() {
		 * 
		 * @Override public void resultAvailable(IFollowService arg0) {
		 * //System.out.println("I am : " + name +
		 * " and my companion's name is: " + arg0.gimmeYourStringNOW() +
		 * " and I have: " + currentMoney + "$");
		 * 
		 * }
		 * 
		 * @Override public void exceptionOccurred(Exception arg0) { // TODO
		 * Auto-generated method stub
		 * 
		 * } });
		 */

	}

	@Goal(recur = true)
	public class GetRich {

		protected Double goalMoney;

		public GetRich(Double goalMoney) {
			this.goalMoney = goalMoney;
		}

		public Double getGoalMoney() {
			return goalMoney;
		}

		/**
		 * @return True if the goal should repeat.
		 */
		@GoalRecurCondition(beliefs = "counter")
		public boolean checkRecur() {
			System.out.println("Check recur, currentMoney: " + currentMoney + ", goalMoney: " + goalMoney + " Condition: " + (currentMoney < goalMoney));
			System.out.println();

			// Returns if goal isnt achieved/should repeat
			return !(goalAchieved = currentMoney >= goalMoney);

		}
	}

	// TODO: when agent gets killed, goes to all the agents who follow him and
	// invokes a service
	// saying "removeMe"
	// TODO: when an agent reaches a goal, kill him?

	@Plan(trigger = @Trigger(goals = GetRich.class))
	private class searchCompaniesPlan {
		@PlanAPI
		protected IPlan plan;

		searchCompaniesPlan() {

		}

		// Fornece Venda de Ações -> Agents
		// Fornece Dados Ao broker

		@PlanBody
		public void plan(GetRich goal) {

			System.out.println("Plan started.");

			if (currentMoney < 1000)
				currentMoney += 10;

			pickBestStock();
			// This block of code will trigger the check to see if the plan
			// needs to be repeated
			/***********
			 * TRIGGER A CHECK TO SEE IF GOAL WAS MET. IF NOT, RUNS ANOTHER PLAN
			 *****************/
			execFeature.waitForDelay(TIME_BETWEEN_PLANS, new IComponentStep<Void>() {

				public IFuture<Void> execute(IInternalAccess arg0) {
					System.out.println("Prompting a check");
					counter++; // this will trigger a checkRecur in
								// TIME_BETWEEN_PLANS milliseconds
					return IFuture.DONE;
				}
			});
			System.out.println("Current Money: " + currentMoney);
			throw new PlanFailureException();/************************************************************************************************/

		}

		@PlanPassed
		public void passed() {
			System.out.println("Plan finished successfully.");
		}

		@PlanAborted
		public void aborted() {
			System.out.println("Plan aborted.");
		}

		@PlanFailed
		public void failed(Exception e) {
			System.out.println("Plan failed: " + e);
		}

	}

	@Plan(trigger = @Trigger(factchangeds = "tipBuyCompany"))
	private class analyzeFollowTipsPlan {
		@PlanAPI
		protected IPlan plan;

		analyzeFollowTipsPlan() {

		}

		// Fornece Venda de Ações -> Agents
		// Fornece Dados Ao broker

		@PlanBody
		public void plan() {

			// This block of code will trigger the check to see if the
			// companySearchPlan needs to continue
			/***********
			 * TRIGGER A CHECK TO SEE IF GOAL WAS MET. IF NOT, RUNS ANOTHER PLAN
			 *****************/
			execFeature.waitForDelay(TIME_BETWEEN_PLANS, new IComponentStep<Void>() {

				public IFuture<Void> execute(IInternalAccess arg0) {
					System.out.println("Prompting a check");
					counter++; // this will trigger a checkRecur in
								// TIME_BETWEEN_PLANS milliseconds
					return IFuture.DONE;
				}
			});
			System.out.println("Current Money: " + currentMoney);
			throw new PlanFailureException();/************************************************************************************************/

		}

		@PlanPassed
		public void passed() {
			System.out.println("Plan finished successfully.");
		}

		@PlanAborted
		public void aborted() {
			System.out.println("Plan aborted.");
		}

		@PlanFailed
		public void failed(Exception e) {
			System.out.println("Plan failed: " + e);
		}

	}

	public synchronized List<Pair<IComponentIdentifier, Purchase>> createStockList() {
		System.out.println("In StockList");
		List<Pair<IComponentIdentifier, Purchase>> possiblePurchases = new ArrayList<Pair<IComponentIdentifier, Purchase>>();
		double maxSpendMoney = currentMoney * maxMoneySpentOnPurchase;
		List<Pair<IComponentIdentifier, Double>> stdrList;

		if ((stdrList = broker.stockPricesStandardDeviation) != null && !stdrList.isEmpty()) {

			for (ListIterator<Pair<IComponentIdentifier, Double>> iter = stdrList.listIterator(stdrList.size()); iter.hasPrevious();) {

				Pair<IComponentIdentifier, Double> companyStdrDev = iter.previous();

				if (companyStdrDev != null && calculateCompanyRisk(companyStdrDev.getValue()) <= maxRisk && notInPurchases(companyStdrDev.getKey())) {

					Pair<IComponentIdentifier, Double> pair = broker.getPairBinary(companyStdrDev.getKey(), companyStdrDev.getValue(), broker.stockPrices);
					possiblePurchases.add(new Pair<IComponentIdentifier, Purchase>(companyStdrDev.getKey(), new Purchase(maxSpendMoney, pair.getValue(), internalAccess.getComponentIdentifier())));
				} else
					break;
			}
			System.out.println("Size of Companies: " + purchases.size());
			System.out.println("Size of Possible Companies: " + possiblePurchases.size());
		}

		return possiblePurchases;
	}

	public boolean notInPurchases(IComponentIdentifier key) {

		for (Pair<IComponentIdentifier, Purchase> p : purchases) {
			if (p.getKey().equals(key))
				return false;
		}
		return true;
	}

	public synchronized void sellStocks() {
		if (purchases != null && !purchases.isEmpty()) {
			for (ListIterator<Pair<IComponentIdentifier, Purchase>> iter = purchases.listIterator(); iter.hasNext();) {

				Pair<IComponentIdentifier, Purchase> pair = iter.next();
				// No sense doing binary since stock probably already changed
				// value
				Pair<IComponentIdentifier, Double> companyStockPair = broker.getPairLinear(pair.getKey(), broker.stockPrices);

				Purchase p = pair.getValue();
				Double stockValue = companyStockPair.getValue();
				// TODO check if ratio formula is correct
				double ratio = stockValue / p.getStockPurchasePrice();

				if (lowerBoundOfSalesInterval >= ratio && ratio <= upperBoundOfSalesInterval) {
					sellStock(p.getNumberOfStocks(), stockValue);
				}
			}
		}
	}

	public void sellStock(int nStocks, Double stockValue) {
		currentMoney += stockValue * nStocks;
	}

	private Double calculateCompanyRisk(Double value) {

		return value;
	}

	/**
	 * Picks Best Stock To Buy ATM
	 * 
	 * @param possiblePurchases
	 */

	// Company ID Purchase Details
	public Pair<IComponentIdentifier, Purchase> pickBestStock() {
		// Need to go through all the possible purchases and pick the best, so
		// give them a score, check if higher than currentMax if not keep going
		// if so replace currentMax
		List<Pair<IComponentIdentifier, Purchase>> possiblePurchases = createStockList();

		Pair<IComponentIdentifier, Purchase> currentMaxPair = null;
		if (possiblePurchases != null && !possiblePurchases.isEmpty()) {
			double currentMaxValue = -1;
			for (ListIterator<Pair<IComponentIdentifier, Purchase>> iter = possiblePurchases.listIterator(); iter.hasNext();) {

				Pair<IComponentIdentifier, Purchase> currentPair = iter.next();
				double localMaxValue = rateCompany(currentPair.getKey(), currentPair.getValue());
				if (localMaxValue > currentMaxValue)
					currentMaxPair = currentPair;

			}
		}
		return currentMaxPair;
	}

	private double rateCompany(IComponentIdentifier key, Purchase purchase) {
		double stdr_dev = broker.getPairLinear(key, broker.stockPricesStandardDeviation).getValue();
		double growth = broker.getPairLinear(key, broker.stockPricesGrowth).getValue();

		System.out.println("RANK: " + growth / stdr_dev);
		return growth / stdr_dev;
	}

	@Override
	public String gimmeYourStringNOW() {
		return name;
	}

	/*
	 * @AgentKilled public IFuture<Void> agentKilled() { return Void; }
	 */

	/**
	 * Companies the Agent already trusts ( assumimos que ele ja tem algum
	 * conhecimento de antes)
	 */
	@Belief
	private Set<String> trustedCompanies = new HashSet<String>();

	@Belief
	private List<String> followedAgentsCID = new ArrayList<String>();

	// public boolean sellStock(Purchase stockPurchase) {
	// // TODO check if it will do as we want that it if it starts with one and
	// // the command next will not skip
	// // maybe put next in the end of cycle
	// Iterator<Purchase> it = stocksBought.iterator();
	// while (it.hasNext()) {
	// Purchase p = it.next();
	// if (p.equals(stockPurchase)) {
	// if (p.sellStock(this)) {
	// p.getSaleProfit();
	// return true;
	// } else
	// return false;
	// }
	// }
	// return false;
	// }

	// -------------------------------------------

	/**
	 * 
	 * @return The companies the agent trusts
	 */
	@Belief
	public Set<String> getTrustedCompanies() {
		return trustedCompanies;
	}

	/**
	 * sets the trusted companies
	 * 
	 * @param trustedCompanies
	 */
	@Belief
	public void setTrustedCompanies(Set<String> trustedCompanies) {
		this.trustedCompanies = trustedCompanies;
	}

	@Override
	public IFuture<Boolean> buyStocks() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * public void test() { System.out.println("Own CID: " +
	 * bdi.getComponentIdentifier() + ", Own name: " + name + ", companionCID: "
	 * + "companion's Name: "); } // Test functions public void fillCompanions()
	 * { for (int i = 0; i < broker.agents.size(); i++) { IComponentIdentifier
	 * cid = broker.agents.get(i); if (!cid.equals(identifier)){
	 * companionCIDs.add(cid); } } } // Test functions public void
	 * getSingleCompanion() { for (int i = 0; i < broker.agents.size(); i++) {
	 * IComponentIdentifier cid = broker.agents.get(i); if
	 * (!cid.equals(identifier)) { companion = cid; break; } }
	 * System.out.println("I am: " + identifier + ", Single Companion: " +
	 * companion); }
	 */

}
