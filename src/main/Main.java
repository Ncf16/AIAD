package main;

import java.util.ArrayList;
import java.util.Map;

import broker.InformationBroker;
import broker.Pair;
import company.Stock;
import company.Stock.StockType;
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

import gui.AppFrame;

public class Main {

	static InformationBroker broker = InformationBroker.getInstance();

	// Agent Creation Variables
	public static CreationInfo ci;
	public static ITuple2Future<IComponentIdentifier, Map<String, Object>> tupleFut;
	public static IComponentIdentifier cid;
	public static IComponentManagementService cms;
	public static IExternalAccess platform;

	public static void main(String args[]) {
		ArrayList<IComponentIdentifier> agentIDs = new ArrayList<IComponentIdentifier>();

		/*
		 * IFuture<IExternalAccess> fut = Starter.createPlatform()
		 * IExternalAccess platform = fut.get();
		 */

		PlatformConfiguration platformConfig = PlatformConfiguration.getDefault();
		RootComponentConfiguration rootConfig = platformConfig.getRootConfig();

		// Pass to Starter:
		platform = Starter.createPlatform(platformConfig).get();

		rootConfig.setLogging(true);
		platformConfig.setDebugFutures(true);
		rootConfig.setAwareness(false);

		IFuture<IComponentManagementService> fut = SServiceProvider.getService(platform,
				IComponentManagementService.class);

		cms = fut.get();

		// FOR BROKER TO BE ABLE TO CALL SERVICES OF AGENTS TO
		// COMMUNICATE ON HIS PART TO RESPOND
		broker.initBrokerServiceInfo(platform);

		try {
			AppFrame frame = new AppFrame();
			frame.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}