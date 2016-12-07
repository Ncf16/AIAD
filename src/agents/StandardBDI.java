package agents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import broker.InformationBroker;
import company.Stock;
import company.Stock.StockType;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
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
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.RequiredService;
import services.IFollowService;

// Multi archetypal agent: can follow different plans along it's life: greedy, cautious, etc.

@Agent
@Arguments({ @Argument(name = "platform", clazz = IExternalAccess.class),
	@Argument(name = "name", clazz = String.class, defaultvalue = "A"),
	@Argument(name = "startingMoney", clazz = Double.class, defaultvalue = "300") })
@RequiredServices(@RequiredService(name = "followservices", type = IFollowService.class, multiple = true, binding = @Binding(scope = Binding.SCOPE_GLOBAL)))
@ProvidedServices(@ProvidedService(type = IFollowService.class))
public class StandardBDI implements IFollowService {

	@AgentArgument
	IExternalAccess platform;

	@Belief
	@AgentArgument
	private String name;
	
	@Belief
	@AgentArgument
	private Double startingMoney;

	@Belief 
	private Double currentMoney;
	
	@Belief
	private Double goalMoney; // Is this supposed to be a belief?
	
	@Belief int maxFollowed;
	
	@Belief int maxStockPriceToBuy; 
	
	@Belief
	private IComponentIdentifier identifier;

	@Belief
	private List<IComponentIdentifier> followed = new ArrayList<IComponentIdentifier>();
	
	@Belief
	private List<IComponentIdentifier> followers = new ArrayList<IComponentIdentifier>();
	
	private InformationBroker broker;

	@AgentFeature
	protected IBDIAgentFeature bdiFeature;
	
	@Agent
	protected IInternalAccess bdi;
	
	

	@AgentCreated
	public void init() {
		broker = InformationBroker.getInstance();
		Map<String, Object> arguments = bdi.getComponentFeature(IArgumentsResultsFeature.class).getArguments();
		platform = (IExternalAccess) arguments.get("platform");
		name = (String) arguments.get("name");
	}

	@AgentBody
	public void executeBody() {
		IFuture<IComponentManagementService> fut = SServiceProvider.getService(platform, IComponentManagementService.class);
		IComponentManagementService cms = fut.get();
	
		identifier = bdi.getComponentIdentifier();
		currentMoney = startingMoney;
		
		System.out.println("I am: " + identifier + " and I have " + currentMoney + "$"); 

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Service call
		IFuture<IExternalAccess> futExt = cms.getExternalAccess(identifier);
		IExternalAccess extAcc = futExt.get();
		
		bdiFeature.dispatchTopLevelGoal(currentMoney);
		
		/*
		IFuture<IFollowService> fut1 = SServiceProvider.getService(extAcc, IFollowService.class);
			    fut1.addResultListener(new IResultListener<IFollowService>() {
			     
				@Override
				public void resultAvailable(IFollowService arg0) {
					//System.out.println("I am : " + name + " and my companion's name is: " + arg0.gimmeYourStringNOW() + " and I have: " + currentMoney + "$");
					
				}

				@Override
				public void exceptionOccurred(Exception arg0) {
					// TODO Auto-generated method stub
					
				}
			    });
		*/
			
	}
	
	@Plan(trigger=@Trigger(goals=GetRich.class))
	protected void getRichPlan1(GetRich goal){
		currentMoney += 10;
		System.out.println("Carrying out plan 1, current money is: " + currentMoney) ;
		
	}
	
	@Plan(trigger=@Trigger(goals=GetRich.class))
	protected void getRichPlan2(GetRich goal){
		currentMoney += 20;
		System.out.println("Carrying out plan 2, current money is: " + currentMoney) ;
	}
	
	@Goal(recur = true, retrydelay = 1000)
	public class GetRich
	{
	  @GoalParameter
	  protected Double goalMoney;
		
	  @GoalResult
	  protected Double finalMoney;

	  public GetRich(Double goalMoney)
	  {
	    this.goalMoney = goalMoney;
	  }
	  
	  @GoalRecurCondition(beliefs="time")
		public boolean checkRecur() {
			// The buyer's job is done when all required units have been purchased
			return currentMoney > goalMoney;
		}

	}
	

	@Override
	public String gimmeYourStringNOW() {
		return name;
	}

	

	

	/*
	 * @AgentKilled public IFuture<Void> agentKilled() { return Void; }
	 */

	/**
	 * Record of Stocks bought
	 */
	@Belief
	private PriorityQueue<Purchase> stocksSold;
	/**
	 * Current Stocks the an Agent Owns
	 */
	@Belief
	private PriorityQueue<Purchase> stocksBought;
	/**
	 * Companies the Agent already trusts ( assumimos que ele ja tem algum conhecimento de antes)
	 */
	@Belief
	private Set<String> trustedCompanies = new HashSet<String>();

	@Belief
	private List<String> followedAgentsCID = new ArrayList<String>();

	public boolean sellStock(Purchase stockPurchase) {
		// TODO check if it will do as we want that it if it starts with one and
		// the command next will not skip
		// maybe put next in the end of cycle
		Iterator<Purchase> it = stocksBought.iterator();
		while (it.hasNext()) {
			Purchase p = it.next();
			if (p.equals(stockPurchase)) {
				if (p.sellStock(this)) {
					p.getSaleProfit();
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	// -------------------------------------------

	/**
	 * 
	 * @return a priority queue of stocks that have been sold by the agent ordered by Date
	 */
	public PriorityQueue<Purchase> getStocksSold() {
		return stocksSold;
	}

	/**
	 * sets the stocks sold
	 * 
	 * @param stocksSold
	 */
	public void setStocksSold(PriorityQueue<Purchase> stocksSold) {
		this.stocksSold = stocksSold;
	}

	/**
	 * 
	 * @return a priority queue of stocks that have been bought by the agent ordered by Date
	 */
	@Belief
	public PriorityQueue<Purchase> getStocksBought() {
		return stocksBought;
	}

	/**
	 * sets the stocks bought by the Agent
	 * 
	 * @param stocksBought
	 */
	@Belief
	public void setStocksBought(PriorityQueue<Purchase> stocksBought) {
		this.stocksBought = stocksBought;
	}

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
	public void test() {
		System.out.println("Own CID: " + bdi.getComponentIdentifier() + ", Own name: " + name + ", companionCID: " + "companion's Name: ");
	}
	
	// Test functions
	public void fillCompanions() {
		for (int i = 0; i < broker.agents.size(); i++) {
			IComponentIdentifier cid = broker.agents.get(i);
			if (!cid.equals(identifier)){				
				companionCIDs.add(cid);
			}
		}

	}
	
	// Test functions
	public void getSingleCompanion() {
		for (int i = 0; i < broker.agents.size(); i++) {
			IComponentIdentifier cid = broker.agents.get(i);
			if (!cid.equals(identifier)) {
				companion = cid;
				break;
			}
		}
		System.out.println("I am: " + identifier + ", Single Companion: " + companion);
	}
	
	*/

}
