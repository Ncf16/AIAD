package services;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

public interface IFollowService {

	public IFuture<Boolean> buyStocks();

	public IFuture<Boolean> sellStocks();

	// a2 wants to follow a1, discovers a1. Calls: a1.startedBeingFollowed(a2);
	public IFuture<Boolean> startedBeingFollowed(IComponentIdentifier follower);
	public IFuture<Boolean> stoppedBeingFollowed(IComponentIdentifier follower);
	public IFuture<Boolean> giveStockTip(IComponentIdentifier company, IComponentIdentifier sender);
	public IFuture<Boolean> sendReward(Double reward, IComponentIdentifier sender);


}
