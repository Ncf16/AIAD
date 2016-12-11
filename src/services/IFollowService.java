package services;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

public interface IFollowService {
	// a2 wants to follow a1, discovers a1. Calls: a1.startedBeingFollowed(a2);
	public IFuture<Boolean> startedBeingFollowed(IComponentIdentifier follower);

	public IFuture<Boolean> stoppedBeingFollowed(IComponentIdentifier follower);

	public IFuture<Boolean> stopFollowingMe(IComponentIdentifier tragicHero);

	public Double giveStockTip(IComponentIdentifier company, IComponentIdentifier sender);
	
	public Double getCurrentMoney(IComponentIdentifier agent);
	
	public Double getCurrentStocks(IComponentIdentifier agent);

}
