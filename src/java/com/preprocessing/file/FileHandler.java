package com.preprocessing.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {

	public static void loadData(Saveable s) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File("src\\resources\\" + s.getFilename())));
		
		String lines = "";
		
		String line = reader.readLine();
		
		while (line != null && line != "") {
			lines += line + "\n";
			
			line = reader.readLine();
		}
		
		reader.close();
		
		s.loadData(lines);
	}
	
	public static void saveData(Saveable s) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("src\\resources\\" + s.getFilename())));
		writer.write(s.getData());
		writer.close();
	}
	
}
