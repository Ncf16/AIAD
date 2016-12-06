package company;

import java.util.Random;

public class Stock {
	public enum StockType {
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

	public Stock() {
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
			this.type = StockType.NORMAL;
		} else if (type == StockType.VOLATILE) {
			priceFluctuation = 10.0;
			this.type = StockType.VOLATILE;
		} else if (type == StockType.VERY_VOLATILE) {
			priceFluctuation = 25.0;
			this.type = StockType.VERY_VOLATILE;
		}
	}

	/**
	 * 
	 * @return 1 if positive or -1 if the fluctuation will be negative
	 */
	public int isItPositiveFluctuation() {
		if (randomNumber.nextInt(RANDOM_INT_LIMIT) == 0)
			return -1;
		else
			return 1;
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
		double variation = (randomNumber.nextDouble() * getPriceFluctuation() * isItPositiveFluctuation() / 100.0) + 1;
		stockPrice = stockPrice * variation;
	}

	public StockType getType() {
		return type;
	}

	public void setType(StockType type) {
		this.type = type;
	}
}
