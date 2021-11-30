package com.preprocessing.methods;

import com.preprocessing.data.BinaryRecord;
import com.preprocessing.data.VoltageRecords;

public class OneHotEncoder {

	public static BinaryRecord encode(VoltageRecords records) {
		float[] voltages = records.getVoltages();
		
		int width = 100;
		int height = 20;
		
		BinaryRecord record = new BinaryRecord(records.getFilename().replace(".csv", "_bin"), width, height);
		float[][] data = new float[height][width];
		
		float samplingRate = voltages.length / width;
		float bounds = 100 / height;
		
		for (int i = 0; i < width; i++) {
			int startIndex = (int) (samplingRate * i);
			int endIndex = ((int) (samplingRate * (i + 1)));

			float avg = 0;
			for (int n = startIndex; n < endIndex; n++) {
				avg += voltages[n];
			}	
			avg /= (endIndex - startIndex);
			
			float bound = (avg * 100) / bounds;
			
			if (bound >= height) {
				bound = height - 1;
			}
			
			data[(int) bound][i] = 1;
		}

		record.setOnehotMatrix(data);
		
		return record;
	}

}
