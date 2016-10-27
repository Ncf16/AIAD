package company;

public class VolatileStock extends Stock {

	private static final double DEFAULT_FLUCTUATION = 10;

	/**
	 * Constructor with a user defined priceFluctuation %
	 * 
	 * @param stockPrice
	 * @param priceFluctuation
	 */
	public VolatileStock(double stockPrice, double priceFluctuation) {
		super(stockPrice, priceFluctuation);
	}

	/**
	 * Constructor with default priceFluctuation %
	 * 
	 * @param stockPrice
	 * @param priceFluctuation
	 */
	public VolatileStock(double stockPrice) {
		super(stockPrice, DEFAULT_FLUCTUATION);
	}
}
