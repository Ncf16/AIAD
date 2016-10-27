package company;

public class VeryVolatileStock extends Stock {
	private static final double DEFAULT_FLUCTUATION = 25;
	/**
	 * Constructor with a user defined priceFluctuation %
	 * 
	 * @param stockPrice
	 * @param priceFluctuation
	 */
	public VeryVolatileStock(double stockPrice, double priceFluctuation) {
		super(stockPrice, priceFluctuation);
	}
	/**
	 * Constructor with default priceFluctuation %
	 * 
	 * @param stockPrice
	 * @param priceFluctuation
	 */
	public VeryVolatileStock(double stockPrice) {
		super(stockPrice, DEFAULT_FLUCTUATION);
	}
}
