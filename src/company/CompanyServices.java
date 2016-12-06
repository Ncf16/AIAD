package company;

import agents.Purchase;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

public interface CompanyServices {

	public IFuture<Void> updateStockValue();

	public IFuture<Purchase> sellStock(IComponentIdentifier buyer, int numberOfStocksToBuy);

	public IFuture<Void> updateBrokerInfo();

}
