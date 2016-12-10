package main;

import java.util.ArrayList;
import java.util.Map;

import agents.StandardBDI;
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

public class Main {

	static InformationBroker broker = InformationBroker.getInstance();

	public static void main(String args[]) {
		ArrayList<IComponentIdentifier> agentIDs = new ArrayList<IComponentIdentifier>();

		/*
		 * IFuture<IExternalAccess> fut = Starter.createPlatform()
		 * IExternalAccess platform = fut.get();
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

		// VERY IMPORTANT, FOR BROKER TO BE ABLE TO CALL SERVICES OF AGENTS TO
		// COMMUNICATE ON HIS PART TO RESPOND
		broker.initBrokerServiceInfo(platform);

		CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[] { "platform", "name", "startingMoney", "goalMoney", "debug", "maxMoneySpentOnPurchase" },
				new Object[] { platform, "A1", 50000.0, 100000.0, true, 0.25 }));
		ITuple2Future<IComponentIdentifier, Map<String, Object>> tupleFut = cms.createComponent("myStandardBDI", "agents.StandardBDI.class", ci);
		IComponentIdentifier cid = tupleFut.getFirstResult();

		// ci = new CreationInfo(SUtil.createHashMap(new String[] { "platform",
		// "name", "startingMoney", "goalMoney", "debug" }, new Object[] {
		// platform, "A2", 50000.0, 52000.0, true }));
		// tupleFut = cms.createComponent("myStandardBDI",
		// "agents.StandardBDI.class", ci);
		// cid = tupleFut.getFirstResult();

		// ci = new CreationInfo(SUtil.createHashMap(new String[] { "platform",
		// "name", "startingMoney", "goalMoney" }, new Object[] { platform,
		// "A3", 500.0, 600.0}));
		// tupleFut = cms.createComponent("myStandardBDI",
		// "agents.StandardBDI.class", ci);
		// cid = tupleFut.getFirstResult();

		ci = new CreationInfo(SUtil.createHashMap(new String[] { "companyName", "stockPrice", "stockType" }, new Object[] { "APPLE", 10000.0, StockType.NORMAL }));
		tupleFut = cms.createComponent("myCompanyBDI", "company.CompanyBDI.class", ci);
		cid = tupleFut.getFirstResult();

		// ci = new CreationInfo(SUtil.createHashMap(new String[] {
		// "companyName", "stockPrice", "stockType" }, new Object[] { "GOOGLE",
		// 40000.0, StockType.VOLATILE }));
		// tupleFut = cms.createComponent("myCompanyBDI",
		// "company.CompanyBDI.class", ci);
		// cid = tupleFut.getFirstResult();
		//
		// ci = new CreationInfo(SUtil.createHashMap(new String[] {
		// "companyName", "stockPrice", "stockType" }, new Object[] {
		// "MICROSOFT", 40000.0, StockType.VERY_VOLATILE }));
		// tupleFut = cms.createComponent("myCompanyBDI",
		// "company.CompanyBDI.class", ci);
		// cid = tupleFut.getFirstResult();

		/*
		 * ci = new CreationInfo(SUtil.createHashMap(new String[] {
		 * "companyName", "stockPrice", "stockType" }, new Object[] {"APPLE",
		 * 5.0, StockType.VERY_VOLATILE })); tupleFut =
		 * cms.createComponent("myCompanyBDI", "company.CompanyBDI.class", ci);
		 * cid = tupleFut.getFirstResult();
		 */

		/*
		 * ci = new CreationInfo(SUtil.createHashMap(new String[] {
		 * "companyName", "stockPrice", "stockType" }, new Object[] {"GOOGLE",
		 * 6.0, StockType.VOLATILE })); tupleFut =
		 * cms.createComponent("myCompanyBDI", "company.CompanyBDI.class", ci);
		 * cid = tupleFut.getFirstResult();
		 * 
		 * ci = new CreationInfo(SUtil.createHashMap(new String[] {
		 * "companyName", "stockPrice", "stockType" }, new Object[] {
		 * "MICROSOFT", 7.0, StockType.VERY_VOLATILE })); tupleFut =
		 * cms.createComponent("myCompanyBDI", "company.CompanyBDI.class", ci);
		 * cid = tupleFut.getFirstResult();
		 */

		// StandardBDI bdi = new StandardBDI();
		//
		// ArrayList<Pair<Integer, Integer>> a = new ArrayList<Pair<Integer,
		// Integer>>();
		// a.add(new Pair<Integer, Integer>(1, 2));
		// a.add(new Pair<Integer, Integer>(2, 2));
		// a.add(new Pair<Integer, Integer>(3, 2));
		// a.add(new Pair<Integer, Integer>(5, 2));
		// a.add(new Pair<Integer, Integer>(7, 2));
		//
		// if (!bdi.notInList(1, a))
		// System.out.println("Worked");
		//
		// if (bdi.notInList(9, a))
		// System.out.println("Worked");

	}
}