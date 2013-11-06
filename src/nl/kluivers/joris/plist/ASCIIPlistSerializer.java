package nl.kluivers.joris.plist;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Date;
import java.util.Hashtable;
import java.util.ArrayList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import nl.kluivers.joris.util.DataObject;

/*
 TODO: add support for reading gnustep plists
 http://www.gnustep.org/resources/documentation/Developer/Base/Reference/NSPropertyList.html
*/

class ASCIIPlistSerializer implements PlistSerializer {
	private StringWriter w = null;
	private DateFormat format = null;
	
	public ASCIIPlistSerializer() {
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	}
	
	public String dataFromPlist(Object plist) {
		w = new StringWriter();
		
		serializeObject(plist);
		
		w.flush();
		
		return w.toString();
	}
	
	private void serializeObject(Object obj) {
		// we have a container object
		if (obj instanceof List) {
			serializeObject((List) obj);
		} else if (obj instanceof Map) {
			serializeObject((Map) obj);
		}
		// other objects
		else if (obj instanceof String) {
			serializeObject((String) obj);
		} else if (obj instanceof Integer) {
			serializeObject((Integer) obj);
		} else if (obj instanceof Double) {
			serializeObject((Double) obj);
		} else if (obj instanceof Boolean) {
			serializeObject((Boolean) obj);
		} else if (obj instanceof Date) {
			serializeObject((Date) obj);
		}
	}
	
	private void serializeObject(List list) {
		w.write("(\n");
		
		int item = 0;
		Iterator it = list.iterator();
		while (it.hasNext()) {
			if (item > 0) {
				w.write(",\n");
			}
			serializeObject(it.next());
			item++;
		}
		
		w.write("\n)");
	}
	
	private void serializeObject(Map map) {
		w.write("{\n");
		
		Iterator it = map.keySet().iterator();
		String key;
		while (it.hasNext()) {
			key = (String) it.next();
			serializeObject(key);
			w.write(" = ");
			serializeObject(map.get(key));
			w.write(";\n");
		}
		
		w.write("}");
	}
	
	private void serializeObject(String s) {
		if (s.indexOf(' ') >= 0) {
			w.write("\"");
			w.write(s);
			w.write("\"");
		} else {
			w.write(s);
		}
	}
	
	private void serializeObject(Integer i) {
		w.write(i.toString());
	}
	
	private void serializeObject(Double d) {
		w.write(d.toString());
	}
	
	private void serializeObject(Boolean b) {
		if (b.booleanValue()) {
			w.write("1");
		} else {
			w.write("0");
		}
	}
	
	private void serializeObject(Date d) {
		w.write(format.format(d));
	}
	
	public String dataFromPlist(int i) {
		return new Integer(i).toString();
	}
	
	public String dataFromPlist(double d) {
		return new Double(d).toString();
	}
	
	public String dataFromPlist(boolean b) {
		return new Boolean(b).toString();
	}
	
	public Object plistFromData(InputStream input) {
		ASCIIPlistReader reader = new ASCIIPlistReader(input);
		try {
			return reader.parse();
		} catch (Exception e) {
			return null;
		}
	}
	
	private class ASCIIPlistReader {
		private int PLIST_ARRAY  = 0;
		private int PLIST_DICT   = 1;
		private int PLIST_DATA   = 2;
		private int PLIST_STRING = 3;
		
		private char[] OBJECT_START = {'(', '{', '<', '"'};
		private char[] OBJECT_END   = {')', '}', '>', '"'};
		
		private BufferedReader reader;
		
		public ASCIIPlistReader(InputStream input) {
			reader = new BufferedReader(new InputStreamReader(input));
		}
		
		public Object parse() throws IOException, MalformedPlistException {
			return readMap();
		}
		
		public Object readObject() throws MalformedPlistException, IOException {
			skipSpace();
			
			char c = nextChar();
			
			if (c == OBJECT_START[PLIST_ARRAY]) {
				return readArray();
			} else if (c == OBJECT_START[PLIST_DICT]) {
				return readMap();
			} else if (c == OBJECT_START[PLIST_DATA]) {
				return readData();
			} else if (c == OBJECT_START[PLIST_STRING]) {
				return readString();
			} else if (Character.isLetterOrDigit(c)) {
				return readTextValue(); // String, Date or Number
			} else {
				throw new MalformedPlistException("Unexpected object found");
			}
		}
		
