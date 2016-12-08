package agents;

import java.util.Comparator;
import java.util.Date;

import company.CompanyBDI;
import company.Stock;
import jadex.bridge.IComponentIdentifier;

public class Purchase extends CloneObject {
	private IComponentIdentifier buyer;
	private double stockPurchasePrice;
	private int numberOfStocks;
	private long dateOfPurchase;

	/**
	 * Pretty much if a purchase is still valid, we need this to keep track of
	 * everything happening (The question is, are agents will be independent
	 * threads so, how to keep track of everything, an Agent notifies the
	 * company it made the sale?
	 */

	public Purchase(double maxSpendingMoney, double stockPrice, IComponentIdentifier buyer) {
		this.buyer = buyer;
		this.stockPurchasePrice = stockPrice;
		Double temp = (maxSpendingMoney / stockPrice);
		this.numberOfStocks = temp.intValue();
		// System.out.println("Purcahse INFO: " + this.numberOfStocks + " " +
		// temp + " Price: " + stockPrice + " Max Spending Money: " +
		// maxSpendingMoney);

	}

	public Purchase() {
	}

	/**
	 * Comparator Between purchases
	 */
	public static Comparator<Purchase> comparator = new Comparator<Purchase>() {
		@Override
		public int compare(Purchase c1, Purchase c2) {
			if (c1 == null || c2 == null || !(c1 instanceof Purchase) || !(c2 instanceof Purchase))
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

	public static Comparator<Purchase> getComparator() {
		return comparator;
	}

	public static void setComparator(Comparator<Purchase> comparator) {
		Purchase.comparator = comparator;
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
		Purchase other = (Purchase) obj;
		if (buyer == null) {
			if (other.buyer != null)
				return false;
		} else if (!buyer.equals(other.buyer))
			return false;

		return true;
	}

}
