package com.preprocessing.data;

import com.preprocessing.file.Saveable;

public class VoltageRecords implements Saveable {

	String filename;

	float[] timestamps;
	float[] voltages;

	public VoltageRecords(String filename) {
		this.filename = filename;
	}
	
	public void setAmountOfData(int amount) {
		this.timestamps = new float[amount];
		this.voltages = new float[amount];
	}
	
	public void addLine(int index, String line) {
		String[] data = line.split(",");
		
		float ts = Float.parseFloat(data[0]);
		float voltage = Float.parseFloat(data[1]);
		
		timestamps[index] = ts;
		voltages[index] = voltage;
	}

	public void setTimestamps(float[] timestamps) {
		this.timestamps = timestamps;
	}

	public void setVoltages(float[] voltages) {
		this.voltages = voltages;
	}

	public float[] getTimestamps() {
		return timestamps;
	}

	public float[] getVoltages() {
		return voltages;
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

		timestamps = new float[lines.length];
		voltages = new float[lines.length];

		for (int i = 0; i < lines.length; i++) {
			String[] data = lines[i].split(",");

			float timestamp = Float.parseFloat(data[0]);
			float voltage = Float.parseFloat(data[1]);

			timestamps[i] = timestamp;
			voltages[i] = voltage;
		}

	}
	
	@Override
	public void completeLoad() {
	}

}
