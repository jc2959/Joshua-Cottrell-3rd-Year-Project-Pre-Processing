package com.preprocessing.methods;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.preprocessing.data.VoltageRecords;
import com.preprocessing.file.FileHandler;

public class QRSSegmentation {

	/**
	 * Segments and saves the QRS complexes 
	 * 
	 * @param data
	 */
	public static void segmentQRSComplexes(VoltageRecords data) {
		File file = new File("src\\resources\\seg");
		file.delete();
		file.mkdir();

		float[] timestamps = data.getTimestamps();
		float[] voltages = data.getVoltages();

		float[] stats = getStats(voltages);
		float rPeakThreshold = stats[2] + 5f * (stats[2] - stats[0]);

		int ctr = 0;

		// While loops skips first complex as it is corrupted

		ctr = skipQRSComplex(ctr, stats[1], voltages);
		ctr = findRPeak(ctr, rPeakThreshold, voltages);
		ctr = skipQRSComplex(ctr, stats[1], voltages);

		List<Integer> pWaveIndices = new ArrayList<Integer>();

		int pos = 0;

		while (ctr < voltages.length) {
			// Finds the next R peak
			ctr = findRPeak(ctr, rPeakThreshold, voltages);

			if (ctr >= voltages.length) {
				break;
			}

			// Gets the start of the P wave preceeding the R peak
			int pWavePos = findPWave(ctr, stats[1], stats[2], voltages);

			// Prevents infinite loops if the ctr gets stuck
			if (pWaveIndices.size() > 0 && pWaveIndices.get(pWaveIndices.size() - 1) == pWavePos) {
				// Ensures that the ctr goes to the next P-QRS-T complex
				ctr = skipQRSComplex(ctr, stats[1], voltages);
			} else {
				pWaveIndices.add(pWavePos);
			}

			// Gets past the current R peak
			ctr = skipQRSComplex(ctr, stats[1], voltages);

			// Prevents memory issues by saving the data and clearing
			if (pWaveIndices.size() >= 10000) {
				saveData(pos, pWaveIndices, timestamps, voltages);

				pos += pWaveIndices.size() - 1;
				int lst = pWaveIndices.get(pWaveIndices.size() - 1);

				pWaveIndices.clear();
				pWaveIndices.add(lst);
			}

		}

		// Saves the last of the data
		saveData(pos, pWaveIndices, timestamps, voltages);

	}

	private static void saveData(int pos, List<Integer> pWaveIndices, float[] timestamps, float[] voltages) {
		for (int i = 0; i < pWaveIndices.size() - 1; i++) {
			int start = pWaveIndices.get(i);
			int end = pWaveIndices.get(i + 1);

			float[] tsData = new float[end - start];
			float[] segData = new float[end - start];

			for (int n = 0; n < segData.length; n++) {
				tsData[n] = timestamps[n + start];
				segData[n] = voltages[n + start];
			}

			// Normalises
			float[] stats = getStats(segData);
			for (int n = 0; n < segData.length; n++) {
				segData[n] = (segData[n] - stats[3]) / (stats[4] - stats[3]);
			}

			VoltageRecords record = new VoltageRecords("seg\\Segment" + (pos + i) + ".csv");
			record.setTimestamps(tsData);
			record.setVoltages(segData);

			float timeRange = tsData[tsData.length - 1] - tsData[0];

			// Time range makes sure the data would be within 30 BPM and 200 BPM
			// Assumes other ranges is bad data
			if (timeRange > 0.3f && timeRange < 2f) {
				try {
					FileHandler.saveData(record);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * From the R peak, this finds the start of the P wave of the current P-QRS-T complex
	 * 
	 * @param ctr The current place in the data
	 * @param median The median value of the voltages
	 * @param q3 The upper quartile value of the voltages
	 * @param voltages The voltages
	 * @return The position of the p wave in the 
	 */
	private static int findPWave(int ctr, float median, float q3, float[] voltages) {

		while (voltages[ctr] > median) {
			ctr -= 1;
		}

		while (voltages[ctr] < q3) {
			ctr -= 1;
		}

		while (voltages[ctr] > q3) {
			ctr -= 1;
		}

		return ctr;
	}

	/**
	 * Finds the next R peak in the data
	 * 
	 * @param ctr The current position
	 * @param rPeakThreshold The threshold for what constitutes an R peak
	 * @param voltages the voltages of the ECG data
	 * @return The position of the R peak
	 */
	private static int findRPeak(int ctr, float rPeakThreshold, float[] voltages) {
		while (voltages[ctr] < rPeakThreshold) {
			ctr += 1;

			if (ctr >= voltages.length) {
				break;
			}
		}

		return ctr;
	}

	/**
	 * Skips the rest of the R peak
	 * 
	 * @param ctr The current position
	 * @param median The median value of the voltages
	 * @param voltages The voltage data from the ECG
	 * @return The new position
	 */
	private static int skipQRSComplex(int ctr, float median, float[] voltages) {
		while (voltages[ctr] > median) {
			ctr += 1;

			if (ctr >= voltages.length) {
				break;
			}
		}

		return ctr;
	}

	/**
	 * @param data
	 * @return { Q1, Median, Q3, Min, Max }
	 */
	public static float[] getStats(float[] data) {
		List<Float> vData = new ArrayList<Float>();
		for (float f : data) {
			vData.add(f);
		}
		Collections.sort(vData);

		int q1Ptr = (vData.size() + 1) / 4;

		float q1 = -1;
		float med = -1;
		float q3 = -1;

		float min = vData.get(0);
		float max = vData.get(vData.size() - 1);

		for (int i = 0; i < vData.size(); i++) {
			float volt = vData.get(i);

			if (i == q1Ptr) {
				q1 = volt;
			}

			if (i == q1Ptr * 2) {
				med = volt;
			}

			if (i == q1Ptr * 3) {
				q3 = volt;
			}

		}

		return new float[] { q1, med, q3, min, max };
	}

}
