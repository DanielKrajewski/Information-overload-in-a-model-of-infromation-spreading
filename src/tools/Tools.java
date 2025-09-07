package tools;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Tools {
	//methods 
	//convert array into int[]
	public static int[] ArrayToInts(ArrayList<Integer> list) {
		int[] array = new int[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}
	public static int[] ArrayToInts(ArrayList<Integer> list,int nElements) {
		if(nElements <= 0)
			throw new Error("Number of elements in converthg ArrayList into array must be greater than zero.");
		int[] array = new int[nElements];
		for(int i=0; i<nElements; i++)
			array[i] = list.get(i);
		return array;
	}
	public static double length(int[] array) {
		double length = 0;
		for(int element : array)
			length += element * element;
		return Math.sqrt(length);
	}
	
	
	//cosine similarity
	public static double cosineSimilarity(int[] array1, int[] array2) {
		if (array1.length != array2.length)
			throw new Error("Arrays must have the same length.");
		double dotProduct = 0;
		double length1 = 0;
		double length2 = 0;
		for (int i = 0; i < array1.length; i++) {
			dotProduct += array1[i] * array2[i];
			length1 += array1[i] * array1[i];
			length2 += array2[i] * array2[i];
		}
		return dotProduct / (Math.sqrt(length1) * Math.sqrt(length2));
	}
	
	
	// Converts double to the double with given precision
		public static double convertToDouble(double number, int precision) {
			double temp = number;
			for(int i=0; i<precision; i++)
				temp *= 10;
			temp = Math.round(temp);
			for(int i=0; i<precision; i++)
				temp /= 10;
			return temp;
		}
		
		// Converts double to double with precision 2
		public static double convertToDouble(double number) {return convertToDouble(number, 2);}
		
		public static String convertToString(double number, int precision) {
			String temp = "";
			for(int i=0; i<precision; i++) temp += "0";
			if(precision != 0) return new DecimalFormat("#0." + temp).format(number);
			else return new DecimalFormat("#0" + temp).format(number);
		}
		
		// Converts double to string with two decimal places
		public static String convertToString(double number) {return convertToString(number, 2);}
}
