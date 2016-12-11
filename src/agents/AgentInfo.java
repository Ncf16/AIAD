package agents;

import java.util.ArrayList;

import broker.Pair;
import jadex.bridge.IComponentIdentifier;

public class AgentInfo {
	private String name;
	private double currentMoney;
	private double startMoney;
	private ArrayList<Pair<IComponentIdentifier, StockHolding>> purchases = new ArrayList<Pair<IComponentIdentifier, StockHolding>>();

	public AgentInfo(String name, double currentMoney) {
		this.name = name;
		this.currentMoney = currentMoney;
		this.startMoney = currentMoney;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCurrentMoney() {
		return currentMoney;
	}

	public void setCurrentMoney(double currentMoney) {
		this.currentMoney = currentMoney;
	}

	public double getStartMoney() {
		return startMoney;
	}

	public void setStartMoney(double startMoney) {
		this.startMoney = startMoney;
	}

}
