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

	double getVariance(ArrayList<Double> data, double mean) {

		double temp = 0;
		for (double a : data)
			temp += (a - mean) * (a - mean);
		return temp / data.size();
	}

	double getVarCoef(ArrayList<Double> data) {
		double mean = getMean(data);
		double v = Math.sqrt(getVariance(data, mean)) / mean;
		System.out.println("STDR_DEV: " + v);
		return v;
	}

}