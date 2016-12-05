package main;

import java.util.ArrayList;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;

public class Main {
	
	ArrayList<IComponentIdentifier> agentIDs = new ArrayList<IComponentIdentifier>();
	
	/*
	IFuture<IExternalAccess> fut = Starter.createPlatform()
	IExternalAccess platform = fut.get();
	
	PlatformConfiguration platformConfig = PlatformConfiguration.getDefault();
	RootComponentConfiguration rootConfig = platformConfig.getRootConfig();

	// Pass to Starter:
	IExternalAccess platform = Starter.createPlatform(platformConfig).get();
	
	rootConfig.setLogging(true);
	platformConfig.setDebugFutures(true);
	// rootConfig.setAwareness(false);
	 
	 
	 IFuture<IComponentManagementService> fut = SServiceProvider.getService(platform, IComponentManagementService.class);
		
		IComponentManagementService cms = fut.get();
		
		CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[]{"myName"}, new Object[]{"Harald"}));
		ITuple2Future<IComponentIdentifier, Map<String, Object>> tupleFut = cms.createComponent("myAgent1", "Main.MyAgent.class", null);
		IComponentIdentifier cid = tupleFut.getFirstResult();
		
		System.out.println("Started component: " + cid);
	 
	 
	*/
}
