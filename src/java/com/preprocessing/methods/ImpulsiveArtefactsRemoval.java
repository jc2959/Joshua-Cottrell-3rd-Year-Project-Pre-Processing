package com.preprocessing.methods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.preprocessing.data.VoltageRecords;

public class ImpulsiveArtefactsRemoval {

	public static VoltageRecords removeImpulsiveArtefacts(float sampleFreq, float windowSize, VoltageRecords data) {
		VoltageRecords records = new VoltageRecords("IARData.csv");

		int freq = (int) (windowSize / sampleFreq);

		float[] voltages = data.getVoltages();

		float[] newData = new float[voltages.length];

		float threshold = 0.38f;

		// Iterate through each segment
		for (int i = 0; i < Math.ceil(voltages.length / freq); i++) {
			int startIndex = i * freq;
			int endIndex = (i + 1) * freq;

			float median = getMedian(voltages, startIndex, endIndex);
			float maxDev = getMaxDev(voltages, startIndex, endIndex, median);

			float avgDiff = 0;

			for (int n = startIndex; n < endIndex; n++) {
				avgDiff += Math.abs(voltages[n] - median) / maxDev;
			}

			avgDiff /= (endIndex - startIndex);

			if (avgDiff > threshold) {
				int prevStartIndex = (i - 1) * freq;
				int nextEndIndex = (i + 2) * freq;

				float avg = 0;
				if (i == 0) {
					avg = getAverage(voltages, endIndex, nextEndIndex);
				} else if (i == Math.ceil(voltages.length / freq) - 1) {
					avg = getAverage(voltages, prevStartIndex, startIndex);
				} else {
					avg = (getAverage(voltages, prevStartIndex, startIndex)
							+ getAverage(voltages, endIndex, nextEndIndex)) / 2;
				}

				for (int n = startIndex; n < endIndex; n++) {
					newData[n] = avg;
				}
				
			} else {
				for (int n = startIndex; n < endIndex; n++) {
					newData[n] = voltages[n];
				}
			}

		}

		records.setTimestamps(data.getTimestamps());
		records.setVoltages(newData);

		return records;
	}

	private static float getMedian(float[] data, int startIndex, int endIndex) {
		List<Float> sortedData = new ArrayList<Float>();

		for (int i = startIndex; i < endIndex; i++) {
			sortedData.add(data[i]);
		}

		Collections.sort(sortedData);

		if (sortedData.size() == 1) {
			return sortedData.get(0);
		}

		return sortedData.get((sortedData.size() + 1) / 2);
	}

	private static float getMaxDev(float[] data, int startIndex, int endIndex, float median) {
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;

		for (int i = startIndex; i < endIndex; i++) {
			if (data[i] < min) {
				min = data[i];
			}

			if (data[i] > max) {
				max = data[i];
			}
		}

		return Math.max(median - min, max - median);
	}

	private static float getAverage(float[] data, int startIndex, int endIndex) {
		float avg = 0;

		for (int i = startIndex; i < endIndex; i++) {
			avg += data[i];
		}

		return avg / (endIndex - startIndex);
	}

}