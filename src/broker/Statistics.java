package broker;

import java.util.ArrayList;

import company.Stock;

public class Statistics {
	private static Statistics instance = null;

	private Statistics() {
	}

	public static Statistics instance() {
		if (instance == null)
			instance = new Statistics();
		return instance;
	}

	double getMean(ArrayList<Double> data) {
		double sum = 0.0;
		for (double a : data)
			sum += a;
		return sum / data.size();

	}

	double getVariance(ArrayList<Double> data) {
		double mean = getMean(data);
		double temp = 0;
		for (double a : data)
			temp += (a - mean) * (a - mean);
		return temp / data.size();
	}

	double getStdDev(ArrayList<Double> data) {
		return Math.sqrt(getVariance(data));
	}

}