		private Map readMap() throws MalformedPlistException, IOException {
			Map m = new Hashtable();
			
			skipSpace();
			
			if (nextChar() != OBJECT_START[PLIST_DICT]) {
				throw new MalformedPlistException("Expected dicionary start character '{'");
			}
			reader.skip(1);
			
			skipSpace();
			
			while (nextChar() != OBJECT_END[PLIST_DICT]) {
				String key = readString();
			
				// read key value sepparator
				skipSpace();
				if (nextChar() != '=') {
					throw new MalformedPlistException("Expected dictionary key value sepparator '='");
				}
				reader.skip(1);
				
				skipSpace();
			
				Object value = readObject();
				
				// read dictionary entry sepparator
				skipSpace();
				if (nextChar() != ';') {
					throw new MalformedPlistException("Expected dictionary element sepparator ';'");
				}
				reader.skip(1);
				
				m.put(key, value);
				
				// read up to next key or end character }
				skipSpace();
			}
			
			skipSpace();
			
			if (nextChar() != OBJECT_END[PLIST_DICT]) {
				throw new MalformedPlistException("Expected dictionary end character '}'");
			}
			
			return m;
		}
		
		private List readArray() throws MalformedPlistException, IOException {
			List list = new ArrayList();
			
			skipSpace();
			
			if (nextChar() != OBJECT_START[PLIST_ARRAY]) {
				throw new MalformedPlistException("Expected array start character '('");
			}
			reader.skip(1);
			
			int i = 0;
			while (nextChar() != OBJECT_END[PLIST_ARRAY]) {
				skipSpace();
				
				Object value = readObject();
				list.add(value);
				
				skipSpace();
				
				if (nextChar() != ',') { // no extra array elements
					break;
				}
				reader.skip(1);
			}
			
			skipSpace();
			
			if (nextChar() != OBJECT_END[PLIST_ARRAY]) {
				throw new MalformedPlistException("Expected array end character ')'");
			}
			reader.skip(1);
			
			return list;
		}
		
		private DataObject readData() throws MalformedPlistException, IOException {
			skipSpace();
			
			if (nextChar() != OBJECT_START[PLIST_DATA]) {
				throw new MalformedPlistException("Expected data start character '<'");
			}
			
			StringBuffer hexString = new StringBuffer();
			
			// read binary data
			while (nextChar() != OBJECT_END[PLIST_DATA]) {
				if (Character.isWhitespace(nextChar())) {
					reader.skip(1);
				}
				
				if (!isHex(nextChar())) {
					throw new MalformedPlistException("Expected hexadecimal data only in data element");
				}
				
				hexString.append((char)reader.read());
			}
			
			if (nextChar() != OBJECT_END[PLIST_DATA]) {
				throw new MalformedPlistException("Expected data end character '>'");
			}
			
			byte[] bytes = new byte[hexString.length() / 2];
			for (int i=0; i<bytes.length; i++) {
				bytes[i] = (byte) Integer.parseInt(hexString.substring(2*i, 2*i+2), 16);
			}
			
			return new DataObject(bytes);
		}
		
		private Object readTextValue() throws IOException, MalformedPlistException {
			StringBuffer textValue = new StringBuffer();
			
			String acceptableChars = ".:+- ";
			
			char prevChar = '\0';
			while (Character.isLetterOrDigit(nextChar()) || acceptableChars.indexOf(nextChar()) >= 0) {
				if (prevChar == ' ' && nextChar() == ' ') { // no more then two spaces
					break;
				}
				
				prevChar = (char) reader.read();
				textValue.append(prevChar);
			}
			
			try {
				return ASCIIPlistSerializer.this.format.parse(textValue.toString());
			} catch (Exception e) {}
			
			try {
				return new Double(textValue.toString());
			} catch (Exception e) {}
			
			try {
				return new Integer(textValue.toString());
			} catch (Exception e) {}
			
			throw new MalformedPlistException("Unknown value encountered in plist");
		}
		
		private String readString() throws IOException, MalformedPlistException {
			skipSpace();
			
			StringBuffer characters = new StringBuffer();
			
			if (nextChar() == OBJECT_START[PLIST_STRING]) {
				reader.skip(1);
				
				boolean escape = false;
				
				while (nextChar() != '"' || (nextChar() == '"' && escape)) {
					if (nextChar() == '\\' && !escape) {
						escape = true;
						reader.skip(1);
					}
					
					escape = false;
					characters.append((char)reader.read());
				}
				
				if (nextChar() != OBJECT_END[PLIST_STRING]) {
					throw new MalformedPlistException("Expected string end character '\"'");
				}
				reader.skip(1);
			} else if (Character.isLetter(nextChar())) {
				while (!Character.isWhitespace(nextChar())) {
					characters.append((char)reader.read());
				}
			} else {
				throw new MalformedPlistException("Expected string start character '\"' or character data");
			}
			
			return characters.toString();
		}
		
		private void skipSpace() throws IOException {
			while (Character.isWhitespace(nextChar())) {
				reader.skip(1);
			}
		}
		
		private char nextChar() throws IOException {
			reader.mark(1);
			char c = (char) reader.read();
			reader.reset();
			return c;
		}
		
		private boolean isHex(char c) {
			return (Character.isDigit(c) || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F'));
		}
	}
}