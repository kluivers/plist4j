package nl.kluivers.joris.util;

/**
 * Wrapper object for a byte array. This class tries to imitate the 
 * Cocoa NSData class.
 */
public class DataObject {
	private byte[] bytes;
	
	public DataObject() {
		bytes = new byte[0];
	}
	
	public DataObject(byte[] contents) {
		this(contents, true);
	}
	
	public DataObject(byte[] contents, boolean copy) {
		if (!copy) {
			bytes = contents;
		} else {
			bytes = new byte[contents.length];
			System.arraycopy(contents, 0, bytes, 0, contents.length);
		}
	}
	
	/**
	 * Gets a reference to the internal bytes array.
	 */
	public byte[] getBytes() {
		return bytes;
	}
	
	/**
	 * Copies the internal data buffer into the parameter <code>destination</code>. If
	 * copy is smaller then the internal data buffer only the range 0 to <code>destination.length</code>
	 * will be copied from the internal data buffer to the destination
	 */
	public void getBytes(byte[] destination) {
		System.arraycopy(bytes, 0, destination, 0, (destination.length > bytes.length) ? bytes.length : destination.length);
	}
	
	public void getBytes(byte[] destination, int length) {
		
	}
	
	public void getBytes(byte[] destination, int from, int to) {
		
	}
	
	public DataObject getSubdata(int from, int to) {
		if (to < from) {
			return null;
		}
		
		byte[] subdata = new byte[to - from];
		System.arraycopy(bytes, from, subdata, 0, to - from);
		
		return new DataObject(subdata);
	}
	
	public int length() {
		return bytes.length;
	}
}