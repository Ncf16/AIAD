package main;

import java.util.ArrayList;
import java.util.Map;

import broker.InformationBroker;
import company.Stock;
import jadex.base.PlatformConfiguration;
import jadex.base.RootComponentConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;

public class Main {

	static InformationBroker broker = InformationBroker.getInstance();

	public static void main(String args[]) {
		ArrayList<IComponentIdentifier> agentIDs = new ArrayList<IComponentIdentifier>();

		/*
		 * IFuture<IExternalAccess> fut = Starter.createPlatform() IExternalAccess platform =
		 * fut.get();
		 */
		PlatformConfiguration platformConfig = PlatformConfiguration.getDefault();
		RootComponentConfiguration rootConfig = platformConfig.getRootConfig();
		// Pass to Starter:
		IExternalAccess platform = Starter.createPlatform(platformConfig).get();

		rootConfig.setLogging(true);
		platformConfig.setDebugFutures(true);
		rootConfig.setAwareness(false);

		IFuture<IComponentManagementService> fut = SServiceProvider.getService(platform, IComponentManagementService.class);

		IComponentManagementService cms = fut.get();

		CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[] { "platform", "name" }, new Object[] { platform, "A1" }));
		ITuple2Future<IComponentIdentifier, Map<String, Object>> tupleFut = cms.createComponent("myStandardBDI", "agents.StandardBDI.class", ci);
		IComponentIdentifier cid = tupleFut.getFirstResult();
		broker.agents.add(cid);

		ci = new CreationInfo(SUtil.createHashMap(new String[] { "platform", "name" }, new Object[] { platform, "A2" }));
		tupleFut = cms.createComponent("myStandardBDI", "agents.StandardBDI.class", ci);
		cid = tupleFut.getFirstResult();
		broker.agents.add(cid);
		

	}
}