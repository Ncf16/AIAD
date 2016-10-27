package company;

import java.util.PriorityQueue;

import agents.Purchase;

public class Company extends Thread {
	private Stock companyStock;
	private String companyName;
	private int numberOfStocks;
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

	public int getNumberOfStocks() {
		return numberOfStocks;
	}

	public void setNumberOfStocks(int numberOfStocks) {
		this.numberOfStocks = numberOfStocks;
	}
}
