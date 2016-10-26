package company;

import java.util.PriorityQueue;

import agents.Purchase;

public class Company {
	private Stock companyStock;
	private String name;
	/**
	 * The record of who purchased the Stocks take the ones already invalid?
	 * (como já não pertecem à empresa??) think so, we have date of purcahse
	 */
	private PriorityQueue<Purchase> stocksSold = new PriorityQueue<>(Purchase.comparator);

	public Company() {
	}

	public Stock getCompanyStock() {
		return companyStock;
	}

	public void setCompanyStock(Stock companyStock) {
		this.companyStock = companyStock;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PriorityQueue<Purchase> getStocksSold() {
		return stocksSold;
	}

	public void setStocksSold(PriorityQueue<Purchase> stocksSold) {
		this.stocksSold = stocksSold;
	}

}
