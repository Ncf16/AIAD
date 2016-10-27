package company;

public class SteadyStock extends Stock {
	private static final double DEFAULT_FLUCTUATION = 2.5;

	/**
	 * Constructor with a user defined priceFluctuation %
	 * 
	 * @param stockPrice
	 * @param priceFluctuation
	 */
	public SteadyStock(double stockPrice, double priceFluctuation) {
		super(stockPrice, priceFluctuation);
	}

	/**
	 * Constructor with default priceFluctuation %
	 * 
	 * @param stockPrice
	 * @param priceFluctuation
	 */
	public SteadyStock(double stockPrice) {
		super(stockPrice, DEFAULT_FLUCTUATION);
	}
}
