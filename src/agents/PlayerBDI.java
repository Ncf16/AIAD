package agents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import broker.InformationBroker;
import broker.Pair;
import gui.AppFrame;
import gui.AppPanel;
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
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import services.IFollowService;

@Agent
@Arguments({ @Argument(name = "platform", clazz = IExternalAccess.class),
		@Argument(name = "name", clazz = String.class, defaultvalue = "\"A\""),
		@Argument(name = "startingMoney", clazz = Double.class, defaultvalue = "300.0"),
		@Argument(name = "goalMoney", clazz = Double.class, defaultvalue = "2000.0"),
		@Argument(name = "maxRisk", clazz = Double.class, defaultvalue = "0.3"),
		@Argument(name = "lowerBoundOfSalesInterval", clazz = Double.class, defaultvalue = "0.75"),
		@Argument(name = "upperBoundOfSalesInterval", clazz = Double.class, defaultvalue = "1.25"),
		@Argument(name = "maxMoneySpentOnPurchase", clazz = Double.class, defaultvalue = "0.25"),
		@Argument(name = "debug", clazz = Boolean.class, defaultvalue = "true"),
		@Argument(name = "minAgentPerformance", clazz = Double.class, defaultvalue = "0.40") })
@RequiredServices(@RequiredService(name = "followservices", type = IFollowService.class, multiple = true, binding = @Binding(scope = Binding.SCOPE_GLOBAL)))
@ProvidedServices(@ProvidedService(type = IFollowService.class))
public class PlayerBDI implements IFollowService {

	private static final double REJECT_TIP_RETURN_VALUE = -1.0;

	private static final double REWARD_PER_TIP_PERCENTAGE = 0.01;

	private static final double TRUST_LEVERAGE = 0.3;

	private static long TIME_BETWEEN_PLANS = 1000;

	private static long START_TASK_AFTER = 1000;
	private static long REPEAT_TASK_AFTER = 1000;

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
	private Double goalMoney;

	@Belief

	int maxFollowed = 3;

	@Belief
	boolean goalAchieved = false;

	@Belief
	private IComponentIdentifier identifier;

	@Belief
	public List<IComponentIdentifier> followed = new ArrayList<IComponentIdentifier>();

