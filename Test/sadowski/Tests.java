package sadowski;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class Tests {

	@Test
	public void test1() {
		String expected = "index.html";
		
		ArrayList<String> lines = new ArrayList<String>();
		SimpleServer s = new SimpleServer();
		
		lines.add("GET /index.html HTTP/1.1");
		String result = s.getClientRequestResource(lines);
		assertEquals(expected, result);
	}
	
	@Test
	public void test2() {
		assertFalse(FileReader.fileExists("abasfasfasf.asfgasgsg"));
	}
	
	@Test
	public void test3() {
		assertFalse(FileReader.fileExists("/Test"));
	}
	
	@Test
	public void test4() {
		assertEquals("index.html", Util.removeNFirstCharacters("/index.html", 1));
	}
	
	@Test
	public void test5() {
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("abc");
		lines.add("def");
		lines.add("ghi");
		String expected = "abcdefghi";
		String result = Util.mergeArrayListOfStrings(lines);
		assertEquals(expected, result);
	}
}
