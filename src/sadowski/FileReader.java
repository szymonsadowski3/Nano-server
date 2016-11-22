package sadowski;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Class used to read something from text file
 * @author szymon
 *
 */
public class FileReader {
	/**
	 * This method reads file line by line and returns array of these lines
	 * @param path
	 * Relative path to file
	 * @return
	 * Array which contains different lines read form file at different elements of Array
	 * @throws IOException
	 */
	static ArrayList<String> readLines(String path) throws IOException {
		ArrayList<String> linesList = new ArrayList<String>();

		InputStream is = new FileInputStream(path);
		Reader r = new InputStreamReader(is, "UTF-8");
		BufferedReader br = new BufferedReader(r);
		String line;

		while ((line = br.readLine()) != null)
			linesList.add(line);
		
		br.close();
		
		return linesList;
	}
	
	static boolean fileExists(String path) {
		File f = new File(path);
		if(f.exists() && !f.isDirectory()) { 
		    return true;
		}
		return false;
	}
}
