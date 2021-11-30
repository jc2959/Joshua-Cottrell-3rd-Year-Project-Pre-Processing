package com.preprocessing.data;

public class BinaryRecord {

	private String name;
	private float[][] onehotMatrix;
	
	public BinaryRecord(String name, int width, int height) {
		onehotMatrix = new float[height][width];
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setOnehotMatrix(float[][] onehotMatrix) {
		this.onehotMatrix = onehotMatrix;
	}
	
	public byte[] getData() {
		int width = onehotMatrix[0].length;
		int height = onehotMatrix.length;
		
		byte[] data = new byte[width * height / 8];
		int index = 0;
		
		byte currentByte = 0;
		int byteIndex = 0;
		
		for (int x = 0; x < onehotMatrix[0].length; x++) {
			for (int y = 0; y < onehotMatrix.length; y++) {
				if (byteIndex >= 8) {
					data[index] = currentByte;
					currentByte = 0;
					byteIndex = 0;
					index += 1;
				}
				
				if (onehotMatrix[y][x] == 1) {
					int val = 1 << (8 - byteIndex);
					
					currentByte += val;
				}
				
				byteIndex += 1;
			}
			
		}

		return data;
	}
	
}
