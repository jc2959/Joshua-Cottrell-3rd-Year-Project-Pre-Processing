package com.preprocessing.file;

public interface Saveable {

	public String getFilename();
	public String getData();
	
	public void loadData(String fileData);
	
	public void setAmountOfData(int amount);
	
	public void addLine(int index, String line);
	
}
