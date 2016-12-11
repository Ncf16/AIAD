package agents;

import java.util.Comparator;
import jadex.bridge.IComponentIdentifier;

public class StockHolding extends CloneObject {
	private IComponentIdentifier buyer;
	private double stockPurchasePrice;
	private double currentStockPrice;
	private int numberOfStocks;
	private long dateOfPurchase;
	private String name;

	/**
	 * Pretty much if a purchase is still valid, we need this to keep track of
	 * everything happening (The question is, are agents will be independent
	 * threads so, how to keep track of everything, an Agent notifies the
	 * company it made the sale?
	 */

	public StockHolding(double maxSpendingMoney, double stockPrice, IComponentIdentifier buyer, String name) {
		this.buyer = buyer;
		this.stockPurchasePrice = stockPrice;
		this.currentStockPrice = stockPrice;
		Double temp = (maxSpendingMoney / stockPrice);
		this.numberOfStocks = temp.intValue();
		this.name = name;

		// System.out.println("Purcahse INFO: " + this.numberOfStocks + " " +
		// temp + " Price: " + stockPrice + " Max Spending Money: " +
		// maxSpendingMoney);

	}

	public StockHolding() {
	}

	@Override
	public String toString() {
		return "StockHolding [buyer=" + buyer + ", stockPurchasePrice=" + stockPurchasePrice + ", currentStockPrice=" + currentStockPrice + ", numberOfStocks=" + numberOfStocks + ", dateOfPurchase="
				+ dateOfPurchase + "]";
	}

	/**
	 * Comparator Between purchases
	 */
	public static Comparator<StockHolding> comparator = new Comparator<StockHolding>() {
		@Override
		public int compare(StockHolding c1, StockHolding c2) {
			if (c1 == null || c2 == null || !(c1 instanceof StockHolding) || !(c2 instanceof StockHolding))
				return -1;
			return Long.compare(c1.getDateOfPurchase(), c2.getDateOfPurchase());
		}
	};

	/**
	 * 
	 * @return the buyer of the purchase
	 */
	public IComponentIdentifier getBuyer() {
		return buyer;
	}

	/**
	 * Changes the buyer who made the purchase
	 * 
	 */
	public void setBuyer(IComponentIdentifier buyer) {
		this.buyer = buyer;
	}

	/**
	 * 
	 * @return the number of stocks sold
	 */
	public int getNumberOfStocks() {
		return numberOfStocks;
	}

	/**
	 * 
	 * changes the number of stocks sold
	 */
	public void setNumberOfStocks(int numberOfStocks) {
		this.numberOfStocks = numberOfStocks;
	}

	/**
	 * 
	 * @return the date of purchase
	 */
	public long getDateOfPurchase() {
		return dateOfPurchase;
	}

	/**
	 * 
	 * changes the date of the purchase
	 */
	public void setDateOfPurchase(long dateOfPurchase) {
		this.dateOfPurchase = dateOfPurchase;
	}

	public double getStockPurchasePrice() {
		return stockPurchasePrice;
	}

	public void setStockPurchasePrice(double stockPurchasePrice) {
		this.stockPurchasePrice = stockPurchasePrice;
	}

	public static Comparator<StockHolding> getComparator() {
		return comparator;
	}

	public static void setComparator(Comparator<StockHolding> comparator) {
		StockHolding.comparator = comparator;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buyer == null) ? 0 : buyer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() != obj.getClass())
			return false;
		StockHolding other = (StockHolding) obj;
		if (buyer == null) {
			if (other.buyer != null)
				return false;
		} else if (!buyer.equals(other.buyer))
			return false;

		return true;
	}

	public double getCurrentStockPrice() {
		return currentStockPrice;
	}

	public void setCurrentStockPrice(double currentStockPrice) {
		this.currentStockPrice = currentStockPrice;
	}

}
