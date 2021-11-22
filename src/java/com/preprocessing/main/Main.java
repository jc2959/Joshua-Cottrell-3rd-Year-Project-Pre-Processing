package com.preprocessing.main;

import java.io.IOException;

import com.preprocessing.file.FileHandler;
import com.preprocessing.util.Records;

public class Main {

	public static void main(String[] args) {
		Records sampleData = new Records("SampleData.csv");
		
		try {
			FileHandler.loadData(sampleData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
