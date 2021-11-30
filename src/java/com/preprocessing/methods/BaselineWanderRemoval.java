package com.preprocessing.methods;

import com.preprocessing.data.VoltageRecords;

public class BaselineWanderRemoval {

	/**
	 * Removes the baseline wander from an ECG dataset
	 * 
	 * @param data The ECG data
	 * @param sampleFreq The frequency the ECG was taken at
	 * @param cutoffFreq The cutoff frequency
	 * @return The new ECG data without baseline wander
	 */
	public static VoltageRecords removeBaselineWander(VoltageRecords data, float sampleFreq, float cutoffFreq) {
		VoltageRecords bwData = new VoltageRecords("BWSampleData.csv");

		float[] butterworthCoeff = getButterworthCoefficients(sampleFreq, cutoffFreq);

		float prevX = 0;
		float prevY = 0;

		float[] curVoltages = data.getVoltages();
		float[] bwVoltages = new float[curVoltages.length];

		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;

		// Creates a new signal by subtracting the reference signal from the original signal
		// The reference signal is created by applying the low-pass first order Butterworth filter
		for (int i = 0; i < curVoltages.length; i++) {
			float curVoltage = curVoltages[i];
			float refVoltage =getReferenceSignal(butterworthCoeff, curVoltage, prevX, prevY);

			prevY = refVoltage;
			prevX = curVoltages[i];

			float bwVoltage = curVoltages[i] - refVoltage;
			bwVoltages[i] = bwVoltage;

			if (bwVoltage < min) {
				min = bwVoltage;
			}

			if (bwVoltage > max) {
				max = bwVoltage;
			}

		}

//		 Normalises the new data
		for (int i = 0; i < bwVoltages.length; i++) {
			bwVoltages[i] = (bwVoltages[i] - min) / (max - min);
		}

		bwData.setTimestamps(data.getTimestamps());
		bwData.setVoltages(bwVoltages);

		return bwData;
	}

	/**
	 * Gets the coefficients needed to apply the low-pass first order Butterworth filter
	 * 
	 * @param sampleFreq The frequency the data was sampled at
	 * @param cutoffFreq The cutoff frequency
	 * @return The Butterworth coefficients
	 */
	private static float[] getButterworthCoefficients(float sampleFreq, float cutoffFreq) {
		float cFreq = (float) Math.tan(Math.PI * cutoffFreq / sampleFreq);

		float co1 = cFreq / (cFreq + 1);
		float co2 = (cFreq - 1) / (cFreq + 1);

		return new float[] { co1, co2 };
	}

	/**
	 * Applies the low-pass first order Butterworth filter to retrieve a reference signal
	 * 
	 * @param bwCoeff The Butterworth Coefficients
	 * @param currentV The current voltage
	 * @param prevX The previous voltage
	 * @param prevY The last reference signal value
	 * @return The value of the reference signal for the current voltage
	 */
	private static float getReferenceSignal(float[] bwCoeff, float currentV, float prevX, float prevY) {
		return bwCoeff[0] * currentV + bwCoeff[0] * prevX - bwCoeff[1] * prevY;
	}

}
