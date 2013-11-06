package nl.kluivers.joris.plist;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * <p>A utility class to serialize objects to the plist data format as 
 * used by the Apple. Plist files exist in three flavours: ASCII, XML
 * and Binary. The ASCII format was originally uses by NeXT in NeXTSTEP, 
 * on which OS X is based. This format was replaced by Apple by a XML 
 * format. Since OS X version 10.4 the default serialization type for plist
 * files is a Binary type.</p>
 *
 * <p>More about plist files can be found on wikipedia.org:<br/>
 * <a href="http://en.wikipedia.org/wiki/Plist">Wikipedia Plist entry</a></p>
 *
 * <h3>Plist4J serialization types:</h3>
 * <dl>
 *		<dt><strong>XML</strong></dt>
 *		<dd>Currently serializes {@link java.lang.String}s, {@link java.lang.Double}s, 
 *		{@link java.lang.Integer}s, {@link java.util.Date}s, {@link java.util.Boolean}s, 
 * 	{@link java.util.List}s and {@link java.util.Map}s. The only data type not 
 *		supported yet is the <code>&lt;data&gt;</code> tag.</dd>
 *		<dt><strong>ASCII</strong></dt>
 *		<dd>Objects can be serialized into plist data. Deserialization doesn't work yet.</dd>
 *		<dt><strong>Binary</strong></dt>
 *		<dd>Not supported yet</dd>
 *	</dl>
 */
public class Plist4J {
	/**
	 * Plist ascii serialization type.
	 */
	public static final int PLIST_ASCII = 1;
	
	/**
	 * Plist xml serialization type.
	 */
	public static final int PLIST_XML = 2;
	
	public static final int PLIST_BINARY = 3;
	
	/**
	 * The default serialization type, currently PLIST_XML
	 *
	 * @see Plist4J#PLIST_XML
	 */
	public static final int PLIST_DEFAULT_TYPE = PLIST_XML;
	
	private PlistSerializer xmlPlistSerializer = null;
	private PlistSerializer asciiPlistSerializer = null;
	private PlistSerializer binaryPlistSerializer = null;
	
	private Plist4J() {
		xmlPlistSerializer = new XMLPlistSerializer();
		asciiPlistSerializer = new ASCIIPlistSerializer();
		binaryPlistSerializer = new BinaryPlistSerializer();
	}
	
	private static Plist4J instance = null;
	private static Plist4J getInstance() {
		if (instance == null) {
			instance = new Plist4J();
		}
		
		return instance;
	}
	
	/**
	 * Converts a plist object to serialized plist data in the default type.
	 *
	 * @see #dataFromPlist(int, Object)
	 * @see Plist4J#PLIST_DEFAULT_TYPE
	 */
	public static String dataFromPlist(Object plist) {
		return dataFromPlist(PLIST_DEFAULT_TYPE, plist);
	}
	
	/**
	 * Converts a plist object to serialized plist data in the specified type.
	 *
	 * @param	type	The plist type
	 * @param	plist	The plist object
	 * @see	Plist4J#PLIST_ASCII
	 * @see	Plist4J#PLIST_XML
	 * @see	Plist4J#PLIST_BINARY
	 */
	public static String dataFromPlist(int type, Object plist) {
		return getInstance().deferDataFromPlist(type, plist);
	}
	
	private String deferDataFromPlist(int type, Object plist) {
		return getSerializer(type).dataFromPlist(plist);
	}
	
	/**
	 * Serializes a primitive type int to plist data. This convenience method converts 
	 * <code>i</code> to an Integer before calling {@link #dataFromPlist(int, Object)} 
	 * using the default plist serialization type.
	 * 
	 * @see #PLIST_DEFAULT_TYPE
	 */
	public static String dataFromPlist(int i) {
		return dataFromPlist(PLIST_DEFAULT_TYPE, i);
	}
	
	/**
	 * Serializes a primitive type int to plist data. This is a convenience method 
	 * that converts <code>i</code> to an Integer object before calling 
	 * {@link #dataFromPlist(int, Object)} using the specified plist type.
	 *
	 * @see #PLIST_ASCII
	 * @see #PLIST_XML
	 * @see #PLIST_BINARY
	 */
	public static String dataFromPlist(int type, int i) {
		return getInstance().deferDataFromPlist(type, i);
	}
	
	/**
	 * Defers the static call to Plist4J to the plist serializer
	 * of the specified type
	 */
	private String deferDataFromPlist(int type, int i) {
		return getSerializer(type).dataFromPlist(i);
	}
	
	/**
	 * Serializes a primitive type double to plist data. This is a convenience method
	 * that converts <code>d</code> to a Double object before calling
	 * {@link #dataFromPlist(int, Object)} using the default plist type.
	 *
	 * @param	d	The double to serialize to plist data
	 * @see	#PLIST_DEFAULT_TYPE
	 */
	public static String dataFromPlist(double d) {
		return dataFromPlist(PLIST_DEFAULT_TYPE, d);
	}

	/**
	 * Serializes a primitive type doulbe to plist data. This is a convenience method
	 * that converts <code>d</code> to a Double object before calling
	 * {@link #dataFromPlist(int, Object)} using the specified plist type.
	 *
	 * @param	type	The type to serialize the plist to.
	 * @param	d	The double to serialize
	 * @see #PLIST_ASCII
	 * @see #PLIST_XML
 	 * @see #PLIST_BINARY
 	 */
	public static String dataFromPlist(int type, double d) {
		return getInstance().deferDataFromPlist(type, d);
	}

	private String deferDataFromPlist(int type, double d) {
		return getSerializer(type).dataFromPlist(d);
	}
	
	public static String dataFromPlist(boolean b) {
		return dataFromPlist(PLIST_DEFAULT_TYPE, b);
	}

	public static String dataFromPlist(int type, boolean b) {
		return getInstance().deferDataFromPlist(type, b);
	}

	private String deferDataFromPlist(int type, boolean b) {
		return getSerializer(type).dataFromPlist(b);
	}
	
	public static Object plistFromData(String data) {
		return plistFromData(PLIST_DEFAULT_TYPE, data);
	}
	
	public static Object plistFromData(int type, String data) {
		return plistFromData(type, new ByteArrayInputStream(data.getBytes()));
	}
	
	public static Object plistFromData(InputStream input) {
		// TODO: add auto check
		return plistFromData(PLIST_DEFAULT_TYPE, input);
	}
	
	public static Object plistFromData(int type, InputStream input) {
		return getInstance().deferPlistFromData(type, input);
	}
	
	private Object deferPlistFromData(int type, InputStream input) {
		return getSerializer(type).plistFromData(input);
	}
	
	private PlistSerializer getSerializer(int type) {
		switch (type) {
			case PLIST_ASCII:
				return asciiPlistSerializer;
			case PLIST_XML:
				return xmlPlistSerializer;
			case PLIST_BINARY:
				return binaryPlistSerializer;
			default:
				return getSerializer(PLIST_DEFAULT_TYPE);
		}
	}
}