package sadowski;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Util {
	public static void printArrayList(ArrayList<String> list) {
		System.out.println(Arrays.toString(list.toArray(new String[0])));
	}

	public static String removeNFirstCharacters(String input, int n) {
		return input.substring(n);
	}

	public static String mergeArrayListOfStrings(ArrayList<String> lines) {
		String merged = "";
		for (String s : lines)
			merged += s;
		return merged;
	}

	/**
	 * This method gets User input and Parses it to integer
	 * 
	 * @param message
	 *            Message displayed before acquiring User's input
	 * @return integer typed in by user
	 */
	public static int getUserInteger(String message) {
		System.out.println(message);
		Scanner scan = new Scanner(System.in);
		while (!scan.hasNextInt()) {
			System.out.println(message);
			scan.next();
		}
		int input = scan.nextInt();
		scan.close();

		return input;
	}

	public static String getResourceExtension(String path) {
		if(path.equals(""))
			return "html";
		String[] splitted = splitByDots(path);
		String ext = splitted[splitted.length - 1];
		return ext;
	}

	public static String[] splitByDots(String input) {
		return input.split("\\.");
	}

	public static boolean checkIfExtensionRefersToImg(String extension) {
		switch (extension) {
		case "png":
		case "jpg":
		case "gif":
		case "bmp":
		case "jpeg":
		case "tiff":
			return true;
		default:
			return false;
		}
	}
	
	public static String processExtension(String ext) {
		if(ext.equals("txt"))
			return "plain";
		else if(ext.equals("jpg"))
			return "jpeg";
		else
			return ext;
	}
}
