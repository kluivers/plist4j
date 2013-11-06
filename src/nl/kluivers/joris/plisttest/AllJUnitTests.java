package nl.kluivers.joris.plisttest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllJUnitTests extends TestCase {
	public AllJUnitTests(String name) {
		super(name);
	}
	
	public static Test suite() {
		TestSuite suit = new TestSuite();
		suit.addTest(new TestSuite(XMLPlistTest.class));
		suit.addTest(new TestSuite(ASCIIPlistTest.class));
		
		return suit;
	}
}