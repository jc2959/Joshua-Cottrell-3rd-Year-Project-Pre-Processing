package com.preprocessing.main;

import java.io.IOException;

import com.preprocessing.data.VoltageRecords;
import com.preprocessing.file.FileHandler;
import com.preprocessing.methods.BaselineWanderRemoval;
import com.preprocessing.methods.LinearFitting;
import com.preprocessing.methods.NoiseCancelling;
import com.preprocessing.methods.QRSSegmentation;

public class Main {

	public static void main(String[] args) {
		VoltageRecords sampleData = new VoltageRecords("data.csv");
		
		System.out.println("LOADING DATA");
		try {
			FileHandler.loadData(sampleData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("DATA LOADED\n");
		
		/*
		 * VoltageRecords iarData =
		 * ImpulsiveArtefactsRemoval.removeImpulsiveArtefacts(1f / 256f, 0.06f,
		 * sampleData);
		 * 
		 * try { FileHandler.saveData(iarData); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */
		
		System.out.println("PERFORMING BASELINE WANDER REMOVAL");
		VoltageRecords bwData = BaselineWanderRemoval.removeBaselineWander(sampleData, 256f, 5f);
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
		QRSSegmentation.segmentQRSComplexes(wtData);
		System.out.println("DATA SEGMENTED\n");
		
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
