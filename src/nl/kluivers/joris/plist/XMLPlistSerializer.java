package nl.kluivers.joris.plist;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Iterator;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.SimpleTimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.AttributesImpl;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.IOException;
import java.io.ByteArrayInputStream;

import com.megginson.sax.DataWriter;

class XMLPlistSerializer implements PlistSerializer {
	private DataWriter w = null;
	private DateFormat utcFormat = null;
	
	public XMLPlistSerializer() {
		w = new DataWriter();
		w.setIndentStep(4);
		
		// UTC timezone
		utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		utcFormat.setCalendar(new GregorianCalendar(new SimpleTimeZone(0, "GMT")));
	}
	
	public String dataFromPlist(Object plist) {
		StringWriter sw = new StringWriter();
		
		try {
			w.setOutput(sw);
			serializeRootObject(plist);
		} catch (Exception e) {
			return null;
		}
		
		return sw.toString();
	}
	
	public String dataFromPlist(int i) {
		return dataFromPlist(new Integer(i));
	}
	
	public String dataFromPlist(double d)	{
		return dataFromPlist(new Double(d));
	}
	
	public String dataFromPlist(boolean b)		{
		return dataFromPlist(new Boolean(b));
	}
	
	private void serializeRootObject(Object obj) throws SAXException, IOException {
		w.startDocument();
		w.addDTD("plist", "-//Apple Computer//DTD PLIST 1.0//EN", "http://www.apple.com/DTDs/PropertyList-1.0.dtd");

		AttributesImpl attr = new AttributesImpl();
		attr.addAttribute("", "version", "", "", "1.0");
		w.startElement("", "plist", "", attr);

		serializeObject(obj);

		w.endElement("plist");
		w.endDocument();
		w.flush();
	}
	
	private void serializeObject(Object obj) throws SAXException {
		if (obj instanceof List) {
			serializeObject((List) obj);
		} else if (obj instanceof Map) {
			serializeObject((Map) obj);
		} else if (obj instanceof Date) {
			serializeObject((Date) obj);
		} else if (obj instanceof Double) {
			serializeObject((Double) obj);
		} else if (obj instanceof Integer) {
			serializeObject((Integer) obj);
		} else if (obj instanceof String) {
			serializeObject((String) obj);
		} else if (obj instanceof Boolean) {
			serializeObject((Boolean) obj);
		}
	}
	
	private void serializeObject(List l) throws SAXException {
		w.startElement("array");

		Iterator it = l.iterator();
		while (it.hasNext()) {
			serializeObject(it.next());
		}

		w.endElement("array");
	}
	
	private void serializeObject(Map m) throws SAXException {
		w.startElement("dict");

		Iterator keys = m.keySet().iterator();
		Object key;
		while (keys.hasNext()) {
			key = keys.next();
			w.dataElement("key", key.toString());
			serializeObject(m.get(key));
		}

		w.endElement("dict");
	}

	private void serializeObject(Double d) throws SAXException {
		w.dataElement("real", d.toString());
	}

	private void serializeObject(Integer i) throws SAXException {
		w.dataElement("integer", i.toString());
	}

	private void serializeObject(String s) throws SAXException {
		w.dataElement("string", s);
	}

	private void serializeObject(Boolean b) throws SAXException {
		w.emptyElement(b.toString());
	}
	
	/**
	 * Serializes a Date into the corresponding xml. Assumes the 
	 * Date object is in the local timezone.
	 *
	 * @param d	A date in the local timezone
	 */
	private void serializeObject(Date d) throws SAXException {
		w.dataElement("date", utcFormat.format(d));
	}
	
	/**
	 * Creates a plist object from a data input. For each primitive type 
	 * encountered in the data input a wrapper object will be returned, so 
	 * each int will be returned as Integer, etc.
	 *
	 * @return	The root plist object
	 */
	public Object plistFromData(InputStream input) {
		PlistSAXParser handler = new PlistSAXParser();
		
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			//factory.setValidating(false);
			//factory.setFeature("http://xml.org/sax/features/resolve-dtd-uris", false);
			
			SAXParser parser = factory.newSAXParser();
			
			// XMLReader reader = parser.getXMLReader();
			
			// disable dtd lookup
			//reader.setFeature("http://xml.org/sax/features/resolve-dtd-uris", false);
			// reader.setFeature("http://xml.org/sax/features/validation", false);
			
			parser.parse(new InputSource(input), handler);
		} catch (Exception e) {
			return null;
		}
		
		return handler.getRootObject();
	}
	
	
	private class PlistSAXParser extends DefaultHandler {
		private Object plistObj = null;
		private String currentKey = null;
		private StringBuffer charData = null;
		private boolean inElement = false;
		private Stack containers = null;
		
		private DateFormat localFormat = null;
		
		public PlistSAXParser() {
			charData = new StringBuffer();
			containers = new Stack();
			
			localFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			localFormat.setCalendar(new GregorianCalendar(new SimpleTimeZone(0, "GMT")));
		}
		
		public void startElement(String namespaceURI, String localName, String qName, Attributes attr) {
			if ("plist".equals(qName)) {
				
			} else if ("array".equals(qName)) {
				ArrayList list = new ArrayList();
				addToCurrentContainer(list);
				
				containers.push(list);
			} else if ("dict".equals(qName)) {
				Hashtable dict = new Hashtable();
				addToCurrentContainer(dict);
				
				containers.push(dict);
			} else {
				inElement = true;
			}
		}
		
		public void characters(char ch[], int start, int length) {
			if (!inElement) {
				return;
			}
			
			charData.append(ch, start, length);
		}
		
		public void endElement(String namespaceURI, String localName, String qName) {
			if ("plist".equals(qName)) {
				
			} else if ("array".equals(qName) || "dict".equals(qName)) {
				containers.pop();
			} else if ("key".equals(qName)) {
				currentKey = charData.toString().trim();
				inElement = false;
				charData.delete(0, charData.length());
			} else {
				if (inElement) {
					addToCurrentContainer(objectFromXML(qName, charData.toString().trim()));
				}
				inElement = false;
				charData.delete(0, charData.length());
			}
		}
		
		public InputSource resolveEntity(String publicID, String systemID) {
			// this method is used to ignore doctype definitions
			return new InputSource(
				new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8' ?>".getBytes())
			);
		}
		
		/**
		 * After parsing the xml file this method will return the plist root object
		 * @return	The plist root object.
		 */
		public Object getRootObject() {
			return plistObj;
		}
		
		private void addToCurrentContainer(Object obj) {
			if (containers.size() == 0) {
				plistObj = obj;
				return;
			}

			if (containers.peek() instanceof Map) {
				if (currentKey == null) {
					return;
				}
				((Map)containers.peek()).put(currentKey, obj);
			} else if (containers.peek() instanceof List) {
				((List)containers.peek()).add(obj);
			}
		}
		
		private Object objectFromXML(String tagName, String data) {
			if ("string".equals(tagName)) {
				return data;
			} else if ("integer".equals(tagName)) {
				return new Integer(data);
			} else if ("real".equals(tagName)) {
				return new Double(data);
			} else if ("true".equals(tagName)) {
				return Boolean.TRUE;
			} else if ("false".equals(tagName)) {
				return Boolean.FALSE;
			} else if ("date".equals(tagName)) {
				try {
					return localFormat.parse(data);
				} catch (Exception e) {
					return null;
				}
			} /*else if ("data".equals(tagName)) {
				// parse data
			}*/
			
			return null;
		}
	}
}