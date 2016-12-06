package agents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import company.Stock;
import jadex.bdiv3.annotation.Belief;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.RequiredService;
import services.IFollowService;
 
@Agent
@RequiredServices(@RequiredService(name="followservices", type=IFollowService.class, multiple=true, binding=@Binding(scope=Binding.SCOPE_GLOBAL)))
@ProvidedServices(@ProvidedService(type=IFollowService.class))
public class StandardBDI implements IFollowService{

	@AgentArgument
	IExternalAccess platform;
	
	@Belief
	@AgentArgument
	private String name;
	
	
	private ArrayList<IComponentIdentifier> companionCIDs;
	
	 @Agent
	 protected IInternalAccess bdi;
	 
	 
	@AgentCreated
	public void init(){
		Map<String, Object> arguments = bdi.getComponentFeature(IArgumentsResultsFeature.class).getArguments();
		platform = (IExternalAccess) arguments.get("platform");
		name = (String) arguments.get("name");
	}
	 
	@AgentBody
	public void executeBody(){
		IFuture<IComponentManagementService> fut = SServiceProvider.getService(platform,IComponentManagementService.class);
		IComponentManagementService cms = fut.get();
			  
	
			  
			  
		System.out.println("Own CID: " + bdi.getComponentIdentifier() +", Own name: " + name + ", companionCID: "+ "companion's Name: ");
		
	}
	
	@Override
	public String gimmeYourStringNOW() {
		return name;
	}
	
	/*
	@AgentKilled
	public IFuture<Void> agentKilled()
	{
		return Void;
	}
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
	 * Companies the Agent already trusts ( assumimos que ele ja tem algum
	 * conhecimento de antes)
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
	 * @return a priority queue of stocks that have been sold by the agent
	 *         ordered by Date
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
	 * @return a priority queue of stocks that have been bought by the agent
	 *         ordered by Date
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

	

}
