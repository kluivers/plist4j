package nl.kluivers.joris.plisttest;

import nl.kluivers.joris.plist.Plist4J;

import junit.framework.*;

import java.util.Map;
import java.util.Hashtable;
import java.util.Date;
import java.util.List;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

public class ASCIIPlistTest extends TestCase {
	public ASCIIPlistTest(String name) {
		super(name);
	}
	
	public void testCreateAsciiPlist() {
		Date now = new Date();
		
		Map plist = new Hashtable();
		plist.put("Name", "Joris Kluivers");
		plist.put("TestDate", now);
		
		String asciiData = Plist4J.dataFromPlist(Plist4J.PLIST_ASCII, plist);
		
		Object plistObject = Plist4J.plistFromData(Plist4J.PLIST_ASCII, asciiData);
		
		assertTrue("Plist root object", plistObject instanceof Map);
		
		Map plistMap = (Map) plistObject;
		
		assertEquals("Value for key Name", "Joris Kluivers", plistMap.get("Name"));
		assertEquals("Value for key Date", now.toString(), plistMap.get("TestDate").toString());
	}
	
	public void testReadAsciiDictWithString() {
		Map plist = null;
		
		try {
			plist = (Map) Plist4J.plistFromData(
				Plist4J.PLIST_ASCII,
				new FileInputStream(new File("tests/ASCIIDictWithString.plist"))
			);
		} catch (Exception e) {
			fail("Error reading test/ASCIIDictWithString.plist");
		}
		
		assertNotNull("Plist root object is null", plist);
		assertEquals("Value for key", "Joris Kluivers", plist.get("Name"));
	}
	
	public void testReadAsciiDictWithStringAndArray() {
		Map plist = null;
		
		try {
			plist = (Map) Plist4J.plistFromData(
				Plist4J.PLIST_ASCII,
				new FileInputStream(new File("tests/ASCIIDictWithStringAndArray.plist"))
			);
		} catch (Exception e) {
			fail("Error reading tests/ASCIIDictWithStringAndArray.plist");
		}
		
		assertNotNull("Root map object is null", plist);
		assertEquals("Value for key", "My Bookmarks", plist.get("ListTitle"));
		
		Object listObj = plist.get("ListEntries");
		
		assertTrue("Type of list object", listObj instanceof List);
	}
}