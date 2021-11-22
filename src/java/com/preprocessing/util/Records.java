package com.preprocessing.util;

import com.preprocessing.file.Saveable;

public class Records implements Saveable {

	String filename;
	
	int[] timestamps;
	float[] voltages;
	
	public Records(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public String getData() {
		String fileData = "";
		
		for (int i = 0; i < timestamps.length; i++) {
			String datapoint = timestamps[i] + "," + voltages[i];
			fileData += datapoint + "\n";
		}
		
		return fileData;
	}

	@Override
	public void loadData(String fileData) {
		String[] lines = fileData.split("\n");
		
		timestamps = new int[lines.length];
		voltages = new float[lines.length];
		
		for (int i = 0; i < lines.length; i++) {
			String[] data = lines[i].split(",");
		
			int timestamp = Integer.parseInt(data[0]);
			float voltage = Float.parseFloat(data[1]);
			
			timestamps[i] = timestamp;
			voltages[i] = voltage;
		}
		
	}

}
