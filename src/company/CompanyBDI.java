package company;

import java.util.ArrayList;
import java.util.PriorityQueue;

import agents.BaseAgent;
import agents.StockHolding;
import broker.InformationBroker;
import broker.Pair;
import company.Stock.StockType;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;;

@Agent
@Arguments({ @Argument(name = "companyName", clazz = String.class, defaultvalue = "companyName"), @Argument(name = "stockPrice", clazz = Double.class, defaultvalue = "5.0"),
		@Argument(name = "stockType", clazz = StockType.class, defaultvalue = "1") })
public class CompanyBDI extends BaseAgent {

	private static final int START_OF_STOCK_PRICE_UPDATES = 0;
	private static final int TIME_BETWEEN_STOCK_PRICE_UPDATE = 1000;

	@AgentArgument
	private Stock companyStock = new Stock();

	@AgentArgument
	private String companyName;

	@Belief
	private IComponentIdentifier identifier;

	@Belief
	private InformationBroker broker = InformationBroker.getInstance();

	/** The bdi agent. */
	@AgentFeature
	protected IBDIAgentFeature bdiFeature;

	@Agent
	private IInternalAccess bdi;

	// TODO ainda é relevante?
	private PriorityQueue<StockHolding> stocksSold = new PriorityQueue<>(StockHolding.comparator);

	@AgentFeature
	IExecutionFeature execFeature;

	@AgentCreated
	public void init() {
		identifier = bdi.getComponentIdentifier();
		this.companyName = (String) internalAccess.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("companyName");
		this.companyStock = new Stock((double) internalAccess.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("stockPrice"),
				(StockType) internalAccess.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("stockType"));
	}

	@AgentBody
	public void body() {
		execFeature.repeatStep(START_OF_STOCK_PRICE_UPDATES, TIME_BETWEEN_STOCK_PRICE_UPDATE, new IComponentStep<Void>() {
			@Override
			public IFuture<Void> execute(IInternalAccess arg0) {
				companyStock.changePrice();
				System.out.println("Price is : " + companyStock.getStockPrice() + " | " + identifier);
				broker.addCompanyInfo(new Pair<IComponentIdentifier, ArrayList<Double>>(internalAccess.getComponentIdentifier(), companyStock.getOldValues()));

				
				
				return IFuture.DONE;
			}
		});
	}

	public boolean endCompany() {
		return false;
	}

	public void createStock(double price) {
		companyStock = new Stock(price);
	}


	/**
	 * Called each time the Company update it's StockPrice
	 */
	public void updateBrokerInfo() {
	}

}
