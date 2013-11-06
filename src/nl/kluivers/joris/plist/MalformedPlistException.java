package nl.kluivers.joris.plist;

public class MalformedPlistException extends Exception {
	public MalformedPlistException() {
		this(null);
	}
	
	/**
	 * Constructs a new <code>MalformedPlistException</code> with 
	 * the specified detail message.
	 */
	public MalformedPlistException(String reason) {
		super(reason);
	}
}