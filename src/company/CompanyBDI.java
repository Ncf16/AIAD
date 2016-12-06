package company;

import java.util.PriorityQueue;

import agents.Purchase;
import company.Stock.StockType;
import jadex.bdiv3.features.IBDIAgentFeature;
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
public class CompanyBDI {

	@AgentArgument
	private Stock companyStock = new Stock();

	@AgentArgument
	private String companyName;

	/** The bdi agent. */
	@Agent
	protected IInternalAccess bdi;

	private PriorityQueue<Purchase> stocksSold = new PriorityQueue<>(Purchase.comparator);

	@AgentFeature
	IExecutionFeature execFeature;

	@AgentCreated
	public void init() {
		this.companyName = (String) bdi.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("companyName");
		this.companyStock = new Stock((double) bdi.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("stockPrice"),
				(StockType) bdi.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("stockType"));
	}

	@AgentBody
	public void body() {

		execFeature.repeatStep(0, 1000, new IComponentStep<Void>() {
			@Override
			public IFuture<Void> execute(IInternalAccess arg0) {
				companyStock.changePrice();
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
}
