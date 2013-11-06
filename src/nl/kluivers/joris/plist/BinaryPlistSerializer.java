package nl.kluivers.joris.plist;

import java.io.InputStream;
import java.io.IOException;
import java.io.DataInputStream;

import java.math.BigInteger;
import java.math.BigDecimal;

class BinaryPlistSerializer implements PlistSerializer {
	public String dataFromPlist(Object plist) {
		return null;
	}
	
	public String dataFromPlist(int i) {
		return null;
	}
	
	public String dataFromPlist(double d) {
		return null;
	}
	
	public String dataFromPlist(boolean b) {
		return null;
	}
	
	private short BYTE_NULL = 0x00;
	private short BYTE_TRUE = 0x08;
	private short BYTE_FALSE = 0x09;
	private short BYTE_INT = 0x10;
	private short BYTE_REAL = 0x20;
	private short BYTE_DATE = 0x33;
	private short BYTE_FILL = 0x0F;
	private short BYTE_ASCII = 0x50;
	private short BYTE_UNIC = 0x60;
	private short BYTE_DICT = 0xD0;
	private short BYTE_ARRAY = 0xA0;
	
	private DataInputStream in = null;
	
	public Object plistFromData(InputStream input) {
		in = new DataInputStream(input);
		
		try {
			byte[] header = new byte[6];
			in.read(header);
			
			if (!new String(header).equals("bplist")) {
				return null;
			}
			
			//System.out.println("Version: " + in.readInt());
			in.skip(2);
			
			//while (in.available() > 0) {
			readObject();// }
			
			short b;
			for (int i=0; i<60; i++) {
				b = in.readByte();
				System.out.println("" + b);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}
	
	private void readObject() throws IOException {
		short b = in.readByte();

		/*for (int i=0; i<40; i++) {
			System.out.println(b);
			b = in.readByte();
		}*/

		if (b == BYTE_NULL) {
			System.out.println("NULL");
		} else if (b == BYTE_FALSE) {
			System.out.println("FALSE");
		} else if (b == BYTE_TRUE) {
			System.out.println("TRUE");
		} else if (b == BYTE_FILL) {
			System.out.println("FILL");
		} else if ((b & 0xF0) == BYTE_INT) {
			readInt(b);
		} else if ((b & 0xF0) == BYTE_REAL) {
			readReal(b);
		} else if (b == BYTE_DATE) {
			System.out.println("Date: " + in.readFloat());
		} else if ((b & 0xF0) == BYTE_ASCII) {
			readASCIIString(b);
		} else if ((b & 0xF0) == BYTE_ARRAY) {
			readArray(b);
		} else if ((b & 0xF0) == BYTE_DICT) {
			readDict(b);
		} else if ((b & 0xF0) == 0x70) {  } 
		else if ((b & 0xF0) == 0x90) {  } 
		else if ((b & 0xF0) == 0xB0) {  } 
		else if ((b & 0xF0) == 0xC0) {  } 
		else if ((b & 0xF0) == 0xE0) {  } 
		else if ((b & 0xF0) == 0xF0) {  }
	}
	
	private int readInt(short marker) throws IOException {
		int length = marker & 0x0F;
		length = (int) Math.pow((double)2, (double)length);
		
		byte[] bigInt = new byte[length];
		BigInteger result;
		
		if (length != 0) {
			in.read(bigInt);
		
			result = new BigInteger(bigInt);
		} else {
			System.out.println("Zero length int");
			return 0;
		}
		
		//System.out.println("Int: " + result.toString());
		
		return result.intValue();
	}
	
	private void readReal(short marker) throws IOException {
		int length = marker & 0x0F;
		length = (int)Math.pow((double)2, (double)length);
		
		byte[] bigDec = new byte[length];
		in.read(bigDec);
		
		//System.out.println("Real: " + new BigDecimal(bigDec).toString());
	}
	
	private void readASCIIString(short marker) throws IOException {
		int length = marker & (byte) 0x0F;
		if (length == 0x0F) {
			length = readInt(in.readByte());
		}
		
		System.out.print("String(" + length + "): ");
		
		byte[] text = new byte[length];
		System.out.println(new String(text));
	}
	
	private void readArray(short marker) throws IOException {
		System.out.println("reading array");
		
		int length = marker & (byte) 0x0F;
		if (length == 0x0F) {
			length = readInt(in.readByte());
		}
		
		System.out.println("Array(" + length + ")");
		
		for (int i=0; i<length; i++) {
			readObject();
		}
	}
	
	private void readDict(short marker) throws IOException {
		int length = marker & 0x0F;
		if (length == 0x0F) {
			length = readInt(in.readByte());
		}
		
		System.out.println("Dict(" + length + ")");
	}
}