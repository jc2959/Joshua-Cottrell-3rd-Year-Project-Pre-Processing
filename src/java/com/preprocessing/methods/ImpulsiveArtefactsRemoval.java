package com.preprocessing.methods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.preprocessing.data.VoltageRecords;

public class ImpulsiveArtefactsRemoval {

	public static VoltageRecords removeImpulsiveArtefacts(float sampleFreq, float windowSize, VoltageRecords data) {
		VoltageRecords records = new VoltageRecords("IARData.csv");
		
		float freq = windowSize / sampleFreq;
		
		float[] voltages = data.getVoltages();
		
		float[] newData = new float[voltages.length];
		
		float threshold = 0.2f;
		
		for (int i = 0; i < voltages.length; i++) {
			int startIndex = (int) (i - (freq / 2));
			int endIndex = (int) (i + (freq / 2));
			
			if (startIndex < 0) {
				startIndex = 0;
			}
			
			if (endIndex >= voltages.length) {
				endIndex = voltages.length;
			}
			
			float val = voltages[i];
			float median = getMedian(voltages, startIndex, endIndex);
			
			float percChange = Math.abs((val - median) / val);
			
			if (percChange > threshold) {
				newData[i] = median;
			} else {
				newData[i] = voltages[i];
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
		
		return sortedData.get((sortedData.size() + 1) / 2);
	}
	
}
