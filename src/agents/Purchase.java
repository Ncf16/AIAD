package agents;

import java.util.Comparator;
import java.util.Date;

import company.CompanyBDI;
import company.Stock;
import jadex.bridge.IComponentIdentifier;

public class Purchase extends CloneObject{
	private Stock stockBought;
	private IComponentIdentifier buyer;
	private double sotckPurchasePrice;
	private double stockSalePrice;
	private int numberOfStocks;
	private long dateOfPurchase;
	private IComponentIdentifier company;
	/**
	 * Pretty much if a purchase is still valid, we need this to keep track of
	 * everything happening (The question is, are agents will be independent
	 * threads so, how to keep track of everything, an Agent notifies the
	 * company it made the sale?
	 */
	private boolean sold = false;

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

	public boolean sellStock(StandardBDI seller) {

		// TODO check if seller is buyer
		if (!seller.equals(buyer))
			return false;

		stockSalePrice = stockBought.getStockPrice();
		return (sold = true);

	}

	public double getSaleProfit() {
		return stockSalePrice - sotckPurchasePrice;
	}

	// -----------------------------------------------
	/**
	 * Returns the Stock bought
	 * 
	 * @return
	 */
	public Stock getStockBought() {
		return stockBought;
	}

	/**
	 * Changes the stock that was bought in the purchasea
	 * 
	 */
	public void setStockBought(Stock stockBought) {
		this.stockBought = stockBought;
	}

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
	 * @return the value of the stock at the time it was sold
	 */
	public double getSotckSalePrice() {
		return stockSalePrice;
	}

	/**
	 * 
	 * changes the price of the stock
	 */
	public void setSotckSalePrice(double sotckSalePrice) {
		this.stockSalePrice = sotckSalePrice;
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

	public boolean isSold() {
		return sold;
	}

	public void setSold(boolean sold) {
		this.sold = sold;
	}

	public double getSotckPurchasePrice() {
		return sotckPurchasePrice;
	}

	public void setSotckPurchasePrice(double sotckPurchasePrice) {
		this.sotckPurchasePrice = sotckPurchasePrice;
	}

	public double getStockSalePrice() {
		return stockSalePrice;
	}

	public void setStockSalePrice(double stockSalePrice) {
		this.stockSalePrice = stockSalePrice;
	}

	public IComponentIdentifier getCompany() {
		return company;
	}

	public static Comparator<Purchase> getComparator() {
		return comparator;
	}

	public static void setComparator(Comparator<Purchase> comparator) {
		Purchase.comparator = comparator;
	}

	public void setCompany(IComponentIdentifier componentIdentifier) {
		this.company = componentIdentifier;
	}

}
