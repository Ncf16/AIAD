package agents;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Agent {

	/**
	 * Record of Stocks bought
	 */
	private PriorityQueue<Purchase> stocksSold;
	/**
	 * Current Stocks the Angent Owns
	 */
	private PriorityQueue<Purchase> stocksBought;
	/**
	 * Companies the Agent already trusts ( assumimos que ele já tem algum
	 * conhecimento de antes)
	 */

	private Set<String> trustedCompanies = new HashSet<String>();

	public PriorityQueue<Purchase> getStocksSold() {
		return stocksSold;
	}

	public void setStocksSold(PriorityQueue<Purchase> stocksSold) {
		this.stocksSold = stocksSold;
	}

	public PriorityQueue<Purchase> getStocksBought() {
		return stocksBought;
	}

	public void setStocksBought(PriorityQueue<Purchase> stocksBought) {
		this.stocksBought = stocksBought;
	}

	public Set<String> getTrustedCompanies() {
		return trustedCompanies;
	}

	public void setTrustedCompanies(Set<String> trustedCompanies) {
		this.trustedCompanies = trustedCompanies;
	}

}
