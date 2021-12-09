package com.preprocessing.methods;

import java.io.File;
import java.io.IOException;

import com.preprocessing.data.BinaryRecord;
import com.preprocessing.data.SeizureInfo;
import com.preprocessing.data.VoltageRecords;
import com.preprocessing.file.FileHandler;

public class OneHotEncoder {

	private static final int preictalPeriod = 900;

	public static BinaryRecord[] encodeAll(SeizureInfo sInfo, int fileIndex) throws IOException {
		File f = new File(FileHandler.dirPath + "\\seg\\");
		String[] filenames = f.list();

		BinaryRecord[] records = new BinaryRecord[filenames.length];

		for (int i = 0; i < ((sInfo.getTimestamps(fileIndex).length / 2) * sInfo.getAmountOfFiles()) + 1; i++) {
			new File(FileHandler.dirPath + "\\bin\\" + i).mkdir();
		}

		for (int i = 0; i < filenames.length; i++) {
			String filename = filenames[i];

			if (!filename.contains("_" + fileIndex + ".csv")) {
				continue;
			}

			VoltageRecords record = new VoltageRecords("seg\\" + filename);

			FileHandler.loadData(record);

			records[i] = encode(record, sInfo, fileIndex);

		}

		return records;
	}

	private static boolean isInTimeRange(VoltageRecords record, int[] timestamps, int index) {
		if (index * 2 >= timestamps.length) {
			return false;
		}

		float recordTs = record.getTimestamps()[0];

		int tsStart = timestamps[index * 2];
		int tsEnd = timestamps[index * 2 + 1];

		return (recordTs >= tsStart) && (recordTs <= tsEnd);
	}

	private static BinaryRecord encode(VoltageRecords records, SeizureInfo sInfo, int fileIndex) {
		float[] voltages = records.getVoltages();
		int[] seizureTs = sInfo.getTimestamps(fileIndex);

		int width = 100;
		int height = 40;

		String name = records.getTimestamps()[0] + "_bin";

		int seizureIndex = getSeizureIndex(seizureTs, records.getTimestamps()[0]);

		if (isInTimeRange(records, seizureTs, seizureIndex)) {
			return null;
		}

		int[] preictalTs = null;

		if (seizureIndex * 2 < seizureTs.length) {
			preictalTs = new int[] { seizureTs[seizureIndex * 2] - preictalPeriod, seizureTs[seizureIndex * 2] };
		}

		if (preictalTs != null && isInTimeRange(records, preictalTs, 0)) {
			name = "p" + name;
		} else {
			name = "i" + name;
		}

		name = (fileIndex * sInfo.getAmountOfFiles() + seizureIndex) + "\\" + name;

		BinaryRecord record = new BinaryRecord(name, width, height);
		float[][] data = new float[height][width];

		float samplingRate = (float) voltages.length / width;
		float bounds = 100 / height;

		int prevBound = -1;

		boolean replaceLast = false;

		for (int i = 0; i < width; i++) {
			int startIndex = (int) (samplingRate * i);
			int endIndex = ((int) (samplingRate * (i + 1)));

			if (endIndex - startIndex == 0) {
				replaceLast = true;
				continue;
			}

			float avg = 0;
			for (int n = startIndex; n < endIndex; n++) {
				avg += voltages[n];
			}
			avg /= (endIndex - startIndex);

			float bound = (avg * 100) / bounds;

			if (bound >= height) {
				bound = height - 1;
			}

			// Makes sure there's no empty time steps
			if (replaceLast) {
				int lstBound = 0;

				if (prevBound == -1) {
					lstBound = (int) bound;
				} else {
					lstBound = (int) ((bound + prevBound) / 2);
				}

				data[lstBound][i - 1] = 1;

				replaceLast = false;
			}

			prevBound = (int) bound;

			data[(int) bound][i] = 1;
		}

		record.setOnehotMatrix(data);

		return record;
	}

	private static int getSeizureIndex(int[] timestamps, float ts) {
		int index = 0;

		for (int i = 1; i < timestamps.length; i += 2) {
			if (ts < timestamps[i]) {
				break;
			}

			index += 1;
		}

		return index;
	}

}
