package agents;

import java.util.Date;

import company.Stock;

public class Purchase {
private Stock stockBought;
private Agent buyer;
private double sotckSalePrice;
private int numberOfStocks;
private Date dateOfPurchase;



/**
 *  Returns the Stock bought 
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
public Agent getBuyer() {
	return buyer;
}
/**
 * Changes the buyer who made the purchase
 * 
 */
public void setBuyer(Agent buyer) {
	this.buyer = buyer;
}
/**
 * 
 * @return the value of the stock at the time it was sold
 */
public double getSotckSalePrice() {
	return sotckSalePrice;
}
/**
 * 
 * changes the price of the stock
 */
public void setSotckSalePrice(double sotckSalePrice) {
	this.sotckSalePrice = sotckSalePrice;
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
public Date getDateOfPurchase() {
	return dateOfPurchase;
}
/**
 * 
 * changes the date of the purchase
 */
public void setDateOfPurchase(Date dateOfPurchase) {
	this.dateOfPurchase = dateOfPurchase;
}


}
