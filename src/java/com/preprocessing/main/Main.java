package com.preprocessing.main;

import java.io.IOException;

import com.preprocessing.data.VoltageRecords;
import com.preprocessing.file.FileHandler;
import com.preprocessing.methods.BaselineWanderRemoval;

public class Main {

	public static void main(String[] args) {
		VoltageRecords sampleData = new VoltageRecords("SampleData.csv");
		
		try {
			FileHandler.loadData(sampleData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		VoltageRecords bwData = BaselineWanderRemoval.removeBaselineWander(sampleData, 512f, 5f);
		
		try {
			FileHandler.saveData(bwData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
