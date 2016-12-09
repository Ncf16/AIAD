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
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
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
		@Argument(name = "upperBoundOfSalesInterval", clazz = Double.class, defaultvalue = "1.25"), @Argument(name = "maxMoneySpentOnPurchase", clazz = Double.class, defaultvalue = "0.25"),
		@Argument(name = "debug", clazz = Boolean.class, defaultvalue = "true"), @Argument(name = "maxRisk", clazz = Double.class, defaultvalue = "2.0"),
		@Argument(name = "maxMoneySpentOnPurchase", clazz = Double.class, defaultvalue = "0.25"), @Argument(name = "minAgentPerformance", clazz = Double.class, defaultvalue = "15.0") })
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
	// TODO é uma Belief certo?
	@Belief
	@AgentArgument
	private Double minAgentPerformance;

	@Belief
	@AgentArgument
	private Boolean debug;

	@Belief
	protected Double currentMoney;

	@Belief
	protected Double currentStockMoney;

	@Belief(updaterate = 1000)
	protected long time = System.currentTimeMillis();

	@Belief
	@AgentArgument
	private Double goalMoney; // Is this supposed to be a belief?

	@Belief
	/**
	 * CurrentMoney+MoneyIfAllStocksWereSold
	 */
	private Double possibleMoney;

	int maxFollowed = 3; // TODO: change value

	@Belief
	boolean goalAchieved = false;

	@Belief
	private IComponentIdentifier tipBuyCompany;

	@Belief
	private Pair<IComponentIdentifier, Boolean> tipReceived;

	@Belief
	private IComponentIdentifier tipSellCompany;

	@Belief
	private IComponentIdentifier identifier;

	@Belief
	public List<IComponentIdentifier> followed = new ArrayList<IComponentIdentifier>();

	@Belief
	public List<IComponentIdentifier> followers = new ArrayList<IComponentIdentifier>();

	// TODO change to Hashmap?
	private ArrayList<Pair<IComponentIdentifier, StockHolding>> purchases = new ArrayList<Pair<IComponentIdentifier, StockHolding>>();

	private InformationBroker broker;

	@Belief
	private int counter = 0;

	@AgentFeature
	protected IBDIAgentFeature bdiFeature;

	@AgentFeature
	IExecutionFeature execFeature;

	@Agent
	protected IInternalAccess internalAccess;

	protected IComponentManagementService cms;

	@AgentCreated
	public void init() {

		broker = InformationBroker.getInstance();
		Map<String, Object> arguments = internalAccess.getComponentFeature(IArgumentsResultsFeature.class).getArguments();
		IFuture<IComponentManagementService> fut = SServiceProvider.getService(platform, IComponentManagementService.class);
		cms = fut.get();

		// TODO o bdi já não existe?
		// Map<String, Object> arguments =
		// bdi.getComponentFeature(IArgumentsResultsFeature.class).getArguments();

		platform = (IExternalAccess) arguments.get("platform");
		name = (String) arguments.get("name");
		maxMoneySpentOnPurchase = (Double) arguments.get("maxMoneySpentOnPurchase");
		maxRisk = (Double) arguments.get("maxRisk");
		upperBoundOfSalesInterval = (Double) arguments.get("upperBoundOfSalesInterval");
		lowerBoundOfSalesInterval = (Double) arguments.get("lowerBoundOfSalesInterval");
		identifier = internalAccess.getComponentIdentifier();
		currentMoney = startingMoney;
		currentStockMoney = 0.0;

	}

	@AgentBody
	public void executeBody() {
		IFuture<IComponentManagementService> fut = SServiceProvider.getService(platform, IComponentManagementService.class);
		IComponentManagementService cms = fut.get();
		System.out.println("Debug activated = " + debug);

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

		if ((currentMoney < goalMoney) || currentMoney == 0) {
			// No sense in
			// starting a
			// goal you had
			// already met
			// or that you
			// will never
			// accomplish
			bdiFeature.dispatchTopLevelGoal(new GetRich(goalMoney));
		}
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
			if (currentMoney == 0 && currentStockMoney == 0)
				return false; // Don't repeat goal

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

			// Money varies, stuff happens here, yada yada
			if (currentMoney < 1000)
				currentMoney += 10;

			// buyStock(pickBestStock());
			// This block of code will trigger the check to see if the plan
			// needs to be repeated
			if (debug)
				updateAgentsToFollow();

			/***********
			 * TRIGGER A CHECK TO SEE IF GOAL WAS MET. IF NOT, RUNS ANOTHER PLAN
			 *****************/
			execFeature.waitForDelay(TIME_BETWEEN_PLANS, new IComponentStep<Void>() {

				public IFuture<Void> execute(IInternalAccess arg0) {
					counter++; // this will trigger a checkRecur in
								// TIME_BETWEEN_PLANS milliseconds
					return IFuture.DONE;
				}
			});

			System.out.println("Current Money: " + currentMoney);
			throw new PlanFailureException();/************************************************************************************************/

		}

		public void buyStock(Pair<IComponentIdentifier, StockHolding> bestStock) {
			// TODO BUY STOCk
			if (bestStock != null) {
				bestStock.getValue().setDateOfPurchase(System.currentTimeMillis());
				purchases.add(bestStock);
			}
		}

		private void updateAgentsToFollow() {
			// Let's choose the best 3 agents to follow
			List<Pair<IComponentIdentifier, Double>> agentsRegistered = broker.agentsRegistered;

			// Add every single one as followed just to test

			// if (followed.size() == 0) {
			// for (int i = 0; i < agentsRegistered.size(); i++) {
			// IComponentIdentifier agentToAdd =
			// agentsRegistered.get(i).getKey();
			// if (!agentsRegistered.get(i).getKey().equals(identifier)) {
			// followed.add(agentToAdd);
			//
			// IFuture<IExternalAccess> futExt =
			// cms.getExternalAccess(agentToAdd);
			// IExternalAccess extAcc = futExt.get();
			//
			// IFuture<IFollowService> futService =
			// SServiceProvider.getService(extAcc,
			// IFollowService.class);
			// futService.addResultListener(new
			// IResultListener<IFollowService>() {
			//
			// @Override
			// public void resultAvailable(IFollowService followedService) {
			// followedService.startedBeingFollowed(identifier);
			//
			// }
			//
			// @Override
			// public void exceptionOccurred(Exception arg0) {
			//
			// }
			// });
			// }
			//
			// }
			// }

			System.out.println("I am: " + identifier + " and my current followers are: " + followers);
			System.out.println("I am: " + identifier + " and I am following: " + followed);

			// Discard Agents that have been unsuccessful
			// discardUnsuccessful(agentsRegistered);

			// Can still follow some more
			if (followed.size() < maxFollowed) {

				int canStillFollow = maxFollowed - followed.size();

				// Get the best ranked, until it is following the max number he
				// can (maxFollowed)
				for (int i = 0; i < broker.agentsRegistered.size() && i < canStillFollow; i++) {
					Pair<IComponentIdentifier, Double> agentToAnalyze = broker.agentsRegistered.get(i);

					// Will start following the Top Performing agents (they are
					// already sorted), with the following conditions:
					// (1) agent has at least minimum performance, (2) isn't
					// already following (3) isn't himself
					if (agentToAnalyze.getValue() >= minAgentPerformance && !followed.contains(agentToAnalyze.getKey()) && !agentToAnalyze.getKey().equals(identifier)) {

						followed.add(agentToAnalyze.getKey());

						IFuture<IExternalAccess> futExt = cms.getExternalAccess(agentToAnalyze.getKey());
						IExternalAccess extAcc = futExt.get();

						IFuture<IFollowService> futService = SServiceProvider.getService(extAcc, IFollowService.class);
						futService.addResultListener(new IResultListener<IFollowService>() {

							@Override
							public void resultAvailable(IFollowService followedService) {
								followedService.startedBeingFollowed(identifier);

							}

							@Override
							public void exceptionOccurred(Exception arg0) {

							}
						});

					}
				}
			}

		}

		public void discardUnsuccessful(List<Pair<IComponentIdentifier, Double>> agentsRegistered) {

			System.out.println("Analyzing agents to discard. Currently following: " + followed);

			for (int i = 0; i < followed.size(); i++) {
				System.out.println("hi");
				IComponentIdentifier followedAgent = followed.get(i);

				Double agentPerformance = broker.getPairLinear(followedAgent, agentsRegistered).getValue();

				System.out.println("Agent performance is: " + agentPerformance + " and min performance is: " + minAgentPerformance);
				if (agentPerformance < minAgentPerformance) {

					System.out.println("Stopped following: " + followedAgent + ", its performance was: " + agentPerformance);

					/**************************************************************
					 * COMMUNICATE THAT WE STOPPED FOLLOWING THROUGH THE SERVICE
					 * *
					 **************************************************************/

					IFuture<IExternalAccess> futExt = cms.getExternalAccess(followedAgent);
					IExternalAccess extAcc = futExt.get();

					IFuture<IFollowService> futService = SServiceProvider.getService(extAcc, IFollowService.class);
					futService.addResultListener(new IResultListener<IFollowService>() {

						@Override
						public void resultAvailable(IFollowService followedService) {
							followedService.stoppedBeingFollowed(identifier);

						}

						@Override
						public void exceptionOccurred(Exception arg0) {

						}
					});

					followed.remove(followed.get(i));

				}

			}

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

	public synchronized List<Pair<IComponentIdentifier, StockHolding>> createStockList() {

		List<Pair<IComponentIdentifier, StockHolding>> possiblePurchases = new ArrayList<Pair<IComponentIdentifier, StockHolding>>();
		double maxSpendMoney = currentMoney * maxMoneySpentOnPurchase;
		List<Pair<IComponentIdentifier, Double>> stdrList;

		if ((stdrList = broker.stockPricesStandardDeviation) != null && !stdrList.isEmpty()) {
			for (ListIterator<Pair<IComponentIdentifier, Double>> iter = stdrList.listIterator(stdrList.size()); iter.hasPrevious();) {
				Pair<IComponentIdentifier, Double> companyStdrDev = iter.previous();

				if (companyStdrDev != null && calculateCompanyRisk(companyStdrDev.getValue()) <= maxRisk && notInPurchases(companyStdrDev.getKey())) {

					Pair<IComponentIdentifier, Double> pair = broker.getPairLinear(companyStdrDev.getKey(), broker.stockPrices);
					possiblePurchases
							.add(new Pair<IComponentIdentifier, StockHolding>(companyStdrDev.getKey(), new StockHolding(maxSpendMoney, pair.getValue(), internalAccess.getComponentIdentifier())));
				} else
					break;
				System.out.println("Size of Companies: " + purchases.size());
				System.out.println("Size of Possible Companies: " + possiblePurchases.size());
			}
		}
		return possiblePurchases;
	}

	public boolean notInPurchases(IComponentIdentifier key) {

		for (Pair<IComponentIdentifier, StockHolding> p : purchases) {
			if (p.getKey().equals(key))
				return false;
		}
		return true;
	}

	public synchronized IFuture<Boolean> sellStocks() {
		if (purchases != null && !purchases.isEmpty()) {
			for (ListIterator<Pair<IComponentIdentifier, StockHolding>> iter = purchases.listIterator(); iter.hasNext();) {

				Pair<IComponentIdentifier, StockHolding> pair = iter.next();
				// No sense doing binary since stock probably already changed
				// value
				Pair<IComponentIdentifier, Double> companyStockPair = broker.getPairLinear(pair.getKey(), broker.stockPrices);

				StockHolding p = pair.getValue();
				Double stockValue = companyStockPair.getValue();
				// TODO check if ratio formula is correct
				double ratio = stockValue / p.getStockPurchasePrice();

				if (lowerBoundOfSalesInterval >= ratio && ratio <= upperBoundOfSalesInterval) {
					// TODO sells the stock
					currentMoney += stockValue * p.getNumberOfStocks();
					iter.remove();
				}
			}
			return IFuture.TRUE;
		} else
			return IFuture.FALSE;
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
	public Pair<IComponentIdentifier, StockHolding> pickBestStock() {
		// System.out.println("Pick Best Stock");
		// Need to go through all the possible purchases and pick the best, so
		// give them a score, check if higher than currentMax if not keep going
		// if so replace currentMax
		List<Pair<IComponentIdentifier, StockHolding>> possiblePurchases = createStockList();
		Pair<IComponentIdentifier, StockHolding> currentMaxPair = null;

		if (possiblePurchases != null && !possiblePurchases.isEmpty()) {
			double currentMaxValue = -1;
			for (ListIterator<Pair<IComponentIdentifier, StockHolding>> iter = possiblePurchases.listIterator(); iter.hasNext();) {

				Pair<IComponentIdentifier, StockHolding> currentPair = iter.next();
				double localMaxValue = rateCompany(currentPair.getKey(), currentPair.getValue());
				if (localMaxValue > currentMaxValue)
					currentMaxPair = currentPair;

			}
		}
		System.out.println("Max Picked: " + currentMaxPair);

		return currentMaxPair;
	}

	public double rateCompany(IComponentIdentifier key, StockHolding purchase) {
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

	/**
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

	@Override
	public IFuture<Boolean> startedBeingFollowed(IComponentIdentifier follower) {

		if (followers.contains(follower)) {
			System.out.println(identifier + " not successful added " + follower + " to his followers, already was there.");
			return new Future<Boolean>(false);
		} else {
			System.out.println(identifier + " successfuly started being followed by: " + follower);
			followers.add(follower);
			return new Future<Boolean>(true);
		}
	}

	@Override
	public IFuture<Boolean> stoppedBeingFollowed(IComponentIdentifier follower) {
		if (followers.contains(follower)) {
			System.out.println(identifier + " successfuly stopped being followed by: " + follower);
			followers.remove(follower);
			return new Future<Boolean>(true);
		} else {
			System.out.println(identifier + " not successful removed " + follower + " from his followers, already wasn't there.");
			return new Future<Boolean>(false);
		}
	}

	/*
	 * void test() { System.out.println("Own CID: " +
	 * bdi.getComponentIdentifier() + ", Own name: " + name + ", companionCID: "
	 * + "companion's Name: "); } // Test functions public void fillCompanions()
	 * { for (int i = 0; i < broker.agents.size(); i++) { IComponentIdentifier
	 * cid = broker.agents.get(i); if (!cid.equals(identifier)){
	 * companionCIDs.add(cid); } } } // Test functions public void
	 * getSingleCompanion() { for (int i = 0; i < broker.agents.size(); i++) {
	 * IComponentIdentifier cid = broker.agents.get(i); if
	 * (!cid.equals(identifier)) { companion = cid; break; } }
	 * System.out.println("I am: " + identifier + ", Single Companion: " +
	 * companion); } ======= public void test() { System.out.println("Own CID: "
	 * + bdi.getComponentIdentifier() + ", Own name: " + name +
	 * ", companionCID: " + "companion's Name: "); }
	 * 
	 * // Test functions public void fillCompanions() { for (int i = 0; i <
	 * broker.agents.size(); i++) { IComponentIdentifier cid =
	 * broker.agents.get(i); if (!cid.equals(identifier)){
	 * companionCIDs.add(cid); } }
	 * 
	 * }
	 * 
	 * // Test functions public void getSingleCompanion() { for (int i = 0; i <
	 * broker.agents.size(); i++) { IComponentIdentifier cid =
	 * broker.agents.get(i); if (!cid.equals(identifier)) { companion = cid;
	 * break; } } System.out.println("I am: " + identifier +
	 * ", Single Companion: " + companion); }
	 * 
	 */

}
