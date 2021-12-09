package com.preprocessing.main;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.preprocessing.data.BinaryRecord;
import com.preprocessing.data.SeizureInfo;
import com.preprocessing.data.VoltageRecords;
import com.preprocessing.file.FileHandler;
import com.preprocessing.methods.BaselineWanderRemoval;
import com.preprocessing.methods.ExtractData;
import com.preprocessing.methods.LinearFitting;
import com.preprocessing.methods.NoiseCancelling;
import com.preprocessing.methods.OneHotEncoder;
import com.preprocessing.methods.QRSSegmentation;

public class Main {

	public static void main(String[] args) {
		int[] ids = new int[] {5,6};
		
		for (int i : ids) {
			processAll(i);
		}
		
		startML();
	    
	}

	private static void startML() {
		Desktop desktop = Desktop.getDesktop();

	    try {
			desktop.open(new File("D:\\Uni Project\\3rd Year Project Workspace\\Test CNN\\run.bat"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private static void processAll(int patientID) {
		String patID = patientID < 10 ? "0" + patientID : "" + patientID;

		FileHandler.dirPath = "D:\\Uni Project\\Dataset\\PlosONE_Data\\PlosONE_Data\\Patient" + patID + "\\";

		SeizureInfo patientInfo = new SeizureInfo(patientID);

		try {
			FileHandler.loadData(patientInfo);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		System.out.println("LOADING DATA");
		VoltageRecords[] allData = null;
		try {
			allData = ExtractData.extractData(patientID, FileHandler.dirPath);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("DATA LOADED\n");

		cleanResources();

		for (int i = 0; i < allData.length; i++) {
			process(allData[i], patientInfo, i);
		}
	}

	private static void cleanResources() {
		File bin = new File(FileHandler.dirPath + "\\bin\\");
		if (bin.exists()) {
			for (File f : bin.listFiles()) {
				f.delete();
			}
			bin.delete();
		}
		bin.mkdir();

		File seg = new File(FileHandler.dirPath + "\\seg");
		if (seg.exists()) {
			for (File f : seg.listFiles()) {
				f.delete();
			}

			seg.delete();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		seg.mkdir();
	}

	private static void process(VoltageRecords data, SeizureInfo patientInfo, int fileIndex) {

//		System.out.println("LOADING DATA");
//		try {
//			FileHandler.loadData(sampleData);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("DATA LOADED\n");

		float samplingRate = 1f / (data.getTimestamps()[1] - data.getTimestamps()[0]);
//		
//		System.out.println("Performing Impulsive Artefact Removal");
//
//		VoltageRecords iarData = ImpulsiveArtefactsRemoval.removeImpulsiveArtefacts(1f / samplingRate, 0.03f, sampleData);
//
//		System.out.println("Impulsive Artefacts removed\n");

		System.out.println("PERFORMING BASELINE WANDER REMOVAL");
		VoltageRecords bwData = BaselineWanderRemoval.removeBaselineWander(data, samplingRate, 5f);
		System.out.println("BASELINE WANDER REMOVED\n");

//		try {
//			FileHandler.saveData(bwData);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		System.out.println("LINEARLY FITTING DATA");
		VoltageRecords lfData = LinearFitting.fit(bwData);
		System.out.println("DATA LINEARLY FITTED\n");

		System.out.println("REMOVING NOISE");
		VoltageRecords wtData = NoiseCancelling.threeScaleWaveletTransform(lfData);
		System.out.println("NOISE REMOVED\n");

//		try {
//			FileHandler.saveData(wtData);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		System.out.println("SEGMENTING DATA");
		QRSSegmentation.segmentQRSComplexes(wtData, fileIndex);
		System.out.println("DATA SEGMENTED\n");

		System.out.println("ONE HOT ENCODING");
		try {
			BinaryRecord[] binRecs = OneHotEncoder.encodeAll(patientInfo, fileIndex);

			for (BinaryRecord rec : binRecs) {
				if (rec != null) {
					FileHandler.saveBinaryData(rec.getName(), rec.getData());
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("ONE HOT ENCODING ENDED");

//		VoltageRecords segData = new VoltageRecords("seg\\Segment16.csv");
//		
//		try {
//			FileHandler.loadData(segData);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		
//		BinaryRecord binRec = OneHotEncoder.encode(segData);
//		
//		try {
//			FileHandler.saveBinaryData(binRec.getName(), binRec.getData());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

}
