package com.preprocessing.methods;

import com.preprocessing.data.VoltageRecords;

public class LinearFitting {

	public static VoltageRecords fit(VoltageRecords records) {
		VoltageRecords lfData = new VoltageRecords("LFSampleData.csv");
		
		float[] voltages = records.getVoltages();
		float[] newData = new float[voltages.length];
		
		float[] vs = new float[voltages.length];
		
		float avg = 0;
		for (int i =0; i < voltages.length; i++) {
			avg += voltages[i];
		}
		avg /= voltages.length;
		
		
		for (int i = 0; i < voltages.length; i++) {
			vs[i] = voltages[i] - avg;
		}
		
		float[] stats = QRSSegmentation.getStats(vs);

		float upperBound = stats[2] + 5f * (stats[2] - stats[0]);
		float lowerBound = stats[0] - 5f * (stats[2] - stats[0]);
		
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		
		for (int i = 0; i < voltages.length; i++) {
			float val = vs[i];
			
			if (val > upperBound) {
				val = upperBound;
			}
			
			if (val < lowerBound) {
				val = lowerBound;
			}
			
			newData[i] = val;
			
			if (min > val) {
				min = val;
			}
			
			if (max < val) {
				max = val;
			}
			
		}
		

		for (int i = 0; i < voltages.length; i++) {
			newData[i] = (newData[i] - min) / (max - min);
		}
		
		lfData.setTimestamps(records.getTimestamps());
		lfData.setVoltages(newData);
		
		return lfData;
	}
	
}
