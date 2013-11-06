package nl.kluivers.joris.plist;

import java.io.InputStream;

interface PlistSerializer {
	/**
	 * Serializes a plist object into a data string.
	 */
	public String dataFromPlist(Object plist);
	public String dataFromPlist(int i);
	public String dataFromPlist(double d);
	public String dataFromPlist(boolean b);
	
	/**
	 * Creates a plist object from a data input.
	 */
	public Object plistFromData(InputStream input);
}