import nl.kluivers.joris.plist.Plist4J;

import java.util.Hashtable;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileWriter;

public class PlistTest {
	public void start() {
		serializeTest();
		//binaryReadTest();
	}
	
	public void serializeTest() {
		Map prefs = new Hashtable();
		prefs.put("Username", "joris");
		
		List cals = new ArrayList();
		cals.add("Home");
		cals.add("Rowing");
		cals.add("Study");
		cals.add("Study Appointments");
		
		prefs.put("Calendars", cals);
		prefs.put("FirstRun", new Date());
		
		String prefXML = Plist4J.dataFromPlist(prefs);
		try {
			FileWriter w = new FileWriter(new File("prefs.plist"));
			w.write(prefXML, 0, prefXML.length());
			w.flush();
		} catch (Exception e) {}
		
		System.out.println(prefXML);
		System.out.println(Plist4J.dataFromPlist(Plist4J.PLIST_ASCII, prefs));
		
		Map plist = (Map) Plist4J.plistFromData(prefXML);
		System.out.println(plist.get("FirstRun"));
	}
	
	public void binaryReadTest() {
		InputStream fileInput = null;
		
		try {
			fileInput = new FileInputStream(new File("asbinary.plist"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (fileInput == null) {
			return;
		}
		
		Object obj = Plist4J.plistFromData(Plist4J.PLIST_BINARY, fileInput);
	}
	
	public static void main(String[] argv) {
		new PlistTest().start();
	}
}