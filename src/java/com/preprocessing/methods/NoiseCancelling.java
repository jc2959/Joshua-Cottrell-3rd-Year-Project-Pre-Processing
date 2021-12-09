package com.preprocessing.methods;

import com.preprocessing.data.VoltageRecords;

/**
 * Removes noise from the ECG
 * 
 * @author user
 *
 */
public class NoiseCancelling {

	/**
	 * Performs a threes scale wavelet transform to remove noise from the ECG
	 * 
	 * @param sampleFreq
	 * @param windowSize
	 * @param data
	 * @return The denoised records
	 */
	public static VoltageRecords threeScaleWaveletTransform(VoltageRecords data) {
		VoltageRecords records = new VoltageRecords("3sWTData.csv");

		float[] voltages = data.getVoltages();
		float[] newData = new float[voltages.length];

		// Voltages in range -1 to 1
		float[] vs = new float[voltages.length];

		float mean = 0f;
		for (int i = 0; i < voltages.length; i++) {
			mean += voltages[i];
		}
		mean /= voltages.length;

		for (int i = 0; i < vs.length; i++) {
			vs[i] = voltages[i] - mean;
		}

		float[] lp = vs.clone();
		for (int n = 0; n < 3; n++) {
			float[] tmp = new float[lp.length];

			for (int i = 0; i < voltages.length; i++) {
				float lpVal = deconstructLP(lp, i);

				tmp[i] = lpVal;
			}

			lp = softThreshold(tmp);
		}
		
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;

		for (int i = 0; i < lp.length; i++) {
			float val = reconstructLP(lp, i);
			newData[i] = val;

			if (max < val) {
				max = val;
			}

			if (min > val) {
				min = val;
			}
		}

		for (int i = 0; i < newData.length; i++) {
			newData[i] = (newData[i] - min) / (max - min);
		}

		records.setTimestamps(data.getTimestamps());
		records.setVoltages(newData);

		return records;
	}

	private static float deconstructLP(float[] voltages, int index) {
		float[] coef = new float[] { 0.0033357253f, -0.0125807520f, -0.0062414902f, 0.0775714938f, -0.0322448696f,
				-0.2422948871f, 0.1384281459f, 0.7243085284f, 0.6038292698f, 0.1601023980f };

		return waveletTransform(voltages, index, coef);
	}

	private static float reconstructLP(float[] voltages, int index) {
		float[] coef = new float[] { 0.1601023980f, 0.6038292698f, 0.7243085284f, 0.1384281459f, -0.2422948871f,
				-0.0322448696f, 0.0775714938f, -0.0062414902f, -0.0125807520f, 0.0033357253f };

		return waveletTransform(voltages, index, coef);
	}

	private static float waveletTransform(float[] voltages, int index, float[] coef) {
		float val = 0;

		int endIndex = index + coef.length;

		if (endIndex > voltages.length) {
			endIndex = voltages.length;
		}

		for (int n = index; n < endIndex; n++) {
			int coefI = n - index;

			val += coef[coefI] * voltages[n];
		}

		return val;
	}

	private static float[] softThreshold(float[] data) {
		float stdev = getSTDev(data);
		
		float coef = (float) Math.sqrt(2 * Math.log(data.length));
		float threshold = stdev * 0.025f;
		
		float[] newData = new float[data.length];
		
		for (int i = 0; i < data.length; i++) {
			float val = data[i];
			
			if (Math.abs(val) >= threshold) {
				newData[i] = val - sign(val) * threshold;
			}
		}
		
		return newData;
	}
	
	private static float sign(float x) {
		return x > 0 ? 1 : x < 0 ? -1 : 0;
	}
	
	private static float getSTDev(float[] data) {
		float stdev = 0;
		
		float avg = getAvg(data);
		
		for (int i = 0; i < data.length; i++) {
			float tmp = (data[i] - avg);
			
			stdev += tmp * tmp;
		}
		
		stdev /= data.length;
		
		return (float) Math.sqrt(stdev);
	}
	
	private static float getAvg(float[] data) {
		float avg = 0;
		
		for (float f : data) {
			avg += f;
		}
		
		return avg / data.length;
	}
	
}
