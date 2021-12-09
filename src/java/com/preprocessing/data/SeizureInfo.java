package com.preprocessing.data;

import java.util.regex.Pattern;

import com.preprocessing.file.Saveable;

public class SeizureInfo implements Saveable {

	private int patientID;
	private String tmpLoad;

	private int startTs;

	private int[][] timestamps;

	public SeizureInfo(int patientID) {
		this.patientID = patientID;
		this.tmpLoad = "";
	}

	@Override
	public String getFilename() {
		String id = patientID + "";

		if (patientID < 10) {
			id = "0" + id;
		}

		return "Patient" + id + ".txt";
	}

	@Override
	public String getData() {
		// Redundant
		return null;
	}

	@Override
	public void loadData(String fileData) {
	}

	@Override
	public void setAmountOfData(int amount) {
	}

	public int getPatientID() {
		return patientID;
	}

	@Override
	public void addLine(int index, String line) {
		if (line.contains("Registration start")) {
			startTs = getSeconds(line.replace("Registration start: ", ""));
			tmpLoad += "~";
		} else if (Pattern.matches("Seizure\\d* start.*", line)) {
			int s = getSeconds(line.split(":")[1]) - startTs;

			if (s < 0) {
				s += 24 * 3600;
			}

			tmpLoad += s + ",";
		} else if (Pattern.matches("Seizure\\d* end.*", line)) {
			int s = getSeconds(line.split(":")[1]) - startTs;

			if (s < 0) {
				s += 24 * 3600;
			}

			tmpLoad += s + ";";
		}
	}

	public int getAmountOfFiles() {
		return timestamps.length;
	}
	
	public int[] getTimestamps(int fileIndex) {
		return timestamps[fileIndex];
	}

	private int getSeconds(String line) {
		String[] times = line.trim().split("\\.");

		int ts = 0;

		int[] secConv = new int[] { 3600, 60, 1 };

		for (int i = 0; i < 3; i++) {
			ts += Integer.parseInt(times[i]) * secConv[i];
		}

		return ts;
	}

	@Override
	public void completeLoad() {
		String[] filesTs = tmpLoad.split("~");
		timestamps = new int[filesTs.length - 1][];
		
		for (int i = 0; i < filesTs.length - 1; i++) {
			String[] tsPair = filesTs[i + 1].split(";");

			timestamps[i] = new int[tsPair.length * 2];

			for (int n = 0; n < tsPair.length; n++) {
				String[] ts = tsPair[n].split(",");

				timestamps[i][n * 2] = Integer.parseInt(ts[0]);
				timestamps[i][n * 2 + 1] = Integer.parseInt(ts[1]);
			}
		}

	}

}
