package company;

import java.util.Random;

/**
 * 
 * Acho que não compensa ter as 3 classes filhas 1 Enum seria sufeciente IMO but
 * let us see what you guys think
 *
 */
public class Stock {
	enum StockType {
		NORMAL, VOLATILE, VERY_VOLATILE
	}

	private static final int RANDOM_INT_LIMIT = 3;
	private static double stockPrice = 0.0;
	private double priceFluctuation = 0.0;
	private Random randomNumber = new Random();

	public Stock(double stockPrice) {
		Stock.stockPrice = stockPrice;
		setFluctuationWithType(StockType.NORMAL);
	}

	public Stock(double stockPrice, StockType type) {
		Stock.stockPrice = stockPrice;
		setFluctuationWithType(type);
	}

	/**
	 * Changes the % of price fluctuation according to the stock type
	 * 
	 * @param type
	 */
	public void setFluctuationWithType(StockType type) {
		if (type == StockType.NORMAL) {
			priceFluctuation = 2.5;
		} else if (type == StockType.VOLATILE) {
			priceFluctuation = 10.0;

		} else if (type == StockType.VERY_VOLATILE) {
			priceFluctuation = 25.0;

		}
	}

	/**
	 * 
	 * @return 1 if positive or -1 if the fluctuation will be negative
	 */
	public int isItPositiveFluctuation() {
		return randomNumber.nextInt(RANDOM_INT_LIMIT) - 1;
	}

	public static double getStockPrice() {
		return stockPrice;
	}

	public static void setStockPrice(double stockPrice) {
		Stock.stockPrice = stockPrice;
	}

	public double getPriceFluctuation() {
		return priceFluctuation;
	}

	public void setPriceFluctuation(double priceFluctuation) {
		this.priceFluctuation = priceFluctuation;
	}

	public void changePrice() {
		setStockPrice(Stock.getStockPrice()
				+ Stock.getStockPrice() * (getPriceFluctuation() / 100.0 * isItPositiveFluctuation()));
	}

}
