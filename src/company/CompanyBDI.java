package company;

import java.util.PriorityQueue;

import agents.BaseAgent;
import agents.Purchase;
import company.Stock.StockType;
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
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;;

@Agent
@Arguments({ @Argument(name = "companyName", clazz = String.class, defaultvalue = "companyName"),
		@Argument(name = "stockPrice", clazz = Double.class, defaultvalue = "5.0"),
		@Argument(name = "stockType", clazz = StockType.class, defaultvalue = "1") })
@ProvidedServices({ @ProvidedService(type = CompanyServices.class) })
public class CompanyBDI extends BaseAgent implements CompanyServices {

	@AgentArgument
	private Stock companyStock = new Stock();

	@AgentArgument
	private String companyName;

	/** The bdi agent. */
	@AgentFeature
	protected IBDIAgentFeature bdiFeature;

	private PriorityQueue<Purchase> stocksSold = new PriorityQueue<>(Purchase.comparator);

	@AgentFeature
	IExecutionFeature execFeature;

	@AgentCreated
	public void init() {
		this.bdiFeature.adoptPlan(new CompanyPlan());
		this.companyName = (String) internalAccess.getComponentFeature(IArgumentsResultsFeature.class).getArguments()
				.get("companyName");
		this.companyStock = new Stock(
				(double) internalAccess.getComponentFeature(IArgumentsResultsFeature.class).getArguments()
						.get("stockPrice"),
				(StockType) internalAccess.getComponentFeature(IArgumentsResultsFeature.class).getArguments()
						.get("stockType"));
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

	@Override
	public IFuture<Void> updateStockValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Plan
	private class CompanyPlan {
		@PlanAPI
		protected IPlan plan;

		CompanyPlan() {

		}

		// Fornece Venda de Ações -> Agents
		// Fornece Dados Ao broker
		@PlanBody
		public void plan() {
			System.out.println("Plan started.");
			plan.waitFor(10000).get();
			System.out.println("Plan resumed.");

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

	@Override
	public IFuture<Purchase> sellStock(IComponentIdentifier buyer, int numberOfStocksToBuy) {
		Purchase c = new Purchase();
		c.setCompany(internalAccess.getComponentIdentifier());
		c.setBuyer(buyer);
		c.setNumberOfStocks(numberOfStocksToBuy);
		c.setDateOfPurchase(System.currentTimeMillis());
		c.setSold(false);
		c.setStockBought(companyStock);
		return null;
	}

	@Override
	public IFuture<Void> updateBrokerInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
