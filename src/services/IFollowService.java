package services;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

public interface IFollowService {

	 public IFuture<Boolean> buyStocks();
	 public IFuture<Boolean> sellStocks();
	 public String gimmeYourStringNOW();
	 
	//a2 wants to follow a1, discovers a1. Calls: a1.startedBeingFollowed(a2);
	 public IFuture<Boolean> startedBeingFollowed(IComponentIdentifier follower);      
	 public IFuture<Boolean> stoppedBeingFollowed(IComponentIdentifier follower);
	
}
