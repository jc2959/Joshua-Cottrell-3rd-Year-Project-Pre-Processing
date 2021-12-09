package com.preprocessing.methods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.preprocessing.data.VoltageRecords;

public class ExtractData {

	public static VoltageRecords[] extractData(int patientID, String dirPath) throws IOException {

		String patId = patientID + "";

		if (patientID < 10) {
			patId = "0" + patId;
		}

		String[] files = new File(dirPath).list();

		int[] ecgIndex = getECGIndex(dirPath, files);

		return extract(patientID, dirPath, files, ecgIndex);
	}

	private static VoltageRecords[] extract(int patientID, String dirPath, String[] files, int[] ecgIndex)
			throws IOException {
		// VoltageRecords records = new VoltageRecords("rawData_" + patientID + ".csv");
		// records.setAmountOfData(getTotalDataLength(dirPath, files));

		VoltageRecords[] dataRecs = new VoltageRecords[getTotalDataAmount(dirPath, files)];

		int index = 0;
		for (String s : files) {
			if (s.contains("data")) {
				long lineCount;
				File f = new File(dirPath + s);
				try (Stream<String> stream = Files.lines(f.toPath(), StandardCharsets.UTF_8)) {
					lineCount = stream.count();
				}

				VoltageRecords records = new VoltageRecords("rawData_" + patientID + "_" + index + ".csv");
				records.setAmountOfData((int) lineCount);
				extractSingleData(dirPath + s, records, ecgIndex);
				dataRecs[index] = records;

				index += 1;

			}
		}

		return dataRecs;
	}

	private static void extractSingleData(String path, VoltageRecords records, int[] ecgIndex) throws IOException {
		int index = 0;

		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));

		reader.readLine();

		while (reader.ready()) {
			String[] data = reader.readLine().split(",");

			String voltData = "";
			
			if (ecgIndex.length == 1) {
				voltData = data[ecgIndex[0]];
			} else {
				float v1 = Float.parseFloat(data[ecgIndex[0]]);
				float v2 = Float.parseFloat(data[ecgIndex[1]]);
				
				float v = v1 - v2;
				
				voltData = v + "";
			}
			
			records.addLine(index, data[0] + "," + voltData);

			index += 1;
		}

		reader.close();

	}

	private static int getTotalDataAmount(String dirPath, String[] files) throws IOException {
		int length = 0;

		for (String s : files) {
			if (s.contains("data")) {
				length += 1;
			}
		}

		return length;
	}

	private static int[] getECGIndex(String dirPath, String[] files) throws IOException {
		String signalsFile = "";

		for (String s : files) {
			if (s.contains("signals")) {
				signalsFile = s;
				break;
			}
		}

		int[] ecgIndex = null;

		BufferedReader reader = new BufferedReader(new FileReader(new File(dirPath + signalsFile)));

		while (reader.ready()) {
			String line = reader.readLine().toLowerCase();

			if (line.contains("ekg")) {
				String ind = line.split(",")[0];

				ecgIndex = new int[] { Integer.parseInt(ind) };
				break;
			} else if (line.contains("ecg")) {
				String ind = line.split(",")[0];
				if (line.contains("ecg1")) {
					ecgIndex = new int[] { Integer.parseInt(ind), -1 };
				} else if (line.contains("ecg2")) {
					ecgIndex[1] = Integer.parseInt(ind);
				}
			}
		}

		reader.close();

		return ecgIndex;
	}

}
