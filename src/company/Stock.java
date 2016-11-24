package company;

import java.util.Random;

public class Stock {
	enum StockType {
		NORMAL, VOLATILE, VERY_VOLATILE
	}

	private static final int RANDOM_INT_LIMIT = 2;
	private double stockPrice = 0.0;
	private double priceFluctuation = 0.0;
	private StockType type;
	private Random randomNumber = new Random();

	public Stock(double stockPrice) {
		this.stockPrice = stockPrice;
		setFluctuationWithType(StockType.NORMAL);
	}

	public Stock(double stockPrice, StockType type) {
		this.stockPrice = stockPrice;
		this.type = type;
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

	public double getStockPrice() {
		return stockPrice;
	}

	public void setStockPrice(double stockPrice) {
		this.stockPrice = stockPrice;
	}

	public double getPriceFluctuation() {
		return priceFluctuation;
	}

	public void setPriceFluctuation(double priceFluctuation) {
		this.priceFluctuation = priceFluctuation;
	}

	public void changePrice() {
		setStockPrice(getStockPrice() + getStockPrice()
				* (randomNumber.nextDouble() * getPriceFluctuation() + 1 / 100.0 * isItPositiveFluctuation()));
	}

	public StockType getType() {
		return type;
	}

	public void setType(StockType type) {
		this.type = type;
	}
}
