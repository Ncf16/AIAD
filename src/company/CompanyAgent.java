package company;

import java.util.PriorityQueue;

import agents.Purchase;
import jadex.micro.annotation.Agent;

@Agent
public class CompanyAgent {
	private Stock companyStock;
	private String companyName;

	private PriorityQueue<Purchase> stocksSold = new PriorityQueue<>(Purchase.comparator);

	public CompanyAgent(String name) {
		this.companyName = name;
	}

	public Stock getCompanyStock() {
		return companyStock;
	}

	public void setCompanyStock(Stock companyStock) {
		this.companyStock = companyStock;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String name) {
		this.companyName = name;
	}

	public PriorityQueue<Purchase> getStocksSold() {
		return stocksSold;
	}

	public void setStocksSold(PriorityQueue<Purchase> stocksSold) {
		this.stocksSold = stocksSold;
	}

}
