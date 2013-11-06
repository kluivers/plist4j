package nl.kluivers.joris.plisttest;

import nl.kluivers.joris.plist.Plist4J;

import junit.framework.*;

import java.util.Map;
import java.util.Hashtable;
import java.util.Date;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

public class XMLPlistTest extends TestCase {
	private Map dictWithValues = null;
	
	public XMLPlistTest(String name) {
		super(name);
	}
	
	public void testDictWithString() {
		Map plist = null;
		
		try {
			plist = (Map) Plist4J.plistFromData(
				new FileInputStream(new File("tests/XMLDictWithString.plist"))
			);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			fail("Error reading tests/XMLDictWithString.plist");
		}
		
		assertFalse("Result plist is null", plist == null);
		
		String value = (String) plist.get("Title");
		
		assertEquals("Plist title value", "Test Title", value);
	}
	
	public void testCreatePlist() {
		Map plist = new Hashtable();
		
		Date now = new Date();
		
		plist.put("User", "Joris");
		plist.put("Test Date", now);
		
		String data = Plist4J.dataFromPlist(plist);
		//System.out.println(data);
		
		Object plistObject = Plist4J.plistFromData(data);
		
		assertTrue("Wrong plist root object title", plistObject instanceof Map);
		
		Map plistMap = (Map) plistObject;
		
		assertEquals("Plist string value", "Joris", (String) plistMap.get("User"));
		
		// test by string to prevent milliseconds
		assertEquals("Plist date value", now.toString(), plistMap.get("Test Date").toString());
	}
}