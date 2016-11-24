package agents;

import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

import company.Stock;
import jadex.micro.annotation.Agent;

/**
 * Jadex probably has an extend I should use but for now thread stays just to
 * give the idea that it is about independet programs
 * 
 * @author Filipe
 *
 */
@Agent
// Nome da classe para não entrar em conflito com o Agent do Jadex
public class StandardAgent {

	private String name;
	/**
	 * Record of Stocks bought
	 */
	private PriorityQueue<Purchase> stocksSold;
	/**
	 * Current Stocks the an Agent Owns
	 */
	private PriorityQueue<Purchase> stocksBought;
	/**
	 * Companies the Agent already trusts ( assumimos que ele ja tem algum
	 * conhecimento de antes)
	 */
	private Set<String> trustedCompanies = new HashSet<String>();

	public boolean sellStock(Purchase stockPurchase) {
		// TODO check if it will do as we want that it if it starts with one and
		// the command next will not skip
		// maybe put next in the end of cycle
		Iterator<Purchase> it = stocksBought.iterator();
		while (it.hasNext()) {
			Purchase p = it.next();
			if (p.equals(stockPurchase)) {
				if (p.sellStock(this)) {
					p.getSaleProfit();
					return true;
				} else
					return false;
			}
		}
		return false;
	}


	
	
	
	
	
	// -------------------------------------------

	/**
	 * 
	 * @return a priority queue of stocks that have been sold by the agent
	 *         ordered by Date
	 */
	public PriorityQueue<Purchase> getStocksSold() {
		return stocksSold;
	}

	/**
	 * sets the stocks sold
	 * 
	 * @param stocksSold
	 */
	public void setStocksSold(PriorityQueue<Purchase> stocksSold) {
		this.stocksSold = stocksSold;
	}

	/**
	 * 
	 * @return a priority queue of stocks that have been bought by the agent
	 *         ordered by Date
	 */
	public PriorityQueue<Purchase> getStocksBought() {
		return stocksBought;
	}

	/**
	 * sets the stocks bought by the Agent
	 * 
	 * @param stocksBought
	 */
	public void setStocksBought(PriorityQueue<Purchase> stocksBought) {
		this.stocksBought = stocksBought;
	}

	/**
	 * 
	 * @return The companies the agent trusts
	 */
	public Set<String> getTrustedCompanies() {
		return trustedCompanies;
	}

	/**
	 * sets the trutesd companies
	 * 
	 * @param trustedCompanies
	 */
	public void setTrustedCompanies(Set<String> trustedCompanies) {
		this.trustedCompanies = trustedCompanies;
	}

}