	@Belief
	public List<IComponentIdentifier> followers = new ArrayList<IComponentIdentifier>();

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

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@AgentCreated
	public void init() {

		broker = InformationBroker.getInstance();
		Map<String, Object> arguments = internalAccess.getComponentFeature(IArgumentsResultsFeature.class)
				.getArguments();
		IFuture<IComponentManagementService> fut = SServiceProvider.getService(platform,
				IComponentManagementService.class);
		cms = fut.get();
		identifier = internalAccess.getComponentIdentifier();

		System.out.println((String) arguments.get("name"));
		platform = (IExternalAccess) arguments.get("platform");
		name = (String) arguments.get("name");
		maxMoneySpentOnPurchase = (Double) arguments.get("maxMoneySpentOnPurchase");
		maxRisk = (Double) arguments.get("maxRisk");
		upperBoundOfSalesInterval = (Double) arguments.get("upperBoundOfSalesInterval");
		lowerBoundOfSalesInterval = (Double) arguments.get("lowerBoundOfSalesInterval");

		currentMoney = startingMoney;
		currentStockMoney = 0.0;
		broker.registerAgent(identifier, name, currentMoney);

		if (startingMoney >= goalMoney) {
			System.out.println("END");
			goalAchieved = true;
			agentReachedGoal();
		}
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {

				if (!goalAchieved) {
					updateStockMoney();
					Double successRatio = (currentMoney + currentStockMoney) / startingMoney;

					System.out.println(identifier + " | Current Money: " + currentMoney + ", Current Stock Money: "
							+ currentStockMoney + " | New success ratio: " + successRatio);
					broker.updateAgentRatio(identifier, successRatio);

				}
			}
		}, START_TASK_AFTER, REPEAT_TASK_AFTER, TimeUnit.MILLISECONDS);

	}

	@AgentBody
	public void executeBody() {

		System.out.println("Debug activated = " + debug);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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
			System.out.println("Check recur, currentMoney: " + currentMoney + ", goalMoney: " + goalMoney
					+ " Condition: " + (currentMoney < goalMoney));
			System.out.println();

			// Returns whether the goal is achieved or not
			if (isAgentBroke() || hasAgentReachedGoal()) {
				agentReachedGoal();
				return false; // Don't repeat goal
			}
			return true;

		}

		public boolean hasAgentReachedGoal() {
			return (goalAchieved = currentMoney >= goalMoney);
		}

		public boolean isAgentBroke() {
			return currentMoney == 0 && currentStockMoney == 0;
		}

	}

	// when agent gets killed, goes to all the agents who follow him and
	// invokes a service
	// saying "removeMe"

	@Plan(trigger = @Trigger(goals = GetRich.class))
	private class InvestPlan {
		@PlanAPI
		protected IPlan plan;

		InvestPlan() {

		}

		@PlanBody
		public void plan(GetRich goal) {

			System.out.println("Plan started.");

			// If goal will be met if all stock money is recovered, then sell
			// all stocks

			// This block of code will trigger the check to see if the plan
			// needs to be repeated

			if ((currentMoney + currentStockMoney) >= goalMoney) {
				System.out.println("Can END with: " + (currentMoney + currentStockMoney) + "Needed: " + goalMoney);
				sellAllStocks();

			} else {
				updateAgentsToFollow();
				sellStocks();
				buyStock(pickBestStock(createStockList()));
			}
			broker.updateAgentInfo(identifier, currentMoney, currentStockMoney);
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

			// System.out.println("Current Money: " + currentMoney);
			throw new PlanFailureException();

			/************************************************************************************************/

		}

		private void updateAgentsToFollow() {
			// Let's choose the best 3 agents to follow
			List<Pair<IComponentIdentifier, Double>> agentsRegistered = broker.agentsRegistered;

			System.out.println("I am: " + identifier + " and my current followers are: " + followers);
			System.out.println("I am: " + identifier + " and I am following: " + followed);

			// Discard Agents that have been unsuccessful
			discardUnsuccessful(agentsRegistered);

			// Can still follow some more
			followSuccessful();

		}

		public void discardUnsuccessful(List<Pair<IComponentIdentifier, Double>> agentsRegistered) {

			System.out
					.println("I am " + identifier + ": Analyzing agents to discard. Currently following: " + followed);

			for (int i = 0; i < followed.size(); i++) {
				// System.out.println("hi");
				IComponentIdentifier followedAgent = followed.get(i);

				Double agentPerformance = broker.getPairLinear(followedAgent, agentsRegistered).getValue();

				if (agentPerformance < minAgentPerformance) {

					System.out.println(
							"Stopped following: " + followedAgent + ", its performance was: " + agentPerformance);
					String iden1 = broker.getAgentInfo().get(identifier).getName();
					String iden2 = broker.getAgentInfo().get(followedAgent).getName();
					String stopedFollowing = iden1 + " stoped following " + iden2 + "[Performance : " + agentPerformance
							+ "]";
					AppPanel.logModel.addElement(stopedFollowing);

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

		public void followSuccessful() {
			if (followed.size() < maxFollowed) {

				int canStillFollow = maxFollowed - followed.size();

				System.out.println(identifier + " Can still follow: " + canStillFollow);

				// Get the best ranked, until it is following the max number he
				// can (maxFollowed)
				for (int i = 0; i < broker.agentsRegistered.size() && i < canStillFollow; i++) {
					System.out.println("i: " + i + ", Agents registered: " + broker.agentsRegistered.size()
							+ ", can still follow " + canStillFollow);
					Pair<IComponentIdentifier, Double> agentToAnalyze = broker.agentsRegistered.get(i);

					Boolean minPerform = agentToAnalyze.getValue() >= minAgentPerformance;
					Boolean notAlreadyFollowed = !followed.contains(agentToAnalyze.getKey());
					Boolean notMyself = !agentToAnalyze.getKey().equals(identifier);

					System.out
							.println(identifier + " | Going to analyze if I should follow: " + agentToAnalyze.getKey());
					System.out.println(identifier + " He has minPerform: " + minPerform + "| He is not followed yet: "
							+ notAlreadyFollowed + "| He is not myself: " + notMyself);

					// Will start following the Top Performing agents (they are
					// already sorted), with the following conditions:
					// (1) agent has at least minimum performance, (2) isn't
					// already following (3) isn't himself

					if (agentToAnalyze.getValue() >= minAgentPerformance && !followed.contains(agentToAnalyze.getKey())
							&& !agentToAnalyze.getKey().equals(identifier)) {

						String iden1 = broker.getAgentInfo().get(identifier).getName();
						String iden2 = broker.getAgentInfo().get(agentToAnalyze.getKey()).getName();

						String startedFollowing = iden1 + " started following " + iden2;
						AppPanel.logModel.addElement(startedFollowing);

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

		private void sellAllStocks() {
			// System.out.println("FIRE SALE SELL EVERYTHING MARKET CRASH: " +
			// identifier + " " + (currentMoney + currentStockMoney));
			if (purchases != null && !purchases.isEmpty()) {
				for (ListIterator<Pair<IComponentIdentifier, StockHolding>> iter = purchases.listIterator(); iter
						.hasNext();) {

					Pair<IComponentIdentifier, StockHolding> pair = iter.next();
					Pair<IComponentIdentifier, Double> companyStockPair = broker.getPairLinear(pair.getKey(),
							broker.stockPrices);

					StockHolding p = pair.getValue();
					Double stockValue = companyStockPair.getValue();
					currentMoney += stockValue * p.getNumberOfStocks();
					iter.remove();

				}
				updateStockMoney();
				broker.updateAgentInfo(identifier, currentMoney, currentStockMoney);
				System.out.println("Sold All Stock: " + identifier + "  " + purchases.size() + "   " + currentMoney
						+ "   " + currentStockMoney + "   " + goalMoney);
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

	public synchronized List<Pair<IComponentIdentifier, StockHolding>> createStockList() {

		List<Pair<IComponentIdentifier, StockHolding>> possiblePurchases = new ArrayList<Pair<IComponentIdentifier, StockHolding>>();
		double maxSpendMoney = currentMoney * maxMoneySpentOnPurchase;
		List<Pair<IComponentIdentifier, Double>> coefvarList;

		if ((coefvarList = broker.stockPricesCoefVar) != null && !coefvarList.isEmpty()) {
			for (ListIterator<Pair<IComponentIdentifier, Double>> iter = coefvarList
					.listIterator(coefvarList.size()); iter.hasPrevious();) {
				Pair<IComponentIdentifier, Double> companyCoefVar = iter.previous();

				if (companyCoefVar != null && notInList(companyCoefVar.getKey(), purchases)) {

					if (calculateCompanyRisk(companyCoefVar.getValue()) > maxRisk) {
						break;
					}

					Pair<IComponentIdentifier, Double> pair = broker.getPairLinear(companyCoefVar.getKey(),
							broker.stockPrices);
					System.out.println(pair.getKey().toString() + " sss " + pair.getKey().getLocalName());
					StockHolding holding = new StockHolding(maxSpendMoney, pair.getValue(),
							internalAccess.getComponentIdentifier(), broker.getCompanyNames().get(pair.getKey()));

					System.out.println("WE CAN BUY X STOCK: " + holding.getNumberOfStocks() + "  MaxSpendMoney: "
							+ maxSpendMoney + "   Stock Price: " + pair.getValue());
					if (holding.getNumberOfStocks() > 0)
						possiblePurchases
								.add(new Pair<IComponentIdentifier, StockHolding>(companyCoefVar.getKey(), holding));
				}
			}
		}
		// System.out.println("Has stock of X Companies: " + purchases.size());
		// System.out.println("Can buy from Y Companies: " +
		// possiblePurchases.size());
		return possiblePurchases;
	}

	public <K, V> boolean notInList(K key, List<Pair<K, V>> list) {

		for (Pair<K, V> p : list) {
			if (p.getKey().equals(key))
				return false;
		}
		return true;
	}

	public void buyStock(Pair<IComponentIdentifier, StockHolding> bestStock) {
		// BUY STOCk
		if (bestStock != null) {
			// "buys stock"
			System.out.println("BUYING STOCK");
			bestStock.getValue().setDateOfPurchase(System.currentTimeMillis());
			currentMoney -= bestStock.getValue().getStockPurchasePrice() * bestStock.getValue().getNumberOfStocks();
			purchases.add(bestStock);
			updateStockMoney();
			sendTipToFollowers(bestStock.getKey());

			double val = bestStock.getValue().getStockPurchasePrice() * bestStock.getValue().getNumberOfStocks();
			AgentInfo agent = broker.getAgentInfo().get(identifier);
			String company = broker.getCompanyNames().get(bestStock.getKey());

			String boughtStock = agent.getName() + " bought " + bestStock.getValue().getNumberOfStocks() + " " + company
					+ "'s stocks [" + String.format("%.2g%n", val) + "�]";
			System.out.println(boughtStock);

			AppPanel.logModel.addElement(boughtStock);
		}
	}

	private void sendTipToFollowers(IComponentIdentifier company) {
		for (IComponentIdentifier follower : followers) {

			IFuture<IExternalAccess> futExt = cms.getExternalAccess(follower);
			IExternalAccess extAcc = futExt.get();

			IFuture<IFollowService> futService = SServiceProvider.getService(extAcc, IFollowService.class);
			futService.addResultListener(new IResultListener<IFollowService>() {

				@Override
				public void resultAvailable(IFollowService toSend) {
					double reward;
					if ((reward = toSend.giveStockTip(company, identifier)) > 0) {
						currentMoney += reward;
						System.out.println("Tip from : " + identifier + " was accpeted");
						AppPanel.logModel.addElement(
								broker.getAgentInfo().get(identifier).getName() + " was rewarded for tip in " + reward);
					} else
						System.out.println("Tip Rejected");
				}

				@Override
				public void exceptionOccurred(Exception arg0) {

				}
			});
		}
	}

	public synchronized IFuture<Boolean> sellStocks() {
		if (purchases != null && !purchases.isEmpty()) {
			for (ListIterator<Pair<IComponentIdentifier, StockHolding>> iter = purchases.listIterator(); iter
					.hasNext();) {

				Pair<IComponentIdentifier, StockHolding> pair = iter.next();
				Pair<IComponentIdentifier, Double> companyStockPair = broker.getPairLinear(pair.getKey(),
						broker.stockPrices);

				StockHolding p = pair.getValue();
				Double stockValue = companyStockPair.getValue();
				double ratio = stockValue / p.getStockPurchasePrice();

				if (lowerBoundOfSalesInterval >= ratio && ratio <= upperBoundOfSalesInterval) {
					// sells the stock
					currentMoney += stockValue * p.getNumberOfStocks();
					iter.remove();

					double val = stockValue * p.getNumberOfStocks();
					AgentInfo agent = broker.getAgentInfo().get(identifier);
					String company = broker.getCompanyNames().get(pair.getKey());

					String soldStock = agent.getName() + " sold " + p.getNumberOfStocks() + " " + company
							+ "'s stocks [" + String.format("%.2g%n", val) + "�]";
					System.out.print(soldStock);
					AppPanel.logModel.addElement(soldStock);

				}
			}
			updateStockMoney();
			return IFuture.TRUE;
		} else
			return IFuture.FALSE;

	}

	private synchronized void updateStockMoney() {

		Double money = 0.0;
		List<Pair<IComponentIdentifier, Double>> currentStockPrices = broker.stockPrices;

		for (ListIterator<Pair<IComponentIdentifier, StockHolding>> iter = purchases.listIterator(); iter.hasNext();) {
			Pair<IComponentIdentifier, StockHolding> agentPair = iter.next();
			IComponentIdentifier companyToUpdate = agentPair.getKey();

			for (ListIterator<Pair<IComponentIdentifier, Double>> iter2 = currentStockPrices.listIterator(); iter2
					.hasNext();) {
				Pair<IComponentIdentifier, Double> companyPair = iter2.next();
				if (companyToUpdate.equals(companyPair.getKey())) {
					StockHolding stockHolding = agentPair.getValue();
					stockHolding.setCurrentStockPrice(companyPair.getValue());
					money += stockHolding.getCurrentStockPrice() * stockHolding.getNumberOfStocks();
				}
			}
		}
		currentStockMoney = money;
		// System.out.println("Purchases: " + purchases);

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
	public Pair<IComponentIdentifier, StockHolding> pickBestStock(
			List<Pair<IComponentIdentifier, StockHolding>> possiblePurchases) {
		// System.out.println("Pick Best Stock");
		// Need to go through all the possible purchases and pick the best, so
		// give them a score, check if higher than currentMax if not keep going
		// if so replace currentMax
		Pair<IComponentIdentifier, StockHolding> currentMaxPair = null;

		if (possiblePurchases != null && !possiblePurchases.isEmpty()) {
			double currentMaxValue = -1;
			for (ListIterator<Pair<IComponentIdentifier, StockHolding>> iter = possiblePurchases.listIterator(); iter
					.hasNext();) {

				Pair<IComponentIdentifier, StockHolding> currentPair = iter.next();
				double localMaxValue = rateCompany(currentPair.getKey(), currentPair.getValue());
				if (localMaxValue > currentMaxValue)
					currentMaxPair = currentPair;

			}
		}
		// System.out.println("Max Picked: " + currentMaxPair);

		return currentMaxPair;
	}

	public double rateCompany(IComponentIdentifier key, StockHolding purchase) {
		double stdr_dev = broker.getPairLinear(key, broker.stockPricesCoefVar).getValue();
		double growth = broker.getPairLinear(key, broker.stockPricesGrowth).getValue();

		// System.out.println("RANK: " + growth / stdr_dev);
		return growth / stdr_dev;
	}

	@AgentKilled
	public IFuture<Void> agentKilled() {

		agentReachedGoal();

		return null;
	}

	public void agentReachedGoal() {

		broker.updateAgentRatio(identifier, 0.0);
		AppPanel.logModel.addElement("Agent: " + broker.getAgentInfo().get(identifier).getName()
				+ " has reached the end, it has: " + currentMoney + "$ and it needed " + goalMoney + "$");

		for (IComponentIdentifier hero : followed) {

			IFuture<IExternalAccess> futExt = cms.getExternalAccess(hero);
			IExternalAccess extAcc = futExt.get();

			IFuture<IFollowService> futService = SServiceProvider.getService(extAcc, IFollowService.class);
			futService.addResultListener(new IResultListener<IFollowService>() {

				@Override
				public void resultAvailable(IFollowService heroToSend) {
					heroToSend.stoppedBeingFollowed(identifier);
				}

				@Override
				public void exceptionOccurred(Exception arg0) {

				}
			});

		}

		/**
		 * Notifies the followers to stop following the Agent
		 */
		for (IComponentIdentifier follower : followers) {

			IFuture<IExternalAccess> futExt = cms.getExternalAccess(follower);
			IExternalAccess extAcc = futExt.get();

			IFuture<IFollowService> futService = SServiceProvider.getService(extAcc, IFollowService.class);
			futService.addResultListener(new IResultListener<IFollowService>() {

				@Override
				public void resultAvailable(IFollowService heroToSend) {
					heroToSend.stopFollowingMe(identifier);
				}

				@Override
				public void exceptionOccurred(Exception arg0) {

				}
			});
		}
	}

	@Belief
	private List<String> followedAgentsCID = new ArrayList<String>();

	@Override
	public IFuture<Boolean> startedBeingFollowed(IComponentIdentifier follower) {

		if (followers.contains(follower)) {
			System.out.println(
					identifier + " not successful added " + follower + " to his followers, already was there.");
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
			AppPanel.logModel.addElement(broker.getAgentInfo().get(identifier).getName()
					+ " successfuly stopped being followed by: " + broker.getAgentInfo().get(follower).getName());
			followers.remove(follower);
			return new Future<Boolean>(true);
		} else {
			System.out.println(
					identifier + " not successful removed " + follower + " from his followers, already wasn't there.");
			return new Future<Boolean>(false);
		}
	}

	@Override
	public Double giveStockTip(IComponentIdentifier company, IComponentIdentifier sender) {
		// Receive tip

		System.out.println("Receiving Tip: " + " FROM: " + sender + " I AM : " + identifier);
		if (notInList(company, purchases)) {
			// Decide
			Pair<IComponentIdentifier, Double> coefPair = broker.getPairLinear(company, broker.stockPricesCoefVar);
			if (calculateCompanyRisk(coefPair.getValue()) > maxRisk + TRUST_LEVERAGE) {
				// Rejected Tip
				return REJECT_TIP_RETURN_VALUE;
			}

			Pair<IComponentIdentifier, Double> pair = broker.getPairLinear(company, broker.stockPrices);

			Pair<IComponentIdentifier, StockHolding> purchase = new Pair<IComponentIdentifier, StockHolding>(company,
					new StockHolding(currentMoney * maxMoneySpentOnPurchase, pair.getValue(), identifier,
							broker.getCompanyNames().get(pair.getKey())));

			buyStock(purchase);

			double reward;
			currentMoney -= (reward = currentMoney * REWARD_PER_TIP_PERCENTAGE);

			broker.updateAgentInfo(identifier, currentMoney, currentStockMoney);
			System.out.println("Reward for Tip is : " + reward);
			String acceptedTip = broker.getAgentInfo().get(identifier).getName() + " accepted Tip from "
					+ broker.getAgentInfo().get(sender).getName() + " for company: "
					+ broker.getCompanyNames().get(company) + "]";
			AppPanel.logModel.addElement(acceptedTip);

			return reward;
		}

		// Rejected Tip
		return REJECT_TIP_RETURN_VALUE;
	}

	@Override
	public IFuture<Boolean> stopFollowingMe(IComponentIdentifier tragicHero) {
		// TODO
		if (followed.contains(tragicHero)) {
			System.out.println(tragicHero + " successfuly stopped being followed by: " + identifier);
			AppPanel.logModel.addElement(broker.getAgentInfo().get(tragicHero).getName()
					+ " successfuly stopped being followed by: " + broker.getAgentInfo().get(identifier).getName());

			followed.remove(tragicHero);
			return new Future<Boolean>(true);
		} else {
			System.out.println(tragicHero + " not successful removed " + identifier
					+ " from his followers, already wasn't there.");
			return new Future<Boolean>(false);
		}
	}

	public Double returnCurrentMoney() {
		return currentMoney;
	}
}
