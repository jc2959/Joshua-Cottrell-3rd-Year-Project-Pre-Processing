package com.preprocessing.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

public class FileHandler {

	public static void loadData(Saveable s) throws IOException {
		String path = "src\\resources\\" + s.getFilename();
		File file = new File(path);
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		long lineCount;
		try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
		  lineCount = stream.count();
		}
		
		String line = reader.readLine();
		
		int ctr = 0;
		
		float inc = 0.01f;
		int thng = (int) (lineCount * inc);
		
		s.setAmountOfData((int) lineCount);
		
		System.out.println("0s: 0%");
		
		long ts = System.currentTimeMillis();
		
		while (line != null && line != "") {
			s.addLine(ctr, line);
			
			if ((ctr + 1) % thng == 0) {
				float perc = 100 * inc * (ctr + 1) / thng;
				
				long sec = (System.currentTimeMillis() - ts) / 1000;
				
				System.out.println(sec + "s: " + perc + "%");
			}
			
			ctr += 1;
			line = reader.readLine();
		}
		
		reader.close();
		
	}
	
	public static void saveData(Saveable s) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("src\\resources\\" + s.getFilename())));
		writer.write(s.getData());
		writer.close();
	}
	
	public static void saveBinaryData(String filename, byte[] binaryData) throws IOException {
		File file = new File("src\\resources\\bin\\" + filename);
		file.createNewFile();
		
		FileOutputStream outstream = new FileOutputStream(file);
		outstream.write(binaryData);
		outstream.close();
	}
	
}